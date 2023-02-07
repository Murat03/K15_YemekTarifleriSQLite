package com.muratipek.k15_yemektariflerisqlite

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.muratipek.k15_yemektariflerisqlite.databinding.FragmentDetailsBinding
import java.io.ByteArrayOutputStream

class DetailsFragment : Fragment() {
    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null

    private lateinit var binding : FragmentDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {
            kaydet(it)
        }
        binding.imageView.setOnClickListener {
            gorselSec(it)
        }
        arguments?.let {
            val secilenID = DetailsFragmentArgs.fromBundle(it).foodId
            if(secilenID != -1){
                binding.button.visibility = View.INVISIBLE
                //bir yemeği seçti
                context?.let {
                    try {
                        val database = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE, null)
                        val sqlString = "SELECT * FROM yemekler WHERE id = ?"
                        val cursor = database.rawQuery("SELECT * FROM yemekler WHERE id = ?", arrayOf(secilenID.toString()))

                        val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                        val yemekMalzemeIndex = cursor.getColumnIndex("yemekmalzemesi")
                        val yemekGorseliIndex = cursor.getColumnIndex("gorsel")
                        while (cursor.moveToNext()){
                            binding.yemekIsmiText.setText(cursor.getString(yemekIsmiIndex))
                            binding.yemekMalzemeText.setText(cursor.getString(yemekMalzemeIndex))

                            val byteDizisi = cursor.getBlob(yemekGorseliIndex)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi, 0, byteDizisi.size)
                            binding.imageView.setImageBitmap(bitmap)
                        }
                        cursor.close()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }else{
                //yeni yemek eklemeye geldi
                binding.yemekIsmiText.setText("")
                binding.yemekMalzemeText.setText("")
                binding.button.visibility = View.VISIBLE
                val gorselSecmeArkaPLani = BitmapFactory.decodeResource(context?.resources, R.drawable.selectimage)
                binding.imageView.setImageBitmap(gorselSecmeArkaPLani)
            }
        }
    }
    fun kaydet(view: View){
        //SQLite'a Kaydetme
        val yemekIsmi = binding.yemekIsmiText.text.toString()
        val yemekMalzemeleri = binding.yemekMalzemeText.text.toString()
        if(secilenBitmap != null){
            val kucukBitmap = makeSmallerBitmap(secilenBitmap!!, 300)

            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteDizisi = outputStream.toByteArray()

            try {
                context?.let {
                    val database = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler (id INTEGER PRIMARY KEY, yemekismi VARCHAR, yemekmalzemesi VARCHAR, gorsel BLOB)")

                    val sqlString = "INSERT INTO yemekler (yemekismi, yemekmalzemesi, gorsel) VALUES (?, ?, ?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1, yemekIsmi)
                    statement.bindString(2, yemekMalzemeleri)
                    statement.bindBlob(3, byteDizisi)
                    statement.execute()
                }

            }catch (e: Exception){
                e.printStackTrace()
            }
            val action = DetailsFragmentDirections.actionDetailsFragmentToListFragment()
            Navigation.findNavController(view).navigate(action)

        }
    }
    fun gorselSec(view: View){
        activity?.let {
            if(ContextCompat.checkSelfPermission(it.applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //izin verilmedi, izin iste
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)

            }else{
                //izin verilmiş galeriye git
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            secilenGorsel = data.data
            try{
                context?.let {
                    if(secilenGorsel != null){
                        //API kontrolü nasıl yapılır
                        /*
                        if(Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }else{
                            secilenBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, secilenGorsel)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                         */
                        val source = ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                        secilenBitmap = ImageDecoder.decodeBitmap(source)
                        binding.imageView.setImageBitmap(secilenBitmap)
                    }
                }

            }catch (e : Exception){
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    fun makeSmallerBitmap(image:Bitmap, maximumSize: Int): Bitmap{

        var width = image.width
        var height = image.height

        val bitmapRatio: Double = width.toDouble() /height.toDouble()
        if(bitmapRatio>1){
//landscape
            width = maximumSize
            val scaledHeight = maximumSize/bitmapRatio
            height = scaledHeight.toInt()
        }else{
//portrait
            height = maximumSize
            val scaledWidth = maximumSize*bitmapRatio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)
    }
}