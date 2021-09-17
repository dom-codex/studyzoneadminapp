package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.Announcement

data class NewAnnouncementResponse(val announcement: Announcement,val code:Int,val message:String?)
