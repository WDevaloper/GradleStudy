package com.github.router.runtime

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import java.util.*

interface IBundle {
    fun withString(key: String, value: String)
    fun withBoolean(key: String, value: Boolean)
    fun withInt(key: String, value: Int)
    fun withLong(key: String, value: Long)
    fun withBundle(bundle: Bundle)
    fun withIntArray(key: String, value: IntArray)
    fun withStringArray(key: String, value: Array<String>)
    fun withParcelable(key: String, value: Parcelable)
    fun withSerializable(key: String, value: Serializable)
    fun withParcelableArrayList(key: String, value: ArrayList<Parcelable>)
}