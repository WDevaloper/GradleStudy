package com.github.router.runtime

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import java.util.ArrayList

class BundleManager : IBundle {

    var bundle: Bundle = Bundle()
        private set

    override fun withString(key: String, value: String) {
        bundle.putString(key, value)
    }

    override fun withBoolean(key: String, value: Boolean) {
        bundle.putBoolean(key, value)
    }

    override fun withInt(key: String, value: Int) {
        bundle.putInt(key, value)
    }

    override fun withLong(key: String, value: Long) {
        bundle.putLong(key, value)
    }

    override fun withBundle(bundle: Bundle) {
        this.bundle = bundle
    }

    override fun withIntArray(key: String, value: IntArray) {
        bundle.putIntArray(key, value)
    }

    override fun withStringArray(key: String, value: Array<String>) {
        bundle.putStringArray(key, value)
    }

    override fun withParcelable(key: String, value: Parcelable) {
        bundle.putParcelable(key, value)
    }

    override fun withSerializable(key: String, value: Serializable) {
        bundle.putSerializable(key, value)
    }

    override fun withParcelableArrayList(key: String, value: ArrayList<Parcelable>) {
        bundle.putParcelableArrayList(key, value)
    }
}