package android.kuva.com.recomsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by KuVa on 21.05.2015.
 */
public class PlaceMapActivity extends AppCompatActivity
{

    public static final String PLACES_IDS = "PLACES_IDS";

    private MapView mapView;
    private GoogleMap map;
    private ArrayList<Place> places;
    private ArrayList<Integer> ids;
    private Locator locator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_map);

        locator = Locator.getInstance(this);

        ids = getIntent().getIntegerArrayListExtra(PLACES_IDS);
        places = new ArrayList<>();
        PlacesFactory placesFactory = PlacesFactory.getInstance();
        for (int i = 0; i < ids.size(); i++)
        {
            places.add(placesFactory.getPlace(ids.get(i)));
        }

        if (places.size()==1)
          setTitle(places.get(0).getName());

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        MapsInitializer.initialize(this);

        map = mapView.getMap();
        setMap();


        UiSettings settings = map.getUiSettings();
        map.setMyLocationEnabled(true);
        settings.setMyLocationButtonEnabled(true);

    }

    private void setMap()
    {

        if (places.size()==1)
        {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(places.get(0).getLocation().getLatitude(), places.get(0).getLocation().getLongitude())).zoom(15).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.addMarker(new MarkerOptions().position(new LatLng(places.get(0).getLocation().getLatitude(), places.get(0).getLocation().getLongitude())).title(places.get(0).getName()));
            map.animateCamera(cameraUpdate);
        }
        else
        {
            CameraPosition cameraPosition;
            if (locator.getCurrentLocation()!=null)
            {
                cameraPosition = new CameraPosition.Builder().target(new LatLng(locator.getCurrentLocation().getLatitude(), locator.getCurrentLocation().getLongitude())).zoom(10).build();
            }
            else
            {
                cameraPosition = new CameraPosition.Builder().target(new LatLng(59.939832f, 30.31456f)).zoom(10).build();
            }
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

            map.animateCamera(cameraUpdate);

            for (int i = 0; i < places.size(); i++)
            {
                map.addMarker(new MarkerOptions().position(new LatLng(places.get(i).getLocation().getLatitude(), places.get(i).getLocation().getLongitude())).title(places.get(i).getName()));
            }

        }
    }
}
