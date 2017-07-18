package ggn.com.locationtracker.models;

import io.realm.RealmObject;

/**
 * Created by GagaN "G-Expo" on 05-09-2016.
 */
public class LocationDetails extends RealmObject
{
    private double lattitude,longitude;
    private long time_ms;
    private int routeid;

    public double getLattitude()
    {
        return lattitude;
    }

    public void setLattitude(double lattitude)
    {
        this.lattitude = lattitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public long getTime_ms()
    {
        return time_ms;
    }

    public void setTime_ms(long time_ms)
    {
        this.time_ms = time_ms;
    }

    public int getRouteid()
    {
        return routeid;
    }

    public void setRouteid(int routeid)
    {
        this.routeid = routeid;
    }
}
