package com.sky.tracktracebt.fragments

import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.sky.tracktracebt.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_alerts.view.*

/**
 * Created by Sky_Alligator on 4/7/2017.
 * 12:33 PM
 */
class AlertsFrag : FragmentMom(R.layout.frag_alerts) {

    override fun onBackPressed() {
        activity.finish()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setTitleBar("Alerts", false)
        val alerts = getDbAlerts(activity)

        val adapter = CommonAdapter(R.layout.row_alerts, alerts, { v, data ->
            v.ui_row_alert_timestampV.text = data.timestamp
            buildTransRow(data, v)
        })

        val recyclerV = view as RecyclerView
        recyclerV.layoutManager = LinearLayoutManager(activity)
        recyclerV.adapter = adapter
        recyclerV.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        recyclerV.itemAnimator = DefaultItemAnimator()
    }

    private fun buildTransRow(data: History, v: View) {
        v.ui_row_alert_headingTv.setTextColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
        v.ui_row_alert_headingTv.text = "Kit Tampered"
        val details = "Kit ID ${data.kit_id} and kit Type ${data.kit_type} has been tampered under the ownership of ${data.login_name}."
//        v.ui_row_alert_detailTv.text = details
        setBlackForWords(v.ui_row_alert_detailTv, details, data.kit_id, data.kit_type, data.login_name)

        Picasso.with(v.context).load("file:///android_asset/imgs/${data.receiver_name}.jpg").into(v.ui_row_alert_imgV)
    }
}