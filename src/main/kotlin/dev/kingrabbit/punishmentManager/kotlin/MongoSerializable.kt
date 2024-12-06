package dev.kingrabbit.punishmentManager.kotlin

import gg.flyte.twilight.gson.GSON
import org.bson.Document

interface MongoSerializable {

    fun toDocument(): Document {
        return Document.parse(GSON.toJson(this))
    }

    companion object {
        inline fun <reified T> fromDocument(document: Document?): T? {
            if (document == null) return null
            val json = document.toJson()
            return GSON.fromJson(json, T::class.java)
        }
    }

}