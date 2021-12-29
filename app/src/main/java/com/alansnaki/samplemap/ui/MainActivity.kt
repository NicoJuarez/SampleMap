package com.alansnaki.samplemap.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat
import com.alansnaki.samplemap.databinding.ActivityMainBinding
import com.alansnaki.samplemap.ui.application.Permissions

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        actionBar?.let{
            it.title = "Funcionalidades"

        }
        supportActionBar?.let{
//            it.hide()
            it.title = "Funcionalidades"
            it.subtitle = "Selecciona una de las opciones"
        }


    }


    override fun onRequestPermissionsResult(
        rc: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(rc, permissions, grantResults)
        when(rc){
            Permissions.LOCATION_PERMISSION -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Concedido", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "No concedido :(", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}