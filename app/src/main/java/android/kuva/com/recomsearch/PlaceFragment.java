package android.kuva.com.recomsearch;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by KuVa on 21.05.2015.
 */
public class PlaceFragment extends Fragment
{
    private final String TAG = "myLogs";
    public static final String EXTRA_PLACE_ID = "EXTRA_PLACE_ID";

    private Place place;


    private TextView nameTextView;
    private ImageView imageView;
    private TextView descriptionTextView;
    private Button toMapButton;
    private RatingBar ratingBar;

    Locator locator;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        locator = Locator.getInstance(getActivity());

        int placeId = getArguments().getInt(EXTRA_PLACE_ID);
        place = PlacesFactory.getInstance().getPlace(placeId);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_place, container, false);

        nameTextView = (TextView)v.findViewById(R.id.place_nameTextView);
        nameTextView.setText(place.getName());

        imageView = (ImageView)v.findViewById(R.id.place_imageView);
        try
        {
            InputStream ims = getActivity().getAssets().open(place.getImgSrc());
            Drawable d = Drawable.createFromStream(ims, null);

            imageView.setImageDrawable(d);

        }
        catch (IOException e)
        {

        }

        descriptionTextView = (TextView)v.findViewById(R.id.place_descriptionTextView);
        descriptionTextView.setText("  " + place.getDescription());

        toMapButton = (Button)v.findViewById(R.id.place_toMapButton);
        toMapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getActivity(), PlaceMapActivity.class);
                ArrayList<Integer> list = new ArrayList<>();
                list.add(place.getId());
                i.putExtra(PlaceMapActivity.PLACES_IDS, list);
                startActivity(i);
            }
        });

        ratingBar = (RatingBar)v.findViewById(R.id.place_ratingBar);
        ratingBar.setRating(place.getRating());
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
                place.setRating(rating);
                Log.d(TAG,"--- onDestroyView ---");
                DBHelper dbHelper = DBHelper.getInstance();
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.execSQL("UPDATE places SET rating=" + place.getRating() + " WHERE id=" + place.getId());
                dbHelper.close();

            }
        });

        return v;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();


    }

    public static PlaceFragment newInstance(int placeId)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_PLACE_ID, placeId);
        PlaceFragment fragment = new PlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
