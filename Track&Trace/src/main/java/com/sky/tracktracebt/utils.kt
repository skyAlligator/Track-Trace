package com.sky.tracktracebt

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.sky.tracktracebt.activities.HomeTabActivity
import com.sky.tracktracebt.fastscrollrecyclerview.FastScrollRecyclerViewInterface
import com.sky.tracktracebt.fragments.FragmentMom
import kotlinx.android.synthetic.main.activity_tabhome.*
import kotlinx.android.synthetic.main.alert_simple.view.*
import kotlinx.android.synthetic.main.common_action_bar.*
import org.jetbrains.anko.layoutInflater
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler


/**
 * Created by Sky_Alligator on 3/10/2017.
 * 12:53 AM
 */
enum class StreamType {
   EXCEPTION, RECEIVE, SEND,ERROR
}

fun ignoreException(fnc: () -> Unit) {
    try {
        fnc()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun parseLocalJson(con: Context): String? {
    var json: String? = null
    try {
        val ins = con.assets.open("login.json")
        val reader = BufferedReader(InputStreamReader(ins))
        json = reader.readText()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return json
}

fun navigateToHome(activity: Activity) {
    val homeTabActivity = activity as HomeTabActivity
    homeTabActivity.ui_tab_transferV.performClick()
}

fun setBlackForWords(tV: TextView, sentence: String, vararg words: String) {
    try {
        val ss1 = SpannableString(sentence)
        for (w in words) {
            val start = sentence.indexOf(w)
            val end = start + w.length
            log("$start  --  $end  for ${sentence.length}")
            ss1.setSpan(ForegroundColorSpan(Color.BLACK), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        tV.text = ss1
    } catch (e: Exception) {
        e.printStackTrace()
        tV.text = sentence
    }
}

fun setFirstWordBlack(tV: TextView, sentence: String) {
    try {
        val split = sentence.split(" ")
        setBlackForWords(tV, sentence, split[0])
    } catch (e: Exception) {
        e.printStackTrace()
        tV.text = sentence
    }
}

fun simpleAlert(context: Context, message: String, onOk: () -> Unit) {
    val builder = AlertDialog.Builder(context)
    val alertV = context.layoutInflater.inflate(R.layout.alert_simple, null)
    builder.setView(alertV)

    val dialog = builder.create()
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    alertV.ui_alert_cmn_messageV.text = message
    alertV.ui_alert_cmn_okV.setOnClickListener {
        dialog.dismiss()
        onOk()
    }
    dialog.show()
}

enum class ApiType {
    LOGIN, OLIST
}

fun getTimeStamp(): String {
    val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val currenTime = Calendar.getInstance().time
    return timeStamp.format(currenTime)
}

fun Activity.addLinearFrag(frag: FragmentMom) {
    fragmentManager.beginTransaction()
            .replace(R.id.ui_frag_container, frag)
            .addToBackStack("transfer")
            .commit()
}

fun Fragment.addLinearFrag(frag: FragmentMom) {
    activity.addLinearFrag(frag)
}

fun Fragment.removeTransferFragments() {
    fragmentManager.popBackStack("transfer", POP_BACK_STACK_INCLUSIVE)
}

open class CommonAdapter<T>(val layout: Int, var li: List<T>, val bindV: (View, T) -> Unit) :
        RecyclerView.Adapter<CommonAdapter.ViewHolder<T>>() {

    open fun updateList(list: List<T>) {
        li = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = li.size

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bindData(li[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder<T> {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(layout, parent, false), bindV)
    }

    class ViewHolder<in T>(val itemV: View, val bindV: (View, T) -> Unit) : RecyclerView.ViewHolder(itemV) {

        fun bindData(ownerData: T) {
            bindV(itemV, ownerData)
        }
    }
}

class AlphabetAdapter<T>(layout: Int, li: List<T>, bindV: (View, T) -> Unit) : CommonAdapter<T>(layout, li, bindV), FastScrollRecyclerViewInterface {

    private var mapIndex: HashMap<String, Int>

    init {
        mapIndex = calculateIndexesForName(li)
    }

    override fun getLetterMapIndex() = mapIndex

    override fun updateList(list: List<T>) {
        mapIndex = calculateIndexesForName(list)
        super.updateList(list)
    }
}

fun log(text: String) {
    Log.d("debug", text)
}

class BrushTextView : TextView {
    constructor(context: Context, attr: AttributeSet, style1: Int, style2: Int) : super(context, attr, style1, style2) {
        start()
    }

    constructor(context: Context, attr: AttributeSet, style1: Int) : super(context, attr, style1) {
        start()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        start()
    }

    constructor(context: Context) : super(context) {
        start()
    }

    private fun start() {
        val tf = Typeface.createFromAsset(context.assets, "BRUSHSCI.TTF")
        setTypeface(tf, 1)
    }
}

object AppData {
    var loginActualName = ""
    var loginUserName = ""
    var loginRole = ""
    var loginOwnerId = ""
    var loginPhone = "1-(541)754-3010"
//    var loginProfileImage = "John Bradley-West.jpg" //Todo use image in profile

    var manufCatSelected: Boolean = false

    val SUPPLY_MANAGER = "Supply Manager"
    val OPERATIONS_MANAGER = "Operations Manager"
    val FLEET_MANAGER = "Fleet Manager"
    val PURCHASING_MANAGER = "Purchasing Manager"

    var supplyManager: List<ReceiverData>? = null
    var fleetManager: List<ReceiverData>? = null
    var operationsManager: List<ReceiverData>? = null
    var purchasingManager: List<ReceiverData>? = null

    var receiverData: ReceiverData? = null

    var sterilizedSuccess = true
    var kitID = ""
    var kitType = ""
}

fun checkCon(it: ReceiverData, s: String) = it.actualName.toLowerCase().contains(s.toLowerCase())

fun <T> calculateIndexesForName(items: List<T>): HashMap<String, Int> {
    val mapIndex = LinkedHashMap<String, Int>()
    for (i in items.indices) {
        val name = items[i].toString()
        var index = name.substring(0, 1)
        index = index.toUpperCase()

        if (!mapIndex.containsKey(index)) {
            mapIndex.put(index, i)
        }
    }
    return mapIndex
}

interface FragmentBackPress {
    fun onBackPressed()
}

fun Fragment.showSnackMessage(message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) view = View(activity)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getUserData(context: Context): Pair<String, String> {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val name = preferences.getString("userName", "")
    val pass = preferences.getString("password", "")
    return Pair(name, pass)
}

fun setUserData(context: Context, name: String, password: String) {
    val edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
    edit.putString("userName", name)
    edit.putString("password", password)
    edit.apply()
}

fun clearUserData(context: Context) {
    val edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
    edit.putString("userName", "")
    edit.putString("password", "")
    edit.apply()
}

fun internetConnected(context: Context): Boolean {
    val c = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return c.activeNetworkInfo?.isConnectedOrConnecting ?: false
}

fun setViewVisible(visible: Boolean, vararg v: View) {
    v.forEach {
        if (visible) it.visibility = View.VISIBLE
        else it.visibility = View.INVISIBLE
    }
}

fun EditText.setTextChangeListener(listener: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            listener(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun Activity.setTitleBar(title: String, enableBack: Boolean, onBackFun: (() -> Unit)? = null) {
    if (enableBack) {
        ui_actionBar_backV.visibility = View.VISIBLE
        ui_actionBar_refreshV.gone()
        ui_actionBar_backV.setOnClickListener {
            if (onBackFun != null) onBackFun()
            else finish()
        }
    } else {
        ui_actionBar_backV.visibility = View.GONE
    }
    ui_actionBar_titleV.text = title
}

fun Fragment.setTitleBar(title: String, enableBack: Boolean, onBackFun: (() -> Unit)? = null) = activity.setTitleBar(title, enableBack, onBackFun)


/*fun dummyData(list: ArrayList<ReceiverData>) {
    list.add(ReceiverData("1851", "Aidan Gillen", "Manufacturer", "1-(130)276-2259", "Aidan Gillen.jpg"))
    list.add(ReceiverData("1852", "Alfie Allen", "distributor", "1-(130)276-2259", "Alfie Allen.jpg"))
    list.add(ReceiverData("1853", "Carice van Houten", "Hospital", "1-(130)276-2259", "Carice van Houten.jpg"))
    list.add(ReceiverData("1854", "Charles Dance", "Sterilizer", "1-(130)276-2259", "Charles Dance.jpg"))
    list.add(ReceiverData("1855", "Conleth Hill", "Manufacturer", "1-(130)276-2259", "Conleth Hill.jpg"))
    list.add(ReceiverData("1856", "Emilia Clarke", "distributor", "1-(130)276-2259", "Emilia Clarke.jpg"))
    list.add(ReceiverData("1857", "Gwendoline Christie", "Hospital", "1-(130)276-2259", "Gwendoline Christie.jpg"))
    list.add(ReceiverData("1858", "Hannah Murray", "Sterilizer", "1-(130)276-2259", "Hannah Murray.jpg"))
    list.add(ReceiverData("1859", "Iain Glen", "distributor", "1-(130)276-2259", "Iain Glen.jpg"))
    list.add(ReceiverData("1860", "Indira Varma", "Hospital", "1-(130)276-2259", "Indira Varma.jpg"))
    list.add(ReceiverData("1861", "Isaac Hempstead Wright", "Manufacturer", "1-(130)276-2259", "Isaac Hempstead Wright.jpg"))
    list.add(ReceiverData("1862", "Iwan Rheon", "Sterilizer", "1-(130)276-2259", "Iwan Rheon.jpg"))
    list.add(ReceiverData("1863", "Jack Gleeson", "distributor", "1-(130)276-2259", "Jack Gleeson.jpg"))
    list.add(ReceiverData("1864", "Jason Momoa", "Hospital", "1-(130)276-2259", "Jason Momoa.jpg"))
    list.add(ReceiverData("1865", "Jerome Flynn", "Manufacturer", "1-(130)276-2259", "Jerome Flynn.jpg"))
    list.add(ReceiverData("1866", "John Bradley-West", "Sterilizer", "1-(130)276-2259", "John Bradley-West.jpg"))
    list.add(ReceiverData("1867", "Julian Glover", "distributor", "1-(130)276-2259", "Julian Glover.jpg"))
    list.add(ReceiverData("1868", "Kit Harington", "Hospital", "1-(130)276-2259", "Kit Harington.jpg"))
    list.add(ReceiverData("1869", "Kristian Nairn", "Manufacturer", "1-(130)276-2259", "Kristian Nairn.jpg"))
    list.add(ReceiverData("1870", "Lena Headey", "Sterilizer", "1-(130)276-2259", "Lena Headey.jpg"))
    list.add(ReceiverData("1871", "Liam Cunningham", "distributor", "1-(130)276-2259", "Liam Cunningham.jpg"))
    list.add(ReceiverData("1872", "Maisie Williams", "Hospital", "1-(130)276-2259", "Maisie Williams.jpg"))
    list.add(ReceiverData("1873", "Michael McElhatton", "Manufacturer", "1-(130)276-2259", "Michael McElhatton.jpg"))
    list.add(ReceiverData("1874", "Michiel Huisman", "Sterilizer", "1-(130)276-2259", "Michiel Huisman.jpg"))
    list.add(ReceiverData("1875", "Natalie Dormer", "distributor", "1-(130)276-2259", "Natalie Dormer.jpg"))
    list.add(ReceiverData("1876", "Nathalie Emmanuel", "Hospital", "1-(130)276-2259", "Nathalie Emmanuel.jpg"))
    list.add(ReceiverData("1877", "Nikolaj Coster-Waldau", "Manufacturer", "1-(130)276-2259", "Nikolaj Coster-Waldau.jpg"))
    list.add(ReceiverData("1878", "Peter Dinklage", "Sterilizer", "1-(130)276-2259", "Peter Dinklage.jpg"))
    list.add(ReceiverData("1879", "Richard Madden", "distributor", "1-(130)276-2259", "Richard Madden.jpg"))
    list.add(ReceiverData("1880", "Rory McCann", "Hospital", "1-(130)276-2259", "Rory McCann.jpg"))
    list.add(ReceiverData("1881", "Rose Leslie", "Manufacturer", "1-(130)276-2259", "Rose Leslie.jpg"))
    list.add(ReceiverData("1882", "Sean Bean", "Sterilizer", "1-(130)276-2259", "Sean Bean.jpg"))
    list.add(ReceiverData("1883", "Sibel Kekilli", "distributor", "1-(130)276-2259", "Sibel Kekilli.jpg"))
    list.add(ReceiverData("1884", "Sophie Turner", "Hospital", "1-(130)276-2259", "Sophie Turner.jpg"))
}*/

