package android.kuva.com.recomsearch;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by KuVa on 23.05.2015.
 */
public class CategoriesActivity extends AppCompatActivity
{
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        listView = (ListView) findViewById(R.id.categories_list);
        CategoriesAdapter adapter = new CategoriesAdapter(Interest.getCategories());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String s = ((CategoriesAdapter)parent.getAdapter()).getItem(position);
                Intent i = new Intent(CategoriesActivity.this, PlacesListActivity.class);
                i.putExtra(PlacesListActivity.PLACES_ID, PlacesFactory.getInstance().getPlacesByCategories(s));
                i.putExtra(PlacesListActivity.TITLE, s);
                startActivity(i);
            }
        });
    }

    private class CategoriesAdapter extends ArrayAdapter<String>
    {

        public CategoriesAdapter(ArrayList<String> categories)
        {
            super(CategoriesActivity.this, 0, categories);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = CategoriesActivity.this.getLayoutInflater().inflate(R.layout.list_item_categories, null);

            }

            String s = getItem(position);
            TextView categoriesTextView = (TextView)convertView.findViewById(R.id.categories_textView);
            categoriesTextView.setText(s);

            return convertView;
        }
    }
}
