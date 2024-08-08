package com.baraa.training.ecommerce.ui.auth.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baraa.training.ecommerce.databinding.CountryItemLayoutBinding
import com.baraa.training.ecommerce.ui.auth.models.CountryUIModel

class CountriesAdapter(
    private val countries: List<CountryUIModel>,
    private val countryClickListener: CountryClickListener
) : RecyclerView.Adapter<CountriesAdapter.CountryViewHolder>() {

    inner class CountryViewHolder(private val binding: CountryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(country: CountryUIModel) {
            binding.country = country
            binding.root.setOnClickListener {
                countryClickListener.onCountryClicked(country)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = CountryItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(countries[position])
    }

    override fun getItemCount(): Int = countries.size

}

interface CountryClickListener {
    fun onCountryClicked(country: CountryUIModel)
}