package com.jinvita.mykakaomap.ui.markerlistview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jinvita.mykakaomap.R
import com.jinvita.mykakaomap.databinding.MarkerListFooterBinding
import com.jinvita.mykakaomap.databinding.MarkerListItemBinding
import com.jinvita.mykakaomap.model.data.Place
import com.jinvita.mykakaomap.ui.viewmodel.MapViewModel

class MarkerListAdapter(private val context: Context, private val viewModel: MapViewModel) :
    RecyclerView.Adapter<MarkerViewHolder>() {
    companion object {
        private const val TAG = "KM/MarkerListAdapter"

        const val VIEW_TYPE_ITEM = 1
        const val VIEW_TYPE_FOOTER = 0
        const val NOT_SELECTED = -1
    }

    private var dataList = ArrayList<Place>()
    private var isNoNeedToMoreButton = false
    private var selectedPosition = NOT_SELECTED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        return if (viewType == VIEW_TYPE_ITEM)
            MarkerViewHolder(
                0, MarkerListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ) else MarkerViewHolder(
            1,
            MarkerListFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.itemView.setBackgroundColor(
            if (selectedPosition == position)
                ContextCompat.getColor(context, R.color.yellow_selected) else Color.TRANSPARENT
        )

        if (position == itemCount - 1) {
            holder.setMoreInfoButton(
                isNoNeedToMoreButton
            ) { viewModel.setNextPage() }
        } else {
            holder.setText(
                dataList[position].category_name,
                dataList[position].place_name,
                dataList[position].address_name
            )

            holder.itemView.setOnClickListener {
                viewModel.setMarkerItem(dataList[position].place_url)
                Log.d(TAG, "Click index : $position, data : $dataList")

                if (selectedPosition != NOT_SELECTED) {
                    notifyItemChanged(selectedPosition)
                }
                selectedPosition = position
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            itemCount - 1 -> VIEW_TYPE_FOOTER
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        val count = dataList.size

        return if (count > 0) {
            count + 1
        } else {
            count
        }
    }

    fun setDataList(list: ArrayList<Place>, isEnd: Boolean) {
        dataList = list

        isNoNeedToMoreButton = isEnd
        selectedPosition = NOT_SELECTED
        notifyDataSetChanged()
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun setSelectItem(url: String) {
        if (selectedPosition != NOT_SELECTED) {
            notifyItemChanged(selectedPosition)
        }
        selectedPosition = findSelectedPosition(url)
        if (selectedPosition != NOT_SELECTED) {
            notifyItemChanged(selectedPosition)
        }
    }

    private fun findSelectedPosition(url: String): Int {
        val position = NOT_SELECTED
        for (data in dataList.withIndex()) {
            if (data.value.place_url == url) {
                return data.index
            }
        }
        return position
    }
}