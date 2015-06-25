package android.kuva.com.recomsearch;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by KuVa on 22.05.2015.
 */
public class InterestsActivity extends AppCompatActivity
{

    private ListView listView;
    private ArrayList interests;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        interests = Interest.getList();
        final InterestAdapter adapter = new InterestAdapter(interests);

        listView = (ListView) findViewById(R.id.interests_list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Interest interest = ((InterestAdapter) parent.getAdapter()).getItem(position);
                boolean like = interest.isLike();

                interest.setLike(!like);
                ((CheckBox) view.findViewById(R.id.interests_checkBox)).setChecked(!like);

                DBHelper dbHelper = DBHelper.getInstance();
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                int bool;
                if (interest.isLike())
                {
                    bool = 1;
                }
                else
                {
                    bool = 0;
                }
                database.execSQL("UPDATE interests SET bool=" + bool + " WHERE tag='" + interest.getTag() + "'");
                dbHelper.close();

            }
        });

    }

    private class InterestAdapter extends ArrayAdapter<Interest>
    {

        public InterestAdapter(ArrayList<Interest> interests)
        {
            super(InterestsActivity.this, 0, interests);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = InterestsActivity.this.getLayoutInflater().inflate(R.layout.list_item_interests, null);

            }

            Interest interest = getItem(position);

            TextView nameTextView = (TextView)convertView.findViewById(R.id.interests_textView);
            nameTextView.setText(interest.getTag());

            CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.interests_checkBox);
            checkBox.setChecked(interest.isLike());

            return convertView;
        }
    }
}
