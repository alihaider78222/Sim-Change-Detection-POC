package com.example.simchangedetectionpoc.ui.theme

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences


class SharedPreference private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val prefsEditor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences("sp_cwal", Activity.MODE_PRIVATE)
        prefsEditor = sharedPreferences.edit()
    }

    fun save(key: String?, value: String?): Boolean {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    fun getString(key: String?): String? {
        return sharedPreferences.getString(key, "")
    }

    fun save(valuesMap: HashMap<String?, String?>): Boolean {
        val editor = sharedPreferences.edit()
        var value: String? = ""
        for (key in valuesMap.keys) {
            value = valuesMap[key]
            editor.putString(key, value)
        }
        return editor.commit()
    }

    fun save(key: String?, value: Boolean): Boolean {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun clear(context: Context?) {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun remove(key: String?) {
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun save(key: String?, value: Int): Boolean {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun save(key: String?, value: Float): Boolean {
        val editor = sharedPreferences.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }

    fun getFloat(key: String?, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    fun save(key: String?, value: Set<String?>?): Boolean {
        if (value == null) return false
        val editor = sharedPreferences.edit()
        editor.putStringSet(key, value)
        return editor.commit()
    }

    fun getStringSet(key: String?, defaultValue: Set<String?>?): Set<String>? {
        return sharedPreferences.getStringSet(key, defaultValue)
    }

    companion object {
        private var mInstance: SharedPreference? = null
        fun init(context: Context) {
            mInstance = SharedPreference(context)
        }

        val instance: SharedPreference?
            get() {
                if (mInstance == null) {
                    throw RuntimeException(
                        "Must run init(Application application) before an instance can be obtained"
                    )
                }
                return mInstance
            }
    }
}
