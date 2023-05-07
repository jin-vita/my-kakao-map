package com.jinvita.mykakaomap.ui.markerlistview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.jinvita.mykakaomap.databinding.MarkerListFooterBinding
import com.jinvita.mykakaomap.databinding.MarkerListItemBinding

class MarkerViewHolder(setting: Int, private val binding: Any) :
    RecyclerView.ViewHolder(if (setting == 0) (binding as MarkerListItemBinding).root else (binding as MarkerListFooterBinding).root) {
    fun setText(category: String, place: String, address: String) {
        with(binding as MarkerListItemBinding) {
            categoryName.text = category
            placeName.text = place
            addressName.text = address
        }
    }

    fun setMoreInfoButton(hasToRemove: Boolean, onClickListener: View.OnClickListener) {
        with(binding as MarkerListFooterBinding) {
            if (hasToRemove) {
                moreInfoBtn.visibility = View.GONE
            } else {
                moreInfoBtn.visibility = View.VISIBLE
                moreInfoBtn.setOnClickListener(onClickListener)
            }
        }
    }
}