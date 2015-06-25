package android.kuva.com.recomsearch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{

    private final String TAG = "MyLog";


    private Button toGoButton;
    private Button nearMeButton;
    private Button interestsButton;
    private Button categoriesButton;
    private Button test;
    public static float distance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Locator locator = Locator.getInstance(this);
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        distance = 2f;

        onButtonClick listener = new onButtonClick();

        toGoButton = (Button) findViewById(R.id.recommendations);
        toGoButton.setOnClickListener(listener);

        nearMeButton = (Button) findViewById(R.id.near_me);
        nearMeButton.setOnClickListener(listener);

        interestsButton = (Button) findViewById(R.id.interests);
        interestsButton.setOnClickListener(listener);

        categoriesButton = (Button) findViewById(R.id.categories);
        categoriesButton.setOnClickListener(listener);

        test = (Button) findViewById(R.id.test);
        test.setOnClickListener(listener);


    }

    private class onButtonClick implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.recommendations:
                    Intent i = new Intent(MainActivity.this, PlacesListActivity.class);
                    PlacesFactory placesFactory = PlacesFactory.getInstance();
                    if (placesFactory.getMean(false) < 10)
                    {
                        ArrayList<Integer> list = placesFactory.getInterestPlace();
                        if (list.isEmpty())
                        {
                            if (Locator.getInstance().getCurrentLocation()!=null)
                            {
                                i.putExtra(PlacesListActivity.PLACES_ID, placesFactory.getPlacesNearMe(1f));
                                Toast.makeText(MainActivity.this, R.string.recommend_warning2 , Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, R.string.nothing_to_recommend, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        else
                        {
                            i.putExtra(PlacesListActivity.PLACES_ID, placesFactory.getInterestPlace());
                            Toast.makeText(MainActivity.this, R.string.recommend_warning, Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        i.putExtra(PlacesListActivity.PLACES_ID, placesFactory.getRecommendations());
                    }

                    i.putExtra(PlacesListActivity.TITLE, "Рекомендации");
                    startActivity(i);

                    break;
                case R.id.near_me:
                    if (Locator.getInstance().getCurrentLocation()!=null)
                    {
                        ArrayList<Integer> nearMe = PlacesFactory.getInstance().getPlacesNearMe(distance);

                        if (nearMe.size()!=0)
                        {
                            Intent i2 = new Intent(MainActivity.this, PlacesListActivity.class);
                            i2.putExtra(PlacesListActivity.PLACES_ID, nearMe);
                            i2.putExtra(PlacesListActivity.TITLE, "Рядом со мной");
                            startActivity(i2);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, R.string.nothing_near_you, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, R.string.finding_youre_coordinates , Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.interests:
                    Intent i3 = new Intent(MainActivity.this, InterestsActivity.class);
                    startActivity(i3);
                    break;
                case R.id.categories:
                    Intent i4 = new Intent(MainActivity.this, CategoriesActivity.class);
                    startActivity(i4);
                    break;
                case R.id.test:
                    DBHelper dbHelper = DBHelper.getInstance(MainActivity.this);
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    database.execSQL("DROP TABLE IF EXISTS " + "places");
                    database.execSQL("DROP TABLE IF EXISTS " + "interests");
                    dbHelper.onCreate(database);
                    database = dbHelper.getWritableDatabase();

                    Log.d(TAG, "--- ВСЕ ЗАПИСИ ---");
                    Cursor cursor = database.rawQuery("SELECT * from places", null);
                    if (cursor != null)
                    {
                        if (cursor.moveToFirst())
                        {
                            String str;
                            do
                            {
                                str = "";
                                for (String cn : cursor.getColumnNames())
                                {
                                    str = str.concat(cn + " = " + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                                }
                                Log.d(TAG, str);
                            } while (cursor.moveToNext());
                        } else
                            Log.d(TAG, "Cursor is null");

                        cursor.close();

                        dbHelper.close();

                    }
                    break;

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            final EditText edittext= new EditText(MainActivity.this);
            alert.setTitle(R.string.near_me_function);
            alert.setMessage(R.string.enter_distance);
            edittext.setText(String.valueOf(distance));


            alert.setView(edittext);

            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    try
                    {
                        String s = edittext.getText().toString();
                        float x = Float.parseFloat(s);
                        if (x > 20f)
                        {
                            Toast.makeText(MainActivity.this, R.string.distance_too_big, Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (x < 0)
                        {
                            Toast.makeText(MainActivity.this, R.string.error_number, Toast.LENGTH_LONG).show();
                        }

                        distance = x;
                    } catch (NumberFormatException e)
                    {
                        Toast.makeText(MainActivity.this, R.string.entry_number, Toast.LENGTH_LONG).show();
                    }

                }
            });

            alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {

                }
            });

            alert.show();
            return true;
        }

        else if (id == R.id.gps_settings)
        {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        return super.onOptionsItemSelected(item);
    }
}
