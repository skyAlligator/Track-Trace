package com.sky.tracktracebt.fragments

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.sky.tracktracebt.*
import com.sky.tracktracebt.activities.LinearActivity
import com.sky.tracktracebt.fastscrollrecyclerview.FastScrollRecyclerViewItemDecoration

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_ownerlist.*
import kotlinx.android.synthetic.main.row_ownerlist.view.*
import org.jetbrains.anko.startActivity


class OwnersListFrag : FragmentMom(R.layout.frag_ownerlist) {
    var oList: List<ReceiverData>? = null

    override fun onBackPressed() {
        activity.finish()
    }

    override fun onViewCreated(recyclerV: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(recyclerV, savedInstanceState)
        setTitleBar("Choose Owner", false)
        setCategoryData()
        setupList()
    }

    override fun onStart() {
        super.onStart()
        hideKeyboard(activity)
    }

    private fun setCategoryData() {
        when {

            AppData.loginRole.contains(AppData.SUPPLY_MANAGER, true) -> {
                ui_ownerlist_cat_titleTv.text = AppData.FLEET_MANAGER
                oList = AppData.fleetManager
            }
            AppData.loginRole.contains(AppData.FLEET_MANAGER, true) -> {
                ui_ownerlist_cat_titleTv.text = AppData.OPERATIONS_MANAGER
                oList = AppData.operationsManager
            }
            AppData.loginRole.contains(AppData.OPERATIONS_MANAGER, true) -> {
                ui_ownerlist_cat_titleTv.text = AppData.PURCHASING_MANAGER
                oList = AppData.purchasingManager
            }
        }

        ui_ownerList_manufCountTV.text = "${oList?.size} Owners"
        ui_ownerlist_cat_manuContainerV.setOnClickListener {
            setupList()
        }
    }

    /**
     * update the list adapter and set the search functionality
     */
    private fun setupList() {
        val li = oList!!
        val adapter = getAlphaAdapter(li)

        ui_ownerList_recyV.layoutManager = LinearLayoutManager(activity)
        ui_ownerList_recyV.adapter = adapter

        val letterDecoration = FastScrollRecyclerViewItemDecoration(activity)
        val dividerItemDecoration = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        ui_ownerList_recyV.addItemDecoration(letterDecoration)
        ui_ownerList_recyV.addItemDecoration(dividerItemDecoration)
        ui_ownerList_recyV.itemAnimator = DefaultItemAnimator()

        ui_ownerList_recyV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                hideKeyboard(activity)
            }
        })

        ui_findOwnerField.setTextChangeListener { text ->
            val list = li.filter { it.actualName.toLowerCase().contains(text.toLowerCase()) }

            if (list.isEmpty()) ui_ownerList_titleV.text = "No Data Found"
            else ui_ownerList_titleV.text = "Owners List"
            adapter.updateList(list)
        }
    }

    private fun getAlphaAdapter(li: List<ReceiverData>): AlphabetAdapter<ReceiverData> {
        return AlphabetAdapter(R.layout.row_ownerlist, li, { v, data ->
            //            v.ui_ownLsItemV_nameV.text = data.actualName
            setFirstWordBlack(v.ui_ownLsItemV_nameV, data.actualName)
            v.ui_ownLsItemV_phoneV.text = data.phone
            Picasso.with(v.context).load("file:///android_asset/imgs/${data.actualName}.jpg").into(v.ui_ownLsItemV_profileV)

            v.setOnClickListener {
                AppData.receiverData = data
                startActivity<LinearActivity>("Transfer" to true)
            }
        })
    }
}
