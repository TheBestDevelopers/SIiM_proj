package com.thebestdevelopers.exifphotogallery.fragments.photodetails

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.thebestdevelopers.exifphotogallery.R
import java.util.*
import com.thebestdevelopers.exifphotogallery.fragments.photodetails.DetailsSettingsContent.ListElement

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
        holder.mItem.let { holders[holder.mItem!!.parameter.tag] = holder }
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

    fun saveValues() {
        this.holders.map { it.value.saveValue() }
    }

    class DetailHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mDetailItem: TextView = mView.findViewById(R.id.mDetailItem) as TextView
        val mDetailItemValueEditText: EditText = mView.findViewById(R.id.mDetailItemValue) as EditText
        val mDetailItemValueSpinner: Spinner = mView.findViewById(R.id.mDetailItemValueSpinner) as Spinner
        var mDetailItemValueView: View = mDetailItemValueEditText
        var mItem: DetailContent.DetailItem? = null

        init {
            this.mDetailItemValueEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    saveValue()
                    changeColor()
                }
            }
            this.mDetailItemValueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    saveValue()
                    changeColor()
                }
            }
        }

        fun fillValues() {
            this.mDetailItem.text = mItem?.description
            this.mDetailItemValueView.visibility = View.GONE
            //spinner or edit text
            when (mItem?.parameter?.type) {
                //spinner
                (InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) -> {
                    this.mDetailItemValueView = this.mDetailItemValueSpinner
                    this.mDetailItemValueSpinner.adapter = ArrayAdapter<ListElement>(
                        mView.context,
                        R.layout.support_simple_spinner_dropdown_item,
                        mItem!!.parameter.values
                    )
                    this.mDetailItemValueSpinner.setSelection(mItem?.parameter?.values?.indexOfFirst { it.index.toString() == mItem?.value } ?: 0)
                }
                else -> {
                    this.mDetailItemValueView = this.mDetailItemValueEditText
                    this.mDetailItemValueEditText.inputType = mItem?.parameter!!.type
                    this.mDetailItemValueEditText.text = mItem?.value?.toEditable()
                }
            }
            this.mDetailItemValueView.visibility = View.VISIBLE
            changeColor()
        }

        fun saveValue() {
            this.mItem?.value = when (mItem?.parameter?.type) {
                (InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) ->
                    (mDetailItemValueSpinner.selectedItem as ListElement).index.toString()
                else ->
                    mDetailItemValueEditText.text.toString()
            }
        }

        fun changeColor() {
            mItem?.let { this.mDetailItemValueView.background.setTint(if (mItem!!.isDirty()) Color.RED else Color.GRAY) }
        }

        companion object {
            private val DETAIL_KEY = "DETAIL"
        }
    }
}