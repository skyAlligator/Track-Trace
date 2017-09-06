package com.sky.tracktracebt.fragments

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.sky.tracktracebt.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_transfer_histroy.view.*

/**
 * Created by Sky_Alligator on 4/7/2017.
 * 12:33 PM
 */
class TransferHistoryFrag : FragmentMom(R.layout.frag_trasfer_history) {

    override fun onBackPressed() {
        activity.finish()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setTitleBar("Transfer History", false)
        val alerts = getDbHistory(activity)
        val adapter = CommonAdapter(R.layout.row_transfer_histroy, alerts, { v, data ->
            v.ui_row_transferHistory_timestampV.text = data.timestamp
            buildTransRow(data, v)
        })

        val recyclerV = view as RecyclerView
        recyclerV.layoutManager = LinearLayoutManager(activity)
        recyclerV.adapter = adapter
        recyclerV.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        recyclerV.itemAnimator = DefaultItemAnimator()
    }

    private fun buildTransRow(data: History, v: View) {
//        val details = "You have received ownership of Kit ID ${data.kit_id} and Kit type ${data.kit_type} from ${data.receiver_name}."
        val details = "You have received ownership from ${data.receiver_name}."
//        v.ui_row_transferHistory_detailTv.text = details
//        setBlackForWords(v.ui_row_transferHistory_detailTv, details, data.kit_id, data.kit_type, data.receiver_name)
        setBlackForWords(v.ui_row_transferHistory_detailTv, details, data.receiver_name)
        Picasso.with(v.context).load("file:///android_asset/imgs/${data.receiver_name}.jpg").into(v.ui_row_transferHistory_imgV)
    }
}