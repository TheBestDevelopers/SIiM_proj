package com.thebestdevelopers.exifphotogallery.fragments.photodetails

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.thebestdevelopers.exifphotogallery.R

class DetailsRecyclerViewAdapter(private val mItems: List<DetailContent.DetailItem>) :
    RecyclerView.Adapter<DetailsRecyclerViewAdapter.DetailHolder>(), Filterable {

    private var mFilteredItems = mItems
    override fun getItemCount() = mFilteredItems.size
    private var holders: MutableMap<String, DetailHolder> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsRecyclerViewAdapter.DetailHolder {
        val inflatedView = parent.inflate(R.layout.detail_list_item, false)
        return DetailHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: DetailsRecyclerViewAdapter.DetailHolder, position: Int) {
        holder.mItem = mFilteredItems[position]
        holder.fillValues()
        holder.mItem.let { holders[holder.mItem!!.tag] = holder }
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val sequence = charSequence.toString()
                mFilteredItems = if (sequence.isEmpty())
                    mItems
                else
                    mItems.filter { it.description.toLowerCase().contains(sequence.toLowerCase()) }
                val filterResults = Filter.FilterResults()
                filterResults.values = mFilteredItems
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                mFilteredItems = filterResults?.values as ArrayList<DetailContent.DetailItem>
                notifyDataSetChanged()
            }
        }
    }

    fun saveValues(){
        this.holders.map { it.value.saveValue() }
    }

    class DetailHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mDetailItem: TextView = mView.findViewById(R.id.mDetailItem) as TextView
        val mDetailItemValue: EditText = mView.findViewById(R.id.mDetailItemValue) as EditText
        var mItem: DetailContent.DetailItem? = null

        init {
            this.mDetailItemValue.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    saveValue()
                    mItem?.let { this.mDetailItemValue.background.setTint(if (mItem!!.isDirty()) Color.RED else Color.GRAY)
                }
                }
            }
        }

        fun fillValues() {
            this.mDetailItem.text = mItem?.description
            this.mDetailItemValue.text = mItem?.value?.toEditable()
            mItem?.let { this.mDetailItemValue.background.setTint(if (mItem!!.isDirty()) Color.RED else Color.GRAY) }
        }

        fun saveValue() { this.mItem?.value = mDetailItemValue.text.toString()}

        companion object {
            private val DETAIL_KEY = "DETAIL"
        }
    }
}