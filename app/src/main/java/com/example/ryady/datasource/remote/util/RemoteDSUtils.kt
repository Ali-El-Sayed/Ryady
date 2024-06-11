package com.example.ryady.datasource.remote.util

object RemoteDSUtils {
    fun encodeEmail(email: String): String {
        return email.replace(".", ",").replace("@", "_at_")
    }
    fun decodeEmail(encodedEmail: String): String {
        return encodedEmail.replace("_at_", "@").replace(",", ".")
    }

}