package com.sparktech.studyzoneadmin.network

import com.sparktech.studyzoneadmin.models.AuthLogin
import com.sparktech.studyzoneadmin.models.DetailsResponse
import com.sparktech.studyzoneadmin.models.SliderData
import com.sparktech.studyzoneadmin.request_models.*
import com.sparktech.studyzoneadmin.request_models.NewSchool
import com.sparktech.studyzoneadmin.response_models.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    @POST("/auth/admin/login")
    suspend fun adminLogin(@Body data: AuthLogin): LoginResponse

    @POST("/create/school")
    suspend fun createSchool(@Body newSchool: NewSchool): NewSchoolResponse

    @POST("/delete/school")
    suspend fun deleteSchool(@Body schoolToDelete: SchoolToDelete): SchoolDeleteResponse

    @GET("/get/stats")
    suspend fun getSliderData(): SliderData

    @GET("/get/activity/details?page=0")
    suspend fun getActivityData(): DetailsResponse

    @GET("/get/schools")
    suspend fun fetchSchools(@Query("page") page: Int, @Query("type") type: String): SchoolResponse

    @GET("/get/school/faculty")
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
        @Query("filter") filter: String,
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
        @Query("category") cat: String,
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

    @Multipart
    @POST("/create/pq")
    suspend fun uploadPastQuestion(
        @Part pqPart: MultipartBody.Part,
        @Part dataPart: MultipartBody.Part
    ): CreatePastQuestionResponseBody

    @POST("/delete/pastquestion")
    suspend fun deletePastQuestion(@Body body: DefaultRequestBody): DefaultNetworkResponse

    @POST("/create/level")
    suspend fun createLevel(@Body body: CreateLevelRequestBody): CreateLevelResponseBody

    @POST("/create/department")
    suspend fun createDepartment(@Body rb: CreateDepartmentRequestBody): CreateDepartmentResponseBody

    @POST("/create/faculty")
    suspend fun createFaculty(@Body rb: CreateFacultyRequestBody): CreateFacultyResponseBody

    @GET("/get/withdrawal/requests")
    suspend fun getWithdrawalRequests(@QueryMap options: HashMap<String, String>): GetWithdrawalRequestResponse

    @POST("/withdrawal/update/status")
    suspend fun updateWithdrawalRequest(@Body rb: UpdateWithdrawalStatusRequestBody): UpdateWithdrawalStatusResponseBody

    @POST("/notification/post/to/user")
    suspend fun postNotificationUser(@Body body: PostNotification): DefaultNetworkResponse

    @POST("/user/toggle/status")
    suspend fun toggleUserStatus(@Body body: ToggleUserStatus): DefaultNetworkResponse

    @GET("/chat/get/chatlist")
    suspend fun getChatList(@QueryMap options: HashMap<String, String>): GetChatListResponse

    @POST("/chat/send/message/to/user")
    suspend fun sendMessageToUser(@Body body: SendMessageToUserBody): SentMessageResponse

    @GET("/chat/get/chats")
    suspend fun loadChats(@QueryMap option: HashMap<String, String>): GetChatsResponse

    @GET("/settings/get/all")
    suspend fun getSettings(@Query("adminId") admin: String): GetSettingsResponse

    @POST("/settings/update")
    suspend fun updateSettings(@Body body: UpdateSettingsRequestBody): DefaultNetworkResponse

    @GET("/notification/get/all")
    suspend fun getNotifications(
        @Query("adminId") admin: String,
        @Query("page") page: Int
    ): GetNotificationResponseBody

    @POST("/notification/delete")
    suspend fun deleteNotification(@Body body: DefaultRequestBody): DefaultNetworkResponse

    @POST("/notification/send/announcement")
    suspend fun sendAnnouncement(@Body body: PostNotification): NewAnnouncementResponse

    @GET("/get/announcement")
    suspend fun getAnnouncement(
        @Query("adminId") admin: String,
        @Query("page") page: Int
    ): GetAnnouncementResponse

    @POST("/notification/delete/announcement")
    suspend fun deleteAnnouncement(@Body body: DefaultRequestBody): DefaultNetworkResponse

    @GET("/get/lisensekeys")
    suspend fun getLisenseKeys(@QueryMap options: HashMap<String, String>): GetLisenseKeyResponseBody

    @GET("/get/keys/statistics")
    suspend fun getKeysStatistics(@Query("adminId") admin: String): GetKeyStatisticsResponseBody

    @POST("/update/key/price")
    suspend fun updateKeyWorth(@Body body: UpdateKeyRequestBody): DefaultNetworkResponse

    @POST("/update/delete/key")
    suspend fun deleteKey(@Body body: UpdateKeyRequestBody): DefaultNetworkResponse

    @GET("/get/vendors")
    suspend fun getVendors(@Query("adminId") admin: String): GetVendorsResponseBody

    @GET("/get/vendor/stats")
    suspend fun getVendorStats(
        @Query("adminId") admin: String,
        @Query("vendorId") vendor: String
    ): GetVendorStatsResponseBody

    @POST("/create/vendor")
    suspend fun createVendor(@Body body: CreateVendorRequestBody): CreateVendorResponseBody

    @POST("/update/delete/vendor")
    suspend fun deleteVendor(@Body body: DefaultRequestBody): DefaultNetworkResponse

    @GET("/get/vendor/keys")
    suspend fun getVendorKeys(@QueryMap options: HashMap<String, String>): GetLisenseKeyResponseBody

    @POST("/create/keys")
    suspend fun generateKeys(@Body body: GenerateKeysRequestBody): GenerateKeysResponseBody
    @POST("/update/pastquestions/price")
    suspend fun setPastQuestionPrice(@Body body:PastQuestionPricingRequestBody):DefaultNetworkResponse
}
