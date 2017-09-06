package com.sky.tracktracebt

import com.beust.klaxon.*
import org.json.JSONArray


/**
 * Created by Sky_Alligator on 3/9/2017.
 * 9:33 PM
 */
fun parseLocalJsonOwnerList(resJson: String?): ArrayList<ReceiverData>? {
    if (resJson === null) return null
    val ownerList = arrayListOf<ReceiverData>()
    try {
        val ay = Parser().parse(StringBuilder(resJson))
        val arry = ay as? JsonArray<JsonObject>

        arry?.forEach {
            val uName = it.string("username")!!
            val actName = it.string("ownerName")!!
            val role = it.string("ownerRole")!!
            val id = it.int("ownerID")!!
            ownerList.add(ReceiverData("$id", uName, actName, role, "1-(120)471-8539", "Jason Momoa.jpg"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ownerList
}

fun parseOwnerList(resJson: String): ArrayList<ReceiverData>? {
    try {
        val ownerList = arrayListOf<ReceiverData>()

        val obj = Parser().parse(StringBuilder(resJson)) as JsonObject
        val docsArray: JsonArray<JsonObject>? = obj.array<JsonObject>("docs")

        docsArray?.forEach {
            val usr = it.obj("USERS") as JsonObject
            val uName = usr.string("userName")!!
            val actName = usr.string("ownerName")!!
            val role = usr.string("ownerRole")!!
            val id = usr.int("ownerId")!!

            ownerList.add(ReceiverData("$id", uName, actName, role, "1-(120)471-8539", "Jason Momoa.jpg"))
        }
        return ownerList
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun parseQrCode(qrText: String): QrData? {
    try {
        val obj = Parser().parse(StringBuilder(qrText)) as JsonObject
        return QrData(obj.string("Kit ID")!!,
                obj.string("Kit Type")!!,
                obj.string("assetModel")!!,
                obj.string("firmwareVersion")!!,
                obj.string("buildNumber")!!,
                obj.string("buildBy")!!,
                obj.string("Manufacture")!!,
                obj.string("Last Sterilized")!!,
                obj.string("version")!!,
                obj.string("Kit Status")!!)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}