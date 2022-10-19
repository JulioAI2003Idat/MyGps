package com.example.mygps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mygps.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap

class MainActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnMyLocationClickListener {

    lateinit var  map:GoogleMap
    companion object{
        const val  REQUEST_CODE_LOCATION = 0
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)
        createMapFragment()

    }

    private fun createMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        createPolyLines()
        enableLocation()

    }

    private fun createPolyLines() {
        val polylinesOptions = PolylineOptions()
            .add(LatLng(-11.99591480723795, -77.05528683499459))
            .add(LatLng( -12.009011680050017, -77.05442852818129))
            .add(LatLng(-12.011026527055392, -77.08052105530574))
            .add(LatLng( -12.022443708608225, -77.05305523728))
            .add(LatLng(-12.046787550695964, -77.05065197820275 ))
            .add(LatLng(-12.026808973540003, -77.03657574646456 ))
            .add(LatLng(-12.038393371498566, -77.01820798065987 ))
            .add(LatLng( -12.01992525431149, -77.03434414874998))
            .add(LatLng( -12.011362333424106, -77.0228428374517))
            .add(LatLng(-12.010690720267938, -77.03915066690448))
            .add(LatLng(-11.99591480723795, -77.05528683499459))
            .width(30f)
            .color(ContextCompat.getColor(this,R.color.red))

        val polyline = map.addPolyline(polylinesOptions)
        //polyline.startCap = RoundCap() //forma de inicio del marco de maps
        //polyline.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_star_24))//forma de fin del marco de maps

        val pattern = listOf(
            //punto, espacio, guion
            Dot(),Gap(10f),Dash(50f),Gap(10f) //diseño para el cuerpo del polyline
        )
        polyline.pattern = pattern

        polyline.isClickable = true
        map.setOnPolylineClickListener {  polyline ->changeColor(polyline)}
    }
    fun changeColor(polyline: Polyline){
        val color = (0..3).random()
        when(color){
            0 -> polyline.color = ContextCompat.getColor(this,R.color.red)
            1 -> polyline.color = ContextCompat.getColor(this,R.color.white)
            2 -> polyline.color = ContextCompat.getColor(this,R.color.black)
            3 -> polyline.color = ContextCompat.getColor(this,R.color.purple_200)
        }
    }

    private fun createMarker() {
        val coordinates = LatLng(-12.046749682145187, -77.04134624599482)
        val marker = MarkerOptions().position(coordinates).title("EL centro de lima")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates,18f),
            4000,null
        )
    }
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED

    private fun enableLocation(){
        //si el mapa no ah sido inicializado
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()){
            map.isMyLocationEnabled = true
        }else{
            requestLocationPermission()
        }
    }
    private fun requestLocationPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this,"Ve a ajustes y acepta los permisos",Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){ //si la variable de los permisos no es vacia y el permiso fue aceptado
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
                }else{
                Toast.makeText(this,"Para activar la localizacion ve a ajustes y acepta los permisos",Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this,"Para activar la localizacion ve a ajustes y acepta los permisos",Toast.LENGTH_SHORT).show()

        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this,"Boton pulsado",Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this,"Estas en ${p0.latitude} , ${p0.longitude}",Toast.LENGTH_SHORT).show()

    }
}