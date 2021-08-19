package com.sparktech.studyzoneadmin.network

import com.sparktech.studyzoneadmin.models.AuthLogin
import com.sparktech.studyzoneadmin.models.DetailsResponse
import com.sparktech.studyzoneadmin.models.School
import com.sparktech.studyzoneadmin.models.SliderData
import com.sparktech.studyzoneadmin.request_models.NewSchool
import com.sparktech.studyzoneadmin.request_models.SchoolToDelete
import com.sparktech.studyzoneadmin.response_models.LoginResponse
import com.sparktech.studyzoneadmin.response_models.NewSchoolResponse
import com.sparktech.studyzoneadmin.response_models.SchoolDeleteResponse
import com.sparktech.studyzoneadmin.response_models.SchoolResponse
import retrofit2.http.*

interface ApiService {
@POST("auth/admin/login")
suspend fun adminLogin(@Body data:AuthLogin):LoginResponse
@POST("create/school")
suspend fun createSchool(@Body newSchool: NewSchool):NewSchoolResponse
@POST("delete/school")
suspend fun deleteSchool(@Body schoolToDelete: SchoolToDelete):SchoolDeleteResponse
@GET("get/stats")
suspend fun getSliderData():SliderData
@GET("get/activity/details?page=0")
suspend fun getActivityData():DetailsResponse
@GET("get/schools")
suspend fun fetchSchools(@Query("page") page:Int,@Query("type") type:String):SchoolResponse

}
