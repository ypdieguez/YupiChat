package com.sapp.yupi.ui.appintro.world

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.databinding.CountryListDialogItemWorldBinding
import com.sapp.yupi.databinding.CountryListDialogWorldBinding
import com.sapp.yupi.ui.appintro.world.data.Country
import java.util.*


class CountryListDialogFragment : BottomSheetDialogFragment() {

    interface Listener {
        fun onCountryClicked(country: Country)
    }

    private var mListener: Listener? = null

    private var mAdapter = CountryAdapter()
    private lateinit var mBinding: CountryListDialogWorldBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = CountryListDialogWorldBinding.inflate(inflater, container, false)
        mBinding.apply {
            list.layoutManager = LinearLayoutManager(context)
            list.adapter = mAdapter
            divider.animate().alpha(1f).duration = 1000

            return root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LoadCountriesAsyncTask().execute()
    }

    fun addListener(listener: Listener): CountryListDialogFragment {
        mListener = listener
        return this
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class LoadCountriesAsyncTask : AsyncTask<Void, Void, MutableList<Country>>() {

        override fun onPreExecute() {
            mBinding.apply {
                list.visibility = View.GONE
                spinKit.visibility = View.VISIBLE
            }
        }

        override fun doInBackground(vararg param: Void): MutableList<Country> {
            val mPhoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
            val countries: MutableList<Country> = mutableListOf()

            for (region in mPhoneUtil.supportedRegions) {
                val name = Locale(Locale.getDefault().language, region).displayCountry
                val code = mPhoneUtil.getCountryCodeForRegion(region)
                countries.add(Country(code, region, name))
            }
            countries.sortBy { it.name }

            return countries
        }

        override fun onPostExecute(result: MutableList<Country>) {
            mAdapter.setData(result)

            mBinding.apply {
                spinKit.visibility = View.GONE
                list.visibility = View.VISIBLE
            }
        }
    }

    private inner class ViewHolder internal constructor(binding: CountryListDialogItemWorldBinding)
        : RecyclerView.ViewHolder(binding.root) {
        internal val countryView: TextView = binding.country
        internal val codeView: TextView = binding.code
    }

    private inner class CountryAdapter : RecyclerView.Adapter<ViewHolder>() {
        internal val mCountries: MutableList<Country> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = CountryListDialogItemWorldBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val country = mCountries[position]

            holder.countryView.text = country.name
            holder.codeView.text = "+${country.code}"
            holder.itemView.setOnClickListener {
                mListener?.apply {
                    onCountryClicked(country)
                    dismiss()
                }
            }
        }

        override fun getItemCount(): Int {
            return mCountries.size
        }

        fun setData(countries: MutableList<Country>) {
            mCountries.addAll(countries)
            notifyDataSetChanged()
        }
    }

    companion object {
        fun newInstance() = CountryListDialogFragment()

    }
}
