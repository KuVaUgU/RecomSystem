package android.kuva.com.recomsearch;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by KuVa on 20.05.2015.
 */
public class Place
{
    private String name;
    private String description;
    private Location location;
    private String imgSrc;
    private float rating;
    private float recomRating;
    private int id;
    private String tags;

    public Place()
    {

    }

    public Place(int id, String name, String description, float latitude, float longitude, String imgSrc)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = new Location("Test");
        this.location.setLatitude(latitude);
        this.location.setLongitude(longitude);
        this.imgSrc = imgSrc;
    }

    public float getDistance ()
    {

       return Locator.getInstance().getCurrentLocation().distanceTo(location);
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Location getLocation()
    {
        return location;
    }

    public String getImgSrc()
    {
        return imgSrc;
    }

    public float getRating()
    {
        return rating;
    }

    public float getRecomRating()
    {
        return recomRating;
    }

    public void setRating(float rating)
    {
        this.rating = rating;
    }

    public void setRecomRating(float recomRating)
    {
        this.recomRating = recomRating;
    }

    public int getId()
    {
        return id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void setImgSrc(String imgSrc)
    {
        this.imgSrc = imgSrc;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTags()
    {
        return tags;
    }

    public void setTags(String tags)
    {
        this.tags = tags;
    }

    public float getWeight(Place place)
    {


        String [] mas1 = this.getTags().split(",");
        String [] mas2 = place.getTags().split(",");

        int count = 0;

        for (int i = 0; i < mas1.length; i++)
            for (int j = 0; j< mas2.length; j++)
            {
                if (mas1[i].equals(mas2[j]))
                {
                    count++;
                    break;
                }
            }

        return (float) (count/mas1.length);
    }
}
