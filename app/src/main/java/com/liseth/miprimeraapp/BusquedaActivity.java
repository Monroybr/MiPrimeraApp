package com.liseth.miprimeraapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BusquedaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng bogota = new LatLng(4.6097, -74.0817);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bogota, 13f));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(4.6150, -74.0800))
                .title("Veterinaria Animal Care"));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(4.6050, -74.0900))
                .title("Clínica Veterinaria San Martín"));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(4.6200, -74.0750))
                .title("Vet Salud Mascotas"));
    }
}