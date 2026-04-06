package com.gas.flexapp.ui.maps

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gas.components.ComponentBuilder
import com.gas.components.maps.MapsComponent
import com.gas.flexapp.R
import com.gas.flexapp.databinding.FragmentMapsBinding
import com.gas.flexapp.viewmodels.MapsViewModel
import com.gas.model.maps.GeometryTypes
import com.gas.model.maps.PointModel
import com.gas.model.sources.ResponseTypes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val mapsViewModel: MapsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapsBinding.bind(view)

        val mapFragment = SupportMapFragment.newInstance()

        childFragmentManager.beginTransaction()
            .replace(binding.map.id, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        binding.loading.visibility = View.GONE

        val context = requireContext()

        val viewKey = arguments?.getString("viewKey")

        val contentViewModel = mapsViewModel.getContentViewModel(viewKey)
        val mapsComponent = ComponentBuilder.buildView(contentViewModel.content, context) as MapsComponent

        mapsViewModel.onList.observe(viewLifecycleOwner) {
            binding.loading.visibility = View.GONE
            if (it.isNotEmpty()) {
                val builder = LatLngBounds.Builder()
                when (mapsComponent.model.geometryType.type) {
                    GeometryTypes.POINT -> {
                        val pointModel = mapsComponent.model.geometryType as PointModel
                        it.forEach { json ->
                            val point = LatLng(json.get(pointModel.latFieldName).asDouble, json.get(pointModel.lonFieldName).asDouble)
                            val markerOptions = MarkerOptions()
                                .position(point)
                            if (!json.get(pointModel.titleFieldName).isJsonNull) {
                                markerOptions.title(json.get(pointModel.titleFieldName).asString)
                            }
                            if (!json.get(pointModel.subTitleFieldName).isJsonNull) {
                                markerOptions.snippet(json.get(pointModel.subTitleFieldName).asString)
                            }
                            googleMap.addMarker(
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            )

                            builder.include(point)
                        }
                    }
                    GeometryTypes.LINE -> {}
                    GeometryTypes.POLYGON -> {}
                }
                val bounds = builder.build()
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
            }
        }

        lifecycleScope.launch {
            when (mapsComponent.model.dataSource.responseType) {
                ResponseTypes.LIST -> {
                    binding.loading.visibility = View.VISIBLE
                    mapsViewModel.list(mapsComponent.model.dataSource)
                } else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}