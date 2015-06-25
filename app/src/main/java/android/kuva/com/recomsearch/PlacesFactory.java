package android.kuva.com.recomsearch;



import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by KuVa on 20.05.2015.
 */
public class PlacesFactory
{
    private static PlacesFactory placesFactory;
    private ArrayList<Place> places;
    private DBHelper dbHelper;

    private final static String TAG = "MyLogs";

    public static PlacesFactory getInstance()
    {
        if (placesFactory == null)
            placesFactory = new PlacesFactory();

        return placesFactory;
    }

    private PlacesFactory()
    {
        places = new ArrayList<>();

        dbHelper = DBHelper.getInstance();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor c = null;
        c = database.rawQuery("SELECT * FROM places", null);

        if (c != null)
        {
            if (c.moveToFirst())
            {

                do
                {
                    Place place = new Place();
                    place.setId(c.getInt(c.getColumnIndex("id")));
                    place.setName(c.getString(c.getColumnIndex("name")));
                    place.setDescription(c.getString(c.getColumnIndex("description")));
                    place.setRating(c.getFloat(c.getColumnIndex("rating")));
                    place.setTags(c.getString(c.getColumnIndex("tags")));
                    place.setImgSrc(c.getString(c.getColumnIndex("imgSrc")));
                    Location location = new Location("Test");
                    location.setLatitude(c.getFloat(c.getColumnIndex("latitude")));
                    location.setLongitude(c.getFloat(c.getColumnIndex("longitude")));
                    place.setLocation(location);

                    places.add(place);


                } while (c.moveToNext());
            } else
                Log.d(TAG, "Cursor is null");

            c.close();

            dbHelper.close();
        }
    }

    public ArrayList<Place> getPlaces()
    {
        return places;
    }

    public ArrayList<Integer> getIds()
    {
        ArrayList<Integer> ids = new ArrayList<>();

        for (Place place : places)
        {
            ids.add(place.getId());
        }

        return ids;
    }

    public ArrayList<Integer> getPlacesNearMe(float distance)
    {
        ArrayList<Integer> nearMe = new ArrayList<>();
        Locator locator = Locator.getInstance();



        for (Place place : places)
            if ((place.getDistance()/ 1000) < distance)
                nearMe.add(place.getId());

        nearMe = sortByDistance(nearMe);

        return nearMe;
    }

    private ArrayList<Integer> sortByDistance(ArrayList<Integer> list)
    {
        if (Locator.getInstance().getCurrentLocation()!= null)
        {
            Collections.sort(list, new Comparator<Integer>()
            {
                @Override
                public int compare(Integer lhs, Integer rhs)
                {
                    return (int) (getPlace(lhs).getDistance() - getPlace(rhs).getDistance());
                }
            });
        }

        return list;

    }

    public ArrayList<Integer> getPlacesByCategories(String category)
    {
        ArrayList<Integer> list = new ArrayList<>();

        for (Place place : places)
        {
            String[] tags = place.getTags().split(",");
            for (int i = 0; i < tags.length; i++)
                if (tags[i].equals(category))
                {
                    list.add(place.getId());
                    break;
                }


        }

        list = sortByDistance(list);

        return list;

    }

    public Place getPlace(int id)
    {
        for (Place p : places)
        {
            if (p.getId() == id)
                return p;
        }
        return null;
    }

    public float getMean(boolean param)
    {
        int countRatings = 0;
        int sumRatings = 0;

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * from places", null);
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {

                do
                {
                    float rating = cursor.getFloat(cursor.getColumnIndex("rating"));
                    if (rating!=0)
                    {
                        countRatings++;
                        sumRatings+=rating;
                    }

                } while (cursor.moveToNext());
            } else
                Log.d(TAG, "Cursor is null");

            cursor.close();

            dbHelper.close();

        }
        if (param)
            return (float)(sumRatings/countRatings);
        else
            return countRatings;
    }

    public ArrayList<Integer> getInterestPlace()
    {
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<String> interests = Interest.getInterests();


        for (Place place : places)
        {
            String[] tags = place.getTags().split(",");
            for (int i = 0; i < tags.length; i++)
            {
                for (String interest : interests)
                    if (interest.equals(tags[i]))
                    {
                        list.add(place.getId());
                        break ;
                    }

                if (list.contains(place.getId()))
                    break;
            }
        }

        list = sortByDistance(list);

        return list;
    }

    public ArrayList<Integer> getRecommendations()
    {
        ArrayList<Integer> list = new ArrayList<>();

        float mean = getMean(true);
        float chisl;
        float znam;

        ArrayList<Place> raited = new ArrayList<>();

        for (Place place : getPlaces())
          if (place.getRating()!=0)
              raited.add(place);

        ArrayList<Place> unraited = new ArrayList<>();

        for (Place place : getPlaces())
           if (place.getRating()==0)
               unraited.add(place);


        for (Place place : unraited)
        {
            float recomRaiting = mean;
            chisl = 0;
            znam = 0;

            for (Place raitedPlace : raited)
            {
                float weight = place.getWeight(raitedPlace);
                chisl += (raitedPlace.getRating()-mean)*(weight);
                znam += weight;
            }

            recomRaiting += (chisl/znam);

            if (chisl != 0 & znam != 0 & recomRaiting > 3.5f)
            {
                place.setRecomRating(recomRaiting);
                list.add(place.getId());
            }
        }



        list = sortByRecomRaiting(list);

        ArrayList<Integer> list2 = new ArrayList<>();

        for (int i = 0; i < list.size(); i++)
        {
            if (i > 19)
                break;
            list2.add(list.get(i));
        }

        return list2;
    }

    private ArrayList<Integer> sortByRecomRaiting(ArrayList<Integer> list)
    {

            Collections.sort(list, new Comparator<Integer>()
            {
                @Override
                public int compare(Integer lhs, Integer rhs)
                {
                    return  (getPlace(rhs).getRecomRating() > getPlace(lhs).getRecomRating()) ? 1 : -1;
                }
            });


        return list;

    }
}



