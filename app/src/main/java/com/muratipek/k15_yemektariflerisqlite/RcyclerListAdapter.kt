package com.muratipek.k15_yemektariflerisqlite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.muratipek.k15_yemektariflerisqlite.databinding.RecyclerRowBinding

class RecyclerListAdapter(val foodList: ArrayList<String>, val foodIdList: ArrayList<Int>) : RecyclerView.Adapter<RecyclerListAdapter.FoodHolder>() {
    class FoodHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = foodList.get(position)
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToDetailsFragment(foodIdList.get(position))
            Navigation.findNavController(it).navigate(action)
        }
    }
}