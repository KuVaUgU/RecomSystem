package android.kuva.com.recomsearch;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by KuVa on 20.05.2015.
 */
public class PlacesListActivity extends AppCompatActivity
{

    public static final String PLACES_ID = "PLACES_ID";
    public static final String TITLE = "TITLE";

    private ArrayList<Place> places;
    private ListView listView;
    private EditText searchEditText;
    private Locator locator;
    ArrayList<Integer> ids;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);
        locator = Locator.getInstance(this);

        ids = getIntent().getIntegerArrayListExtra(PLACES_ID);
        setTitle(getIntent().getStringExtra(TITLE));

        PlacesFactory placesFactory = PlacesFactory.getInstance();
        places = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++)
        {
            places.add(placesFactory.getPlace(ids.get(i)));
        }



        PlaceAdapter adapter = new PlaceAdapter(places);
        listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Place p = ((PlaceAdapter) parent.getAdapter()).getItem(position);

                Intent i = new Intent(PlacesListActivity.this, PlaceActivity.class);
                i.putExtra(PlaceFragment.EXTRA_PLACE_ID, p.getId());
                i.putExtra(PLACES_ID, ids);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ((PlaceAdapter)listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_map_all_show:
                Intent i = new Intent(PlacesListActivity.this, PlaceMapActivity.class);
                i.putExtra(PlaceMapActivity.PLACES_IDS, ids);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class PlaceAdapter extends ArrayAdapter<Place>
    {

        public PlaceAdapter(ArrayList<Place> places)
        {
            super(PlacesListActivity.this, 0, places);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = PlacesListActivity.this.getLayoutInflater().inflate(R.layout.list_item_place, null);

            }

            Place place = getItem(position);

            TextView nameTextView = (TextView)convertView.findViewById(R.id.place_list_nameTextView);
            nameTextView.setText(place.getName());

            TextView descriptionTextView = (TextView) convertView.findViewById(R.id.place_list_item_descriptionTextView);
            String description = "";
          /*  if (place.getRecomRating()!=0)
            {
                description+=String.valueOf(place.getRecomRating());
            } */
            description = place.getDescription();
            if (description.length() > 80)
            {
                description = description.substring(0, 80);
            }
            description += "...";
            descriptionTextView.setText(description);

            TextView tagsTextView = (TextView) convertView.findViewById(R.id.place_list_item_tagsTextView);
            tagsTextView.setText(getString(R.string.list_categories) + " " + place.getTags());

            TextView distanceTextView = (TextView) convertView.findViewById(R.id.place_list_item_distanceTextView);
            if (locator.getCurrentLocation()!= null)
               distanceTextView.setText(String.format("Расстояние = %.1f км", (place.getDistance())/1000));




            ImageView imageView = (ImageView) convertView.findViewById(R.id.place_list_item_imageView);
            try
            {
                InputStream ims = getAssets().open(place.getImgSrc());
                Drawable d = Drawable.createFromStream(ims, null);

                imageView.setImageDrawable(d);

            }
            catch (IOException e)
            {

            }


            return convertView;
        }

    }
}
