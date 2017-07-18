package ggn.com.locationtracker.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ggn.com.locationtracker.R;

/**
 * Created by ivan on 3/16/17.
 */

public class Agency {

    private String agencyTag = "";
    private String title = "";
    private String regionTitle = "";
    private LatLng latLng;
    private String city;
    private String zip;
    private String county;
    private String state;
    private String country;

    private String temp;

    private Bitmap icon = null;

    static List<Agency> getAgencyListFromJsonString(String jsonString, Context context) {

        List<Agency> agencyList = new ArrayList<>();
        if(jsonString != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonString);
                JSONObject body = jsonObj.getJSONObject("body");
                JSONArray agency = body.getJSONArray("agency");
                for (int i = 0; i < agency.length(); i++) {
                    Agency age = new Agency();
                    JSONObject c = agency.getJSONObject(i);
                    age.agencyTag = c.getString("-tag");
                    age.title = c.getString("-title");
                    age.regionTitle = c.getString("-regionTitle");
                    age.latLng = new LatLng(Double.parseDouble(c.getString("-lat")), Double.parseDouble(c.getString("-lon")));
                    age.city = c.getString("-city");
                    age.zip = c.getString("-zip");
                    age.county = c.getString("-county");
                    age.state = c.getString("-state");
                    age.country = c.getString("-country");

                    age.temp = c.has("-temp") ? c.getString("-temp") : "";

                    switch (age.getAgencyTag()) {
                        case "ecu":
                            age.icon = getIcon(R.drawable.ecu, context);
                            break;
                        case "great":
                            age.icon = getIcon(R.drawable.great, context);
                            break;
                        case "vmc":
                            age.icon = getIcon(R.drawable.vm, context);
                            break;
                        default:
                            age.icon = getIcon(R.drawable.pat, context);
                            break;
                    }

                    agencyList.add(age);
                }
            } catch (Exception e) {
                e.printStackTrace();
                agencyList = null;
            }
        }

        return agencyList;
    }


    public String getAgencyTag() {
        return agencyTag;
    }

    public void setAgencyTag(String agencyTag) {
        this.agencyTag = agencyTag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRegionTitle() {
        return regionTitle;
    }

    public void setRegionTitle(String regionTitle) {
        this.regionTitle = regionTitle;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Bitmap getIcon() {
        return icon;
    }

    static Bitmap getIcon(int iconID, Context context) {
        BitmapDrawable bitmapdraw=(BitmapDrawable)context.getResources().getDrawable(iconID);
        Bitmap b=bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, 50, 50, false);
    }

}
