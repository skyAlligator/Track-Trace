package com.sky.tracktracebt

/**
 * Created by Sky_Alligator on 3/15/2017.
 * 1:20 PM
 */
fun loginJson(userName: String, password: String) =
        """{
              "username":"$userName",
              "password":"$password"
           }"""

/*fun loginJson1(id: String, ownername: String, ownerrole: String, ownerid: String,userName: String, password: String,
               _rid: String, _self: String, _etag: String, _attachments: String,etag: String) =
        """{
              "id": "$id",
              "ownername": "$ownername",
              "ownerrole": "$ownerrole",
              "ownerid": "$ownerid",
              "username":"$userName",
              "password":"$password"
              "rid": "$_rid",
              "self": "$_self",
              "etag": "$_etag",
              "attachments": "$_attachments",
              "_ts": 1501581689

           }"""*/

fun transferJson(kitID: String, ownerrole: String, ownername: String, assetType: String,
                 assetModel: String, firmwareVersion: String, buildNumber: String, buildBy: String, ownerid: String): String {
    val st = if (AppData.loginRole.equals("Hospital", true)) "USED" else "In-Use"
    return """{
              "assetID": "$kitID",
              "st": "In-Use",
              "ownerrole": "$ownerrole",
              "ownername": "$ownername",
              "assetType": "$assetType",
              "assetModel":"$assetModel",
              "firmwareVersion":"$firmwareVersion",
              "buildNumber":"$buildNumber",
              "buildBy":"$buildBy",
              "ownerid": "$ownerid",
              "Alt": "199.00100000",
              "Lon": "77.339957",
              "Lat": "28.53662",
              "CSts": "${AppData.loginOwnerId}",
              "BSts": "${AppData.loginActualName}",
              "DSts": "${AppData.loginRole}",
              "Tmpr": "ownertransfer",
              "Hmdt": "mobile",
              "Elst": [
                {
                  "nm": "Disposable Cannulas",
                  "st": "I"
                },
                {
                  "nm": "Formula XL blades",
                  "st": "I"
                },
                {
                  "nm": "Flex",
                  "st": "I"
                }
              ]
        }"""
}

fun sterilizationJson(kitID: String, assetType: String,
                      assetModel: String, firmwareVersion: String, buildNumber: String, buildBy: String) =
        """{
              "assetID": "$kitID",
              "st": "USED",
              "ownerrole": "${AppData.loginRole}",
              "ownername": "${AppData.loginActualName}",
              "assetType": "$assetType",
              "assetModel":"$assetModel",
              "firmwareVersion":"$firmwareVersion",
              "buildNumber":"$buildNumber",
              "buildBy":"$buildBy",
              "ownerid": "${AppData.loginOwnerId}",
              "Alt": "199.00100000",
              "Lon": "77.339957",
              "Lat": "28.53662",
              "CSts": "0",
              "BSts": "1",
              "DSts": "In-",
              "Tmpr": "sterilization",
              "Hmdt": "mobile",
              "Elst": [
                {
                  "nm": "Disposable Cannulas",
                  "st": "I"
                },
                {
                  "nm": "Formula XL blades",
                  "st": "I"
                },
                {
                  "nm": "Flex",
                  "st": "I"
                }
              ]
        }"""

fun ownerListJson() =
        """ {
                "selector": {
                    "_id": {
                        "${"$"}gt": 0
                    }
                },
                "sort": [{
                    "_id": "asc"
                }]
            }
        """