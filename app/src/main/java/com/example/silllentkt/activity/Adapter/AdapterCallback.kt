package com.example.silllentkt.activity.Adapter

import com.example.silllentkt.activity.database.Profile

interface AdapterCallback {
    fun updateItem(profile: Profile)
    fun openProfileDetails(profile: Profile)
    fun setAlarms(profile: Profile, startHour: Int = profile.shr, startMinute: Int = profile.smin)
    fun cancelWorkByTag(tag: String)
}