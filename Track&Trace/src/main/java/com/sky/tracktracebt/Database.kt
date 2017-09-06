package com.sky.tracktracebt

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Created by Sky_Alligator on 4/10/2017.
 * 10:11 PM
 */
class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {
    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("TransferHistory", true,
                "_id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "login_name" to TEXT,
                "login_type" to TEXT,
                "kit_id" to TEXT,
                "kit_type" to TEXT,
                "receiver_name" to TEXT,
                "receiver_type" to TEXT,
                "success" to TEXT,
                "response" to TEXT,
                "timestamp" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable("TransferHistory", true)
        onCreate(db)
    }
}

val Context.db: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(applicationContext)

fun insertDB(context: Context, kitID: String, kitType: String, success: Boolean, response: String) {
    val rName = AppData.receiverData?.actualName ?: ""
    val rRole = AppData.receiverData?.role ?: ""

    context.db.use {
        insert("TransferHistory",
                "login_name" to AppData.loginActualName
                , "login_type" to AppData.loginRole
                , "kit_id" to kitID
                , "kit_type" to kitType
                , "receiver_name" to rName
                , "receiver_type" to rRole
                , "success" to if (success) "true" else "false"
                , "response" to response
                , "timestamp" to getTimeStamp())
    }
}

fun getDbHistory(context: Context): List<History> {
    var list = listOf<History>()
    context.db.use {
        select("TransferHistory")
                .where("(login_name = {loginActualName}) and (success = {bool})", "loginActualName" to AppData.loginActualName, "bool" to "true")
                .orderBy("timestamp", SqlOrderDirection.DESC)
                .exec {
                    list = parseList(HistoryParser())
                }
    }
    return list
}

fun getDbAlerts(context: Context): List<History> {
    var list = listOf<History>()
    context.db.use {
        select("TransferHistory")
                .where("login_name = {loginActualName}", "loginActualName" to AppData.loginActualName)
                .orderBy("timestamp", SqlOrderDirection.DESC)
                .exec {
                    list = parseList(HistoryParser())
                }
        close()
    }
    return list
}

class HistoryParser : MapRowParser<History> {
    override fun parseRow(columns: Map<String, Any?>): History {
        return History(
                columns["login_name"] as String,
                columns["login_type"] as String,
                columns["kit_id"] as String,
                columns["kit_type"] as String,
                columns["receiver_name"] as String,
                columns["receiver_type"] as String,
                columns["success"] as String,
                columns["response"] as String,
                columns["timestamp"] as String)
    }
}