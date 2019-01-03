package com.sapp.yupi.ui.appintro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sapp.yupi.R
import kotlinx.android.synthetic.cuba.country_list_dialog.*
import kotlinx.android.synthetic.cuba.country_list_dialog_item.view.*

class CountryListDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.country_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = CountryAdapter()
    }

    fun addListener(listener: Listener): CountryListDialogFragment {
        mListener = listener
        return this
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onCountryClicked(position: Int)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.country_list_dialog_item,
            parent, false)) {

        internal val text: TextView = itemView.text

        init {
            text.setOnClickListener {
                mListener?.apply {
                    onCountryClicked(adapterPosition)
                    dismiss()
                }
            }
        }
    }

    private inner class CountryAdapter : RecyclerView.Adapter<ViewHolder>() {
        val mCountries: Array<String> = resources.getStringArray(R.array.countries_name)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = mCountries[position]
        }

        override fun getItemCount(): Int {
            return mCountries.size
        }
    }

    companion object {
        fun newInstance() = CountryListDialogFragment()

    }
}
