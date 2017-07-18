package ggn.com.locationtracker.utills;

import android.content.Context;

import java.util.List;

import ggn.com.locationtracker.models.LocationDetails;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by GagaN "G-Expo" on 05-09-2016.
 */
public class RealmHelperG
{
    private static RealmHelperG ourInstance = new RealmHelperG();

    public static RealmHelperG getInstance(Context context)
    {
        if (realmConfig == null)
        {
            realmConfig = new RealmConfiguration.Builder(context).deleteRealmIfMigrationNeeded().build();
        }

        return ourInstance;
    }

    private RealmHelperG()
    {
    }

    public static RealmConfiguration realmConfig;





    public List<LocationDetails> GET_LOCATIONDETAILS(int routeNumber)
    {
        final Realm                   realm            = Realm.getInstance(realmConfig);
        RealmResults<LocationDetails> infoRealmResults = realm.where(LocationDetails.class).equalTo("routeid",routeNumber).findAll();
        return realm.copyFromRealm(infoRealmResults);
    }


   /* public void DELETE_MEMO(final String phoneNumber)
    {
        final Realm realm = Realm.getInstance(realmConfig);
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realmG)
            {
                (realmG.where(LocationDetails.class).equalTo("Number", phoneNumber).findFirst()).removeFromRealm();
            }
        });
    }*/



    public void SAVE_LOCATION(final LocationDetails locationDetails)
    {
        try
        {
            final Realm realm = Realm.getInstance(realmConfig);

            realm.executeTransaction(new Realm.Transaction()
            {
                @Override
                public void execute(Realm realmG)
                {

                    LocationDetails locationDetails1=realmG.createObject(LocationDetails.class);
                    locationDetails1.setLattitude(locationDetails.getLattitude());
                    locationDetails1.setLongitude(locationDetails.getLongitude());
                    locationDetails1.setTime_ms(locationDetails.getTime_ms());
                    locationDetails1.setRouteid(locationDetails.getRouteid());

                }
            });
        }
        catch (Exception | Error e)
        {
            e.printStackTrace();
        }
    }

}
