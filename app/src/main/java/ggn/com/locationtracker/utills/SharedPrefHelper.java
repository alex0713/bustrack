package ggn.com.locationtracker.utills;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by GagaN "G-Expo" on 05-09-2016.
 */
public class SharedPrefHelper
{

    private static SharedPreferences sharedPreferences;


    private static SharedPrefHelper ourInstance = new SharedPrefHelper();

    public static SharedPrefHelper getInstance(Context context)
    {
        sharedPreferences = context.getSharedPreferences("TouriTour", Context.MODE_PRIVATE);
        return ourInstance;
    }

    private SharedPrefHelper()
    {
    }


    public void setRouteNumber(int routeNumber)
    {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putInt("route", routeNumber);
        ed.apply();
    }


    public int getrouteNumber()
    {
        return sharedPreferences.getInt("route", 0);
    }

}
