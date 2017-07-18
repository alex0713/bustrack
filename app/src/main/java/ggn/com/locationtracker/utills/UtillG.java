package ggn.com.locationtracker.utills;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by GagaN "G-Expo" on 06-09-2016.
 */
public class UtillG
{
    static Toast toast;


    public static void showToast(String msg, Context context, boolean center)
    {
        if (toast != null)
        {
            toast.cancel();
        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        if (center)
        {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }

        toast.show();

    }


    public static void transitionToActivity(Activity activity, Class target, View logo, String transitionName)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Intent i = new Intent(activity, target);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                    android.util.Pair.create(logo, transitionName));
            activity.startActivity(i, options.toBundle());

        }
        else
        {
            activity.startActivity(new Intent(activity, target));
        }

    }

    public static String loadStringFromResource(Context context, int fileraw) {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(fileraw);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


}
