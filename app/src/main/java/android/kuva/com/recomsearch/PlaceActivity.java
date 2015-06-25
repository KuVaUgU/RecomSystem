package android.kuva.com.recomsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import java.util.ArrayList;

/**
 * Created by KuVa on 20.05.2015.
 */
public class PlaceActivity extends AppCompatActivity
{


    private ViewPager viewPager;
    private ArrayList<Place> places;
    private ArrayList<Integer> ids;

    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        viewPager = new ViewPager(this);
        viewPager.setId(R.id.viewPager);
        setContentView(viewPager);

        ids = getIntent().getIntegerArrayListExtra(PlacesListActivity.PLACES_ID);

        PlacesFactory placesFactory = PlacesFactory.getInstance();
        places = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++)
        {
            places.add(placesFactory.getPlace(ids.get(i)));
        }

        fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager)
        {
            @Override
            public Fragment getItem(int position)
            {
                Place place = places.get(position);
                return PlaceFragment.newInstance(place.getId());
            }

            @Override
            public int getCount()
            {
                return places.size();
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                Place place = places.get(position);
                setTitle(place.getName());

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        int placeId = getIntent().getIntExtra(PlaceFragment.EXTRA_PLACE_ID, 0);

        for (int i = 0; i < places.size(); i++)
        {
            if (places.get(i).getId()==placeId)
            {
                viewPager.setCurrentItem(i);
                setTitle(places.get(i).getName());
                break;
            }
        }



    }
}
