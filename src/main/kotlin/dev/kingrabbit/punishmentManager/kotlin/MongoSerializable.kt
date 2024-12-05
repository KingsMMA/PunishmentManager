package dev.kingrabbit.punishmentManager.kotlin

import com.google.gson.Gson
import org.bson.Document

interface MongoSerializable {

    fun toDocument(): Document {
        val gson = Gson()
        return Document.parse(gson.toJson(this))
    }

    companion object {
        inline fun <reified T> fromDocument(document: Document?): T? {
            if (document == null) return null
            val gson = Gson()
            val json = document.toJson()
            return gson.fromJson(json, T::class.java)
        }
    }

}