package com.alansnaki.samplemap.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alansnaki.samplemap.R
import com.alansnaki.samplemap.databinding.FragmentFunctionalitiesBinding
import com.alansnaki.samplemap.ui.view.ViewBuilder
import kotlin.random.Random


class FunctionalitiesFragment : Fragment() {

    private var _binding: FragmentFunctionalitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFunctionalitiesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gridLayout.alpha = 0f
        configureButtons()
    }

    private fun configureButtons() {

        fillViews(6)

        if (binding.gridLayout.isNotEmpty())
            binding.gridLayout[0].setOnClickListener {
                findNavController().navigate(R.id.mapFragment)
            }

        Log.d("GRID::", "childs count: ${binding.gridLayout.childCount}")

        randomView()
//        binding.gridLayout.alpha = 1f
    }

    private fun fillViews(count: Int) {

        val view = ViewBuilder.addItemToGrid(
            requireActivity().applicationContext,
            binding.gridLayout,
            R.drawable.ic_baseline_location_on_24,
            R.color.di_blue,
            "Ubicación"

        )
        binding.gridLayout.addView(view)

        for (i in 2..count) {
            val view = ViewBuilder.addItemToGrid(
                requireActivity().applicationContext,
                binding.gridLayout,
                null,
                null,
                null
            )
            binding.gridLayout.addView(view)
        }
    }

    private fun randomView() {
        val grid = binding.gridLayout
        val list = mutableListOf<Int>()
        for (i in 0 until grid.childCount) {
            list.add(i)
        }
        val random = Random(System.currentTimeMillis())
        var time = 200L
        grid.alpha = 1f
        while (list.isNotEmpty()) {
            Log.d("SIZE::", list.size.toString())
            var i = 0
            if (list.size > 1) {
                i = list[random.nextInt(list.size - 1)]
                list.remove(i)
            } else {
                i = list[0]
                list.clear()
            }

            Log.d("I_VALUE::", i.toString())

            time += 100L
            grid[i].alpha = 0f
            Handler(Looper.getMainLooper()).postDelayed({
                grid[i].animate().apply {
                    duration = 200 + (100 * random.nextLong(6))
                    alpha(1f)
                    start()
                }
            }, time)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}