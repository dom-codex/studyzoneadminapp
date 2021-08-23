package com.sparktech.studyzoneadmin.network

import com.sparktech.studyzoneadmin.models.AuthLogin
import com.sparktech.studyzoneadmin.models.DetailsResponse
import com.sparktech.studyzoneadmin.models.SliderData
import com.sparktech.studyzoneadmin.request_models.NewSchool
import com.sparktech.studyzoneadmin.request_models.SchoolToDelete
import com.sparktech.studyzoneadmin.response_models.*
import retrofit2.http.*

interface ApiService {
    @POST("auth/admin/login")
    suspend fun adminLogin(@Body data: AuthLogin): LoginResponse

    @POST("create/school")
    suspend fun createSchool(@Body newSchool: NewSchool): NewSchoolResponse

    @POST("delete/school")
    suspend fun deleteSchool(@Body schoolToDelete: SchoolToDelete): SchoolDeleteResponse

    @GET("get/stats")
    suspend fun getSliderData(): SliderData

    @GET("get/activity/details?page=0")
    suspend fun getActivityData(): DetailsResponse

    @GET("get/schools")
    suspend fun fetchSchools(@Query("page") page: Int, @Query("type") type: String): SchoolResponse

    @GET("get/school/faculty")
    suspend fun getFaculties(
        @Query("sch") sch: String,
        @Query("adminId") adminId: String,
        @Query("page") page: Int
    ): GetFacultyResponse

    @GET("/get/school/faculty/department")
    suspend fun getDepartment(
        @Query("sch") sch: String, @Query("adminId") adminId: String,
        @Query("fid") fid: String,
        @Query("page") page: Int
    ): GetDepartmentResponse

    @GET("/get/school/faculty/dept/levels")
    suspend fun getDepartmentLevels(@QueryMap options: HashMap<String, String>): GetLevelResponse

    @GET("/get/pastquestions")
    suspend fun getPastQuestions(
        @QueryMap option: HashMap<String, String>,
        @Query("page") page: Int
    ): GetPatQuestionResponse

    @GET("/get/transactions")
    suspend fun getTransactions(
        @Query("adminId") adminId: String,
        @Query("page") page: Int
    ): GetTransactionResponse

    @GET("/get/withdrawal/requests")
    fun getWithdrawalRequests(
        @Query("adminId") adminId: String,
        @Query("page") page: Int
    ): GetWithdrawalRequestResponse

    @GET("/get/users")
    suspend fun getUsers(
        @Query("adminId") adminId: String,
        @Query("page") page: Int
    ): GetUsersResponse

    @GET("/get/user/transactions")
    suspend fun getUserTransactions(
        @Query("page") page: Int,
        @QueryMap options: HashMap<String, String>
    ): GetTransactionResponse

    @GET("/get/user/referrallist")
    suspend fun getUserReferrals(
        @Query("page") page: Int,
        @QueryMap options: HashMap<String, String>
    ): GetReferralResponse

    @GET("/get/user/downloads")
    suspend fun getUserDownloads(
        @Query("page") page: Int,
        @QueryMap options: HashMap<String, String>
    ): GetDownloadsResponse
}
