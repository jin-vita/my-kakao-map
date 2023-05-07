package com.jinvita.mykakaomap.ui.mapview

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.jinvita.mykakaomap.MyApp
import com.jinvita.mykakaomap.R
import com.jinvita.mykakaomap.databinding.FragmentMapBinding
import com.jinvita.mykakaomap.model.data.CategorySearchData
import com.jinvita.mykakaomap.model.repository.MapRepository
import com.jinvita.mykakaomap.ui.viewmodel.MapViewModel
import com.jinvita.mykakaomap.ui.viewmodel.MapViewModelFactory
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPOIItem.ImageOffset
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Response

class MapFragment : Fragment(), MapView.CurrentLocationEventListener, MapView.POIItemEventListener,
    MapView.MapViewEventListener {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "KM/MapFragment"
    }

    private lateinit var mContext: Context
    private lateinit var mMapView: MapView
    private var isCategorySelected = false
    var isTrackingMode = false
    private val mViewModel: MapViewModel by activityViewModels {
        MapViewModelFactory(
            MapRepository()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMapView()
        initButtonView()
        addObserver()
    }

    private fun initMapView() {
        mMapView = MapView(activity)

        val mapViewContainer = binding.mapView as ViewGroup

        MapView.setMapTilePersistentCacheEnabled(true)
        mMapView.isHDMapTileEnabled = true

        val x = MyApp.getPrefs().getString("longitude", (127.04615783691406).toString())
        val y = MyApp.getPrefs().getString("latitude", (37.29035568237305).toString())

        mViewModel.setXY(x, y)
        mMapView.setMapCenterPointAndZoomLevel(
            MapPoint.mapPointWithGeoCoord(
                y.toDouble(), x.toDouble()
            ), 1, false
        )
        if (checkLocationService()) {
            startTracking(true)
        }

        mMapView.setMapViewEventListener(this)
        mMapView.setCurrentLocationEventListener(this)
        mMapView.setPOIItemEventListener(this)

        mapViewContainer.addView(mMapView)
    }

    private fun initButtonView() {
        binding.searchRefreshBtn.setOnClickListener {
            mViewModel.setRefresh()
            binding.searchRefreshBtn.visibility = View.GONE
        }

        binding.searchCafeBtn.setOnClickListener(listener)
        binding.searchHospitalBtn.setOnClickListener(listener)
        binding.searchPharmacyBtn.setOnClickListener(listener)
        binding.searchGasBtn.setOnClickListener(listener)

        binding.trackingBtn.setOnClickListener {
            // GPS가 켜진 경우에만 트레킹모드 진입할 수 있도록
            if (checkLocationService()) {
                if (it.isSelected) {
                    stopTracking()
                    it.background =
                        ContextCompat.getDrawable(mContext, R.drawable.tracking_btn_background)
                } else {
                    startTracking(false)
                    it.background = ContextCompat.getDrawable(
                        mContext,
                        R.drawable.tracking_btn_selected_background
                    )
                }
                it.isSelected = !it.isSelected
            } else {
                Toast.makeText(mContext, "위치 설정을 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationService(): Boolean {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun startTracking(isFirstInit: Boolean) {
        if (!isFirstInit) {
            isTrackingMode = true
            binding.searchRefreshBtn.visibility = View.GONE
            Toast.makeText(
                mContext,
                "현재 위치를 따라가며 중심좌표가 변경됩니다",
                Toast.LENGTH_LONG
            ).show()
        }
        mMapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    private fun stopTracking() {
        isTrackingMode = false
        mMapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    private var listener = View.OnClickListener {
        val isSelected = it.isSelected

        if (isSelected) {
            isCategorySelected = false
            setNotSelectedDataToListFragment()
            setButtonUI(it, R.drawable.category_btn_background, isSelected)
        } else {
            isCategorySelected = true
            val categoryCode = it.tag.toString()

            if (binding.searchRefreshBtn.isVisible || isTrackingMode) {
                mViewModel.setCategoryCode(categoryCode)
                mViewModel.setRefresh()
            } else {
                mViewModel.searchCategory(categoryCode)
            }
            setButtonUI(it, R.drawable.category_btn_selected_background, isSelected)
        }
    }

    private fun setNotSelectedDataToListFragment() {
        mViewModel.setNullToLiveData()
    }

    private fun setButtonUI(view: View, drawableId: Int, isSelected: Boolean) {
        setButtonToNotSelected()

        binding.searchRefreshBtn.visibility = View.GONE

        view.background = ContextCompat.getDrawable(mContext, drawableId)
        view.isSelected = !isSelected
    }

    private fun setButtonToNotSelected() {
        binding.searchCafeBtn.background =
            ContextCompat.getDrawable(mContext, R.drawable.category_btn_background)
        binding.searchHospitalBtn.background =
            ContextCompat.getDrawable(mContext, R.drawable.category_btn_background)
        binding.searchPharmacyBtn.background =
            ContextCompat.getDrawable(mContext, R.drawable.category_btn_background)
        binding.searchGasBtn.background =
            ContextCompat.getDrawable(mContext, R.drawable.category_btn_background)

        binding.searchCafeBtn.isSelected = false
        binding.searchHospitalBtn.isSelected = false
        binding.searchPharmacyBtn.isSelected = false
        binding.searchGasBtn.isSelected = false
    }

    private fun addObserver() {
        val dataObserver: Observer<Response<CategorySearchData>?> =
            Observer { liveData ->
                mMapView.removeAllPOIItems()

                if (liveData != null && liveData.isSuccessful) {
                    // liveData 변경시(api 호출로 데이터를 가져온 후에, 추가된 마커를 보여줌)
                    for (document in mViewModel.getDataList(mViewModel.getCategoryCode())) {
                        val mapPoint = MapPoint.mapPointWithGeoCoord(
                            document.y.toDouble(),
                            document.x.toDouble()
                        )
                        addMarker(mapPoint, document.place_url)
                    }
                    if (!isTrackingMode) {
                        mMapView.fitMapViewAreaToShowAllPOIItems()
                    }
                }
            }
        mViewModel.liveData.observe(viewLifecycleOwner, dataObserver)

        mViewModel.liveMarkerItem.observe(viewLifecycleOwner) {
            val marker = mMapView.findPOIItemByName(it)[0]
            mMapView.selectPOIItem(marker, true)
            mMapView.setMapCenterPoint(marker.mapPoint, true)
        }
    }

    private fun addMarker(mapPoint: MapPoint, url: String) {
        val marker = MapPOIItem()
        marker.apply {
            itemName = url
            this@apply.mapPoint = mapPoint
            isShowCalloutBalloonOnTouch = false
            markerType = MapPOIItem.MarkerType.CustomImage
            customImageResourceId = R.drawable.pin_yellow
            selectedMarkerType = MapPOIItem.MarkerType.CustomImage
            customSelectedImageResourceId = R.drawable.pin_red
            isCustomImageAutoscale = true
        }
        mMapView.addPOIItem(marker)
    }

    override fun onStop() {
        MyApp.getPrefs().setString("longitude", mViewModel.getX())
        MyApp.getPrefs().setString("latitude", mViewModel.getY())

        super.onStop()
    }

    override fun onCurrentLocationUpdate(
        mapView: MapView?,
        currentLocation: MapPoint?,
        accuracyInMeters: Float
    ) {
        val mapPointGeo = currentLocation!!.mapPointGeoCoord
        mMapView.setMapCenterPointAndZoomLevel(currentLocation, 1, true)
        mViewModel.setXY(mapPointGeo.longitude.toString(), mapPointGeo.latitude.toString())

        Log.d(TAG, "" + mapPointGeo.longitude.toString() + "," + mapPointGeo.latitude.toString())

        // 첫 진입시의 현재위치 업데이트 경우, 한번만 업데이트하고 트래킹을 중단
        if (!isTrackingMode) {
            mMapView.currentLocationTrackingMode =
                MapView.CurrentLocationTrackingMode.TrackingModeOff
        }
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {}

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {}

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {}

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}

    override fun onPOIItemSelected(mapview: MapView?, marker: MapPOIItem?) {
        if (marker != null) {
            mMapView.setMapCenterPoint(marker.mapPoint, true)

            mViewModel.setMarkerItem(marker.itemName)
        }
    }

    override fun onMapViewInitialized(mapView: MapView?) {
        Log.d(TAG, "onMapViewInitialized")

        mMapView.setCustomCurrentLocationMarkerImage(R.drawable.dot, ImageOffset(30, 30))
        mMapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.dot, ImageOffset(30, 30))
        mMapView.setCurrentLocationRadius(15)
        mMapView.setCurrentLocationRadiusFillColor(android.graphics.Color.argb(75, 255, 82, 82))
        mMapView.setCurrentLocationRadiusStrokeColor(android.graphics.Color.argb(0, 255, 82, 82))
    }

    override fun onMapViewCenterPointMoved(mapView: MapView?, mapPoint: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(mapView: MapView?, mapPoint: MapPoint?) {
        if (mapPoint != null && !isTrackingMode) {
            mViewModel.setXY(
                mapPoint.mapPointGeoCoord.longitude.toString(),
                mapPoint.mapPointGeoCoord.latitude.toString()
            )
            if (isCategorySelected) {
                binding.searchRefreshBtn.visibility = View.VISIBLE
            }
        }
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }
}