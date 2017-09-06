package com.sky.tracktracebt

import android.content.Context
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result

/**
 * Created by Sky_Alligator on 3/3/2017.
 * 8:38 PM
 */

private val login = "https://kittrackblockchain.mybluemix.net/loginCheck"
//private val login = "https://coldchaindb.documents.azure.com:443/dbs/coldChainDB/colls/userDetails/docs"
private val ownerList = "https://a1b18fc4-a91f-420a-bdd6-51d2f944e63a-bluemix.cloudant.com/user_owner_details/_find"
private val transfer = "http://kittrackblockchain.mybluemix.net/ownertransfer"
private val kitSterilization = "http://kittrackblockchain.mybluemix.net/kitsterilization"

fun apiCallLogin(name: String, password: String, update: (statusCode: Int, Boolean) -> Unit) {
    Fuel.post(login).apply {
        header("Content-Type" to "application/json", "Authorization" to "No Auth")
        body(loginJson(name, password))

        response { request, response, result ->
            log(request.toString())
            log(response.toString())
            val res = when (result) {
                is Result.Success -> true
                is Result.Failure -> false
            }
            update(response.httpStatusCode, res && String(response.data).equals("validUser", ignoreCase = true))
        }
    }
}

fun apiCallLoginNOlist(con: Context, name: String, password: String, update: (ApiType, statusCode: Int, Boolean, ArrayList<ReceiverData>?) -> Unit) {
//    apiCallLogin(name, password) { loginStCode, success ->
//        if (success) {
    val json = parseLocalJson(con)
    val arrayList = parseLocalJsonOwnerList(json)
    update(ApiType.OLIST, 200, arrayList?.size!! > 0, arrayList)
//        } else
//            update(ApiType.LOGIN, loginStCode, false, null)
    //todo
    /*apiCallOwnerList { oStCode, success, dataList ->
        if (success)
            update(ApiType.OLIST, oStCode, true, dataList)
        else
            update(ApiType.OLIST, oStCode, false, null)
    }*/
//    }
}


fun apiCallOwnerList(update: (statusCode: Int, Boolean, ArrayList<ReceiverData>?) -> Unit) {
    Fuel.post(ownerList).apply {
        authenticate("a1b18fc4-a91f-420a-bdd6-51d2f944e63a-bluemix", "3850d70862fa311c54940fea303a775fe3d2d2be83d302e1d9f1bdfba0206d52")
        header("Content-Type" to "application/json")
        body(ownerListJson())

        response { request, response, result ->
            log(request.toString())
            log(response.toString())
            val success = when (result) {
                is Result.Success -> true
                is Result.Failure -> false
            }
            if (success) {
                val json = String(response.data)
                val list = parseOwnerList(json)
                if (list == null)
                    update(response.httpStatusCode, false, null)
                else {
//                    dummyData(list)
                    update(response.httpStatusCode, true, list)
                }
            } else
                update(response.httpStatusCode, false, null)
        }
    }
}

fun apiCallTransfer(kitID: String, receiverRole: String, receiverName: String, assetType: String,
                    assetModel: String, firmwareVersion: String, buildNumber: String, buildBy: String, receiverId: String, update: (statusCode: Int, Boolean, String) -> Unit) {
    Fuel.post(transfer).apply {
        header("Content-Type" to "application/json", "Authorization" to "No Auth")
        body(transferJson(kitID, receiverRole, receiverName, assetType, assetModel, firmwareVersion, buildNumber, buildBy, receiverId))
        response { request, response, result ->
            log(request.toString())
            log(response.toString())
            val success = when (result) {
                is Result.Success -> true
                is Result.Failure -> false
            }
            update(response.httpStatusCode, success, String(response.data))
        }
    }
}

fun apiCallSterilization(kitID: String, assetType: String,
                         assetModel: String, firmwareVersion: String, buildNumber: String, buildBy: String, update: (Boolean, String) -> Unit) {
    Fuel.post(kitSterilization).apply {
        header("Content-Type" to "application/json", "Authorization" to "No Auth")
        body(sterilizationJson(kitID, assetType, assetModel, firmwareVersion, buildNumber, buildBy))
        response { request, response, result ->
            log(request.toString())
            log(response.toString())
            val success = when (result) {
                is Result.Success -> true
                is Result.Failure -> false
            }
            if (success) update(success, String(response.data))
            else update(false, String(response.data))
        }
    }
}