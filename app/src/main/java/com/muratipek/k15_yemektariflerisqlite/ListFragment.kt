package com.muratipek.k15_yemektariflerisqlite

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.muratipek.k15_yemektariflerisqlite.databinding.FragmentListBinding

class ListFragment : Fragment() {

    var yemekIsmiListesi = ArrayList<String>()
    var yemekIdListesi = ArrayList<Int>()
    private lateinit var listAdapter : RecyclerListAdapter
    private lateinit var binding: FragmentListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAdapter = RecyclerListAdapter(yemekIsmiListesi, yemekIdListesi)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = listAdapter

        sqlVeriAlma()
    }
    fun sqlVeriAlma(){
        try{
            activity?.let {
                val database = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE, null)
                val cursor = database.rawQuery("SELECT * FROM yemekler", null)
                val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                val yemekIdIndex = cursor.getColumnIndex("id")

                yemekIsmiListesi.clear()
                yemekIdListesi.clear()

                while (cursor.moveToNext()){
                    yemekIdListesi.add(cursor.getInt(yemekIdIndex))
                    yemekIsmiListesi.add(cursor.getString(yemekIsmiIndex))
                }
                listAdapter.notifyDataSetChanged()
                cursor.close()
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}