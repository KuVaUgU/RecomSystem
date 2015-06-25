package android.kuva.com.recomsearch;


import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by KuVa on 21.05.2015.
 */
public class DBHelper extends SQLiteOpenHelper
{

    private final static String TAG = "MyLog";


    private static DBHelper dbHelper;
    private static Context context;

    private DBHelper (Context context)
    {
        super(context, "PlaceDB", null, 1);
        this.context = context;
    }

    public static DBHelper getInstance()
    {
        return dbHelper;
    }

    public static DBHelper getInstance(Context context)
    {
        if (dbHelper == null)
            dbHelper = new DBHelper(context);

        return dbHelper;
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d(TAG, "--- onCreate database ---");

        db.execSQL("create table places (id integer primary key autoincrement, " +
                "name varchar(60), " +
                "description text," +
                "latitude float, " +
                "longitude float, " +
                "rating float, " +
                "tags text, " +
                "imgSrc varchar(30));");

        db.execSQL("create table interests (id integer primary key autoincrement, " +
                "tag text, " +
                "bool integer);");

        ContentValues values = new ContentValues();

        Resources res = context.getResources();

        XmlResourceParser _xml = res.getXml(R.xml.place_records);
        try
        {
            int eventType = _xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if ((eventType == XmlPullParser.START_TAG) && (_xml.getName().equals("record")))
                {
                    String name = _xml.getAttributeValue(0);
                    String description = _xml.getAttributeValue(1);
                    float latitude = Float.parseFloat(_xml.getAttributeValue(2));
                    float longitude = Float.parseFloat(_xml.getAttributeValue(3));
                    String tags = _xml.getAttributeValue(4);
                    String imgSrc = _xml.getAttributeValue(5);

                    values.put("name", name);
                    values.put("description", description);
                    values.put("latitude", latitude);
                    values.put("longitude", longitude);
                    values.put("tags", tags);
                    values.put("imgSrc", imgSrc);
                    db.insert("places", null, values);
                }
                eventType = _xml.next();
            }
        } catch (XmlPullParserException e)
        {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e)
        {
            Log.e(TAG, e.getMessage(), e);
        } finally
        {
            _xml.close();
        }

        values = new ContentValues();
        _xml = res.getXml(R.xml.interests_records);
        try
        {
            int eventType = _xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if ((eventType == XmlPullParser.START_TAG) && (_xml.getName().equals("record")))
                {
                    String tag = _xml.getAttributeValue(0);
                    int bool = Integer.parseInt(_xml.getAttributeValue(1));

                    values.put("tag", tag);
                    values.put("bool", bool);

                    db.insert("interests", null, values);
                }
                eventType = _xml.next();
            }
        } catch (XmlPullParserException e)
        {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e)
        {
            Log.e(TAG, e.getMessage(), e);
        } finally
        {
            _xml.close();
        }
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {


    }
}
