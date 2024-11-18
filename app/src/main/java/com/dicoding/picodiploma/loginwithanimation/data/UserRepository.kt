package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultData
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.*
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class UserRepository private constructor(
    private val apiService: ApiService,
    private val preference: UserPreference
) {
    private val _listStories = MutableLiveData<List<ListStoryItem>?>()
    val listStories: LiveData<List<ListStoryItem>?> = _listStories

    private val _detail = MutableLiveData<Story?>()
    val detail: LiveData<Story?> = _detail

    fun register(name: String, email: String, password: String) = liveData {
        emit(ResultData.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(ResultData.Success(response.message))
        } catch (e: HttpException) {
            val errorMessage = handleErrorResponse<FailResponse>(e)?.message ?: "Terjadi kesalahan"
            emit(ResultData.Error(errorMessage))
        }
    }

    fun login(email: String, password: String) = liveData {
        emit(ResultData.Loading)
        try {
            val response = apiService.login(email, password)
            emit(ResultData.Success(response.loginResult?.token))
        } catch (e: HttpException) {
            val errorMessage = handleErrorResponse<LoginResponse>(e)?.message ?: "Terjadi kesalahan"
            emit(ResultData.Error(errorMessage))
        }
    }

    fun getAllStories(token: String) {
        apiService.getStories("Bearer $token").enqueue(object : Callback<GetAllStoryResponse> {
            override fun onResponse(call: Call<GetAllStoryResponse>, response: Response<GetAllStoryResponse>) {
                if (response.isSuccessful) {
                    _listStories.value = response.body()?.listStory as List<ListStoryItem>?
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getDetailStory(token: String, id: String) {
        apiService.detailStory("Bearer $token", id).enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(call: Call<DetailStoryResponse>, response: Response<DetailStoryResponse>) {
                if (response.isSuccessful) {
                    _detail.value = response.body()?.story
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun uploadImage(token: String, imageFile: File, description: String) = liveData {
        emit(ResultData.Loading)
        try {
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val imagePart = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                imageFile.asRequestBody("image/jpeg".toMediaType())
            )
            val response = apiService.addStory("Bearer $token", imagePart, requestBody)
            emit(ResultData.Success(response))
        } catch (e: HttpException) {
            val errorMessage = handleErrorResponse<AddNewStoryResponse>(e)?.message ?: "Terjadi kesalahan"
            emit(ResultData.Error(errorMessage))
        }
    }

    fun getSession(): Flow<UserModel> = preference.getSession()

    suspend fun saveSession(user: UserModel) {
        preference.saveSession(user)
    }

    suspend fun logout() {
        preference.logout()
    }

    companion object {
        private const val TAG = "UserRepository"

        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService, pref: UserPreference): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, pref)
            }.also { instance = it }
    }

    // Utility to handle error response parsing
    private inline fun <reified T> handleErrorResponse(e: HttpException): T? {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            Gson().fromJson(errorBody, T::class.java)
        } catch (ex: Exception) {
            null
        }
    }
}
