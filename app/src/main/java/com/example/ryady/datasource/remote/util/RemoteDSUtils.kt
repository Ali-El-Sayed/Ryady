package com.example.ryady.datasource.remote.util

object RemoteDSUtils {
    fun encodeEmail(email: String): String {
        return email.replace(".", ",").replace("@", "_at_")
    }

}