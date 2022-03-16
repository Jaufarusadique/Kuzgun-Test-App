package com.jaufarusadique.kouspace.kuzgunrocketteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class gps_screen extends Fragment {
    public static boolean gpsEnabled = false;
    public static TextView coordinate;
    public static View gpsView;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            // Add a marker in Sydney and move the camera
            LatLng elginkan = new LatLng(7.2946291, 80.5907617);
            LatLng KandyCityHotel = new LatLng(7.2924385, 80.6314225);

            googleMap.addMarker(new MarkerOptions().position(elginkan)
                    .title("Kandy")
                    .snippet("Jaufar's City")
            );
            googleMap.addMarker(new MarkerOptions().position(KandyCityHotel)
                    .title("Unknown")
                    .snippet("Hotel")
            );
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(elginkan, 12));
            PolylineOptions polylineOptions = new PolylineOptions().add(KandyCityHotel).add(elginkan).width(10).color(Color.RED);
            googleMap.addPolyline(polylineOptions);
            googleMap.setTrafficEnabled(true);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Please grant permission", Toast.LENGTH_SHORT).show();
                return;
            }
            googleMap.setMyLocationEnabled(true);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        gpsView     = inflater.inflate(R.layout.fragment_gps_screen, container, false);
        coordinate  = gpsView.findViewById(R.id.coordiante);
        coordinate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Toast.makeText(getContext(), coordinate.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        gpsEnabled  = true;

        return gpsView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}