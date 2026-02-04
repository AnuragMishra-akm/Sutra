package com.example.sutra.utils

import android.content.Context

object SharedPrep {
    fun storeData(name:String, email:String, bio:String, userName: String,imageUri: String, context: Context){
        val sharedPref = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("name", name)
        editor.putString("email", email)
        editor.putString("bio", bio)
        editor.putString("userName", userName)
        editor.putString("imageUri", imageUri)
        editor.apply()
    }

    fun getUserName(context: Context): String{
        val sharedPref = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        return sharedPref.getString("userName", "") ?: ""
    }
    fun getEmail(context: Context): String{
        val sharedPref = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        return sharedPref.getString("email", "") ?: ""
    }
    fun getName(context: Context): String{
        val sharedPref = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        return sharedPref.getString("name", "") ?: ""
    }
    fun getBio(context: Context): String{
        val sharedPref = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        return sharedPref.getString("bio", "") ?: ""
    }
    fun getImageUri(context: Context): String{
        val sharedPref = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        return sharedPref.getString("imageUri", "") ?: ""
    }
}