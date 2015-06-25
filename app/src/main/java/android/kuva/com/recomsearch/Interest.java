package android.kuva.com.recomsearch;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
     * Created by KuVa on 22.05.2015.
     */
    public class Interest
    {

        private static final String TAG ="MyLogs";


        private static ArrayList<Interest> interests;
        private boolean like;
        private String tag;

        private Interest(String tag, boolean like)
        {
            this.tag = tag;
            this.like = like;
        }

        private Interest ()
        {

        }

        public static ArrayList<String> getCategories()
        {
            ArrayList<String> list = new ArrayList<>();

            for (Interest interest : getList())
                list.add(interest.getTag());

            return list;
        }

        public static ArrayList<String> getInterests()
        {
            ArrayList<String> list = new ArrayList<>();

            for (Interest interest : getList())
              if (interest.isLike())
                  list.add(interest.getTag());

            return list;
        }

        public static ArrayList<Interest> getList()
        {
            if (interests == null)
            {
                interests = new ArrayList<>();
                DBHelper dbHelper = DBHelper.getInstance();
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                Cursor c = null;
                c = database.rawQuery("SELECT * FROM interests", null);

                if (c != null)
                {
                    if (c.moveToFirst())
                    {
                        String str;
                        do
                        {
                            Interest interest = new Interest();
                            interest.setTag(c.getString(c.getColumnIndex("tag")));
                            interest.setLike(c.getInt(c.getColumnIndex("bool")) == 1);

                            interests.add(interest);
                        } while (c.moveToNext());
                    } else
                        Log.d(TAG, "Interest Cursor is null");

                    c.close();

                    dbHelper.close();
                }
            }

            return interests;
        }

        public boolean isLike()
        {
            return like;
        }

        public void setLike(boolean like)
        {
            this.like = like;
        }

        public String getTag()
        {
            return tag;
        }

        public void setTag(String tag)
        {
            this.tag = tag;
        }


    }




