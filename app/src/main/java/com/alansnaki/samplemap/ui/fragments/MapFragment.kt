package com.alansnaki.samplemap.ui.fragments

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.alansnaki.samplemap.R
import com.alansnaki.samplemap.databinding.FragmentMapBinding
import com.alansnaki.samplemap.ui.application.Permissions
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var fabLocationClicked = false
    private var locationMarker: Marker? = null
    private var mHandler: Handler? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.let {
            getInstance().load(
                it.applicationContext,
                getDefaultSharedPreferences(it.applicationContext)
            )
        }
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        configureMap()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            if (it is AppCompatActivity)
                it.supportActionBar?.let { bar ->
                    bar.title = "Map"
                    bar.subtitle = "Busca tu ubicación"
                }
        }

        val fineLocationPermission = ContextCompat.checkSelfPermission(
            view.context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            configureFab()
            binding.fabLocation.visibility = View.VISIBLE
        } else
            activity?.requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                Permissions.LOCATION_PERMISSION
            )

    }

    private fun configureFab() {
        binding.fabLocation.setOnClickListener {

            fabLocationClicked = true
            locationMarker?.alpha = 0f
            it.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE

            val locationOverlay =
                MyLocationNewOverlay(GpsMyLocationProvider(context), binding.map);
            locationOverlay.enableMyLocation();
            locationOverlay.enableFollowLocation()

            if (locationMarker == null) {
                locationMarker = Marker(binding.map).apply {
                    val drawable = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_baseline_person_pin_circle_24
                    )?.apply {
                        setTint(resources.getColor(R.color.di_blue, null))
                    }
                    icon = drawable
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Estás aquí :)"
                }
                if (locationMarker != null)
                    binding.map.overlays.add(locationMarker)
            }

            findLoc(locationOverlay)


        }
    }

    private fun findLoc(locationOverlay: MyLocationNewOverlay) {
        locationOverlay.runOnFirstFix {
            fabLocationClicked = false
            Handler(Looper.getMainLooper()).post {
                _binding?.let {
                    binding.map.invalidate()
                }

                locationMarker?.let {
                    it.position = locationOverlay.myLocation
                    it.alpha = 1f
                    it.showInfoWindow()
                }
                _binding?.let {
                    binding.map.controller.animateTo(
                        locationOverlay.myLocation,
                        22.0, 1000
                    )
                }

                locationOverlay.disableFollowLocation()
                locationOverlay.disableMyLocation()
                val looper = Looper.myLooper()

                _binding?.let {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.fabLocation.visibility = View.VISIBLE
                }

                looper?.let {
                    mHandler = Handler(it)
                    mHandler?.post(object : Runnable {
                        override fun run() {
                            _binding?.let {
                                var orientation = binding.map.mapOrientation + .2f

                                if (orientation > 360)
                                    orientation -= 360

                                binding.map.mapOrientation = orientation
                            }
                            if (!fabLocationClicked)
                                mHandler?.post(this) ?: Log.d(
                                    "mHandler::",
                                    "Terminando ejecución de Handler"
                                )
                            else {
                                binding.map.mapOrientation = 0f
                                locationMarker?.closeInfoWindow()
                                fabLocationClicked = false
                            }

                        }

                    })
                }
            }
        }
    }

    private fun configureMap() {
        val map = binding.map
        val mapController = map.controller

        map.setTileSource(TileSourceFactory.MAPNIK)
        mapController.apply {
            setZoom(16.5)
            setCenter(GeoPoint(-24.844027, -65.462983))
        }
        map.setMultiTouchControls(true)


    }

    private fun getBitmap(): Bitmap? {
        var drawable: Drawable? = null
        try {
            drawable =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_person_pin_circle_24,
                    null
                )
            drawable?.setTint(ResourcesCompat.getColor(resources, R.color.di_blue, null))
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        drawable?.let {
            val bitmap =
                Bitmap.createBitmap(
                    it.intrinsicWidth,
                    it.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)

            return bitmap
        }

        return null
    }


    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mHandler = null
    }
}