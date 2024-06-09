package com.example.ryady.view.screens.settings

import com.example.ryady.network.model.Response
import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

class ResponseTypeAdapter<T> : TypeAdapter<Response<T>>() {
    private val gson = Gson()

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Response<T>) {
        out.beginObject()
        when (value) {
            is Response.Success -> {
                out.name("status").value("success")
                out.name("data")
                gson.toJson(value.data, HashMap::class.java, out)
            }
            is Response.Error -> {
                out.name("status").value("error")
                out.name("message").value(value.message)
            }
            is Response.Loading -> {
                out.name("status").value("loading")
            }
        }
        out.endObject()
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Response<T> {
        `in`.beginObject()
        var status: String? = null
        var data: T? = null
        var message: String? = null

        while (`in`.hasNext()) {
            when (`in`.nextName()) {
                "status" -> status = `in`.nextString()
                "data" -> data = gson.fromJson(`in`, HashMap::class.java) as T
                "message" -> message = `in`.nextString()
            }
        }
        `in`.endObject()

        return when (status) {
            "success" -> Response.Success(data ?: getDefaultData())
            "error" -> Response.Error(message ?: "")
            "loading" -> Response.Loading()
            else -> throw JsonSyntaxException("Unknown status: $status")
        }
    }

    private fun getDefaultData(): T {
        return "" as T
    }
}
