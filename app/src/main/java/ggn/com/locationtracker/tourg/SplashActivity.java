package ggn.com.locationtracker.tourg;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ggn.com.locationtracker.R;

public class SplashActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }


    @Override
    protected void onResume()
    {
        startUp();

        super.onResume();
    }

    private void startUp()
    {
        if (ContextCompat.checkSelfPermission(
                SplashActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
                != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    SplashActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ))
            {


                final AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Dating App");
                builder.setMessage("Allow DatingApp to access your location information .");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        ActivityCompat.requestPermissions(
                                SplashActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                11
                        );

                    }
                });
                builder.setNegativeButton("Cancel", null);

                builder.show();


            }
            else
            {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(
                        SplashActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        11
                );

            }

        }
        else
        {
            gotoMainActivity();

        }
    }


    private void gotoMainActivity()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        }, 2000);
    }


}
