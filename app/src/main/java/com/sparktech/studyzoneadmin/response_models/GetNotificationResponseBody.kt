package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.Notification

data class GetNotificationResponseBody(
    val notifications: List<Notification>,
    val code: Int,
    val message: String?
)
