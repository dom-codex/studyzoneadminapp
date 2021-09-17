package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.Announcement

data class GetAnnouncementResponse(
    val announcements: List<Announcement>,
    val code: Int,
    val message: String?
)