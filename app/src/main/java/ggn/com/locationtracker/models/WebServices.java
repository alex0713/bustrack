package ggn.com.locationtracker.models;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ggn.com.locationtracker.R;
import ggn.com.locationtracker.utills.UtillG;

/**
 * Created by ivan on 3/15/17.
 */




public class WebServices {

    public interface APICallback {
        void onResponse(Object result, boolean success, String message); // Params are self-defined and added to suit your needs.
    }


    //TODO - Temporary Codes : Should be replaced with fetching data from REST APIs
    static private int[] TempXmlFiles = {R.raw.xml301, R.raw.xml302, R.raw.xml303, R.raw.xml304, R.raw.xml306, R.raw.xml307, R.raw.xml501, R.raw.xml551};
//    static private int[] TempXmlFiles = {R.raw.xml301, R.raw.xml302, R.raw.xml303, R.raw.xml304, R.raw.xml306, R.raw.xml307,
//            R.raw.xml401, R.raw.xml402,
//            R.raw.xml501, R.raw.xml502, R.raw.xml503, R.raw.xml504, R.raw.xml505, R.raw.xml506, R.raw.xml507, R.raw.xml508,
//            R.raw.xml551, R.raw.xml552,
//            R.raw.xml610,
//            R.raw.xml801, R.raw.xml802, R.raw.xml803, R.raw.xml804, R.raw.xml805, R.raw.xml806,
//            R.raw.xml850};

    static public void fetchAllRoutes(Context context, final APICallback callback) {

        final AsyncTask<Context, Boolean, Boolean> asyncTask = new AsyncTask<Context, Boolean, Boolean>() {

            @Override
            protected Boolean doInBackground(Context... params) {
                Boolean retval = false;
                Context context = params[0];
                List<Route> routes = new ArrayList<>();
                try {
                    for (int frawid:TempXmlFiles) {
                        InputStream is = context.getResources().openRawResource(frawid);
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document doc = db.parse(new InputSource(is));
                        Route route = Route.newRouteFromXMLDoc(doc);
                        routes.add(route);
                    }

                    callback.onResponse(routes, true, "");
                    retval = true;
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    e.printStackTrace();
                    callback.onResponse(routes, false, e.getMessage());
                }

                return retval;
            }
        };

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
        else
            asyncTask.execute(context);

    }

    static public void fetchAllAgency(Context context, final APICallback callback) {

        final AsyncTask<Context, Boolean, Boolean> asyncTask = new AsyncTask<Context, Boolean, Boolean>() {

            @Override
            protected Boolean doInBackground(Context... params) {

                Context context = params[0];
                List<Agency> listAgency = Agency.getAgencyListFromJsonString(UtillG.loadStringFromResource(context, R.raw.agency), context);
                Boolean retval = listAgency != null;
                callback.onResponse(listAgency, retval, "");

                return retval;
            }
        };

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
        else
            asyncTask.execute(context);

    }
}
