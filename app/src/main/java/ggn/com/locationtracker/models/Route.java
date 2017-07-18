package ggn.com.locationtracker.models;

import android.graphics.Color;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by ivan on 3/15/17.
 */

public class Route {

    public Object tag = null;

    private String routeTag = "";
    private String title = "";
    private int color = Color.parseColor("#00ff00");
    private List<BusStop> busstops = new ArrayList<>();
    private List<RouteDirection> busDirections = new ArrayList<>();
    private List<Path> paths = new ArrayList<>();
    private String tmpTimeStrng = "";
    private List<LatLng> fullPath = new ArrayList<>();


    public static List<Route> allRoutes = new ArrayList<>();

    public static List<Route> getActiveRoutes(Date date) {
        return allRoutes;
    }


    static Route newRouteFromXMLDoc(Document doc) {

        Route route = new Route();

        doc.getDocumentElement().normalize();

        NodeList rt =  doc.getElementsByTagName("route");
        if (rt.getLength() > 0) {
            Element em = (Element) rt.item(0);
            try {
                route.color = Color.parseColor("#" + em.getAttribute("color"));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            route.routeTag = em.getAttribute("tag");
            route.title = em.getAttribute("title");
        }
        NodeList nodeListStop = doc.getElementsByTagName("stop");
        for (int j = 0; j < nodeListStop.getLength(); j++) {
            BusStop bs = new BusStop();
            Node n = nodeListStop.item(j);
            Element fstElmnt = (Element) n;
            String lat =  fstElmnt.getAttribute("lat");
            String lon = fstElmnt.getAttribute("lon");
            bs.title = fstElmnt.getAttribute("title");
            bs.tag = fstElmnt.getAttribute("tag");
            bs.stopId = fstElmnt.getAttribute("stopId");
            if(!lat.equals("") && !lon.equals("")) {
                bs.setLatlon(new LatLng(Double.parseDouble(lat),Double.parseDouble(lon)));
                route.busstops.add(bs);
            }
        }

        NodeList nodeList = doc.getElementsByTagName("path");
        for (int j = 0; j < nodeList.getLength(); j++) {
            NodeList node = nodeList.item(j).getChildNodes();
            Path path = new Path();
            for(int i = 0; i < node.getLength(); i++) {
                Node n = node.item(i);
                Element fstElmnt = (Element) n;
                String lat =  fstElmnt.getAttribute("lat");
                String lon = fstElmnt.getAttribute("lon");
                if(!lat.equals("") && !lon.equals("")) {
                    path.addPoint(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
                }
            }
            route.paths.add(path);

            sortPaths(route);

            for (BusStop s: route.busstops) {
                double df = 1000000.0;
                int idx = -1;
                for (int i = 0; i < route.fullPath.size(); i++) {
                    LatLng ll = route.fullPath.get(i);
                    double pp = (ll.latitude - s.latlon.latitude) * (ll.latitude - s.latlon.latitude) + (ll.longitude - s.latlon.longitude) * (ll.longitude - s.latlon.longitude);
                    if (pp < df) {
                        df = pp;
                        idx = i;
                    }
                }
                s.indexInPath = idx;
            }

        }

        return route;
    }

    static void sortPaths(Route r) {

        if (r.paths.isEmpty()) return;

        List<Path> newPaths = new ArrayList<>();
        newPaths.add(r.paths.get(0));
        r.paths.remove(0);
        while (!r.paths.isEmpty()) {
            Path k = null;
            double df = 1000000.0;
            for (Path path2:r.paths ) {
                List<LatLng> path1 = newPaths.get(newPaths.size() - 1).getPoints();
                LatLng p1 = path1.get(path1.size() - 1);
                LatLng p2 = path2.getPoints().get(0);
                double pp = (p1.latitude - p2.latitude) * (p1.latitude - p2.latitude) + (p1.longitude - p2.longitude) * (p1.longitude - p2.longitude);
                if (pp <= df) {
                    df = pp;
                    k = path2;
                }
            }
            newPaths.add(k);
            r.paths.remove(k);
        }

        r.paths = newPaths;
        r.fullPath.clear();
        for (int i = 0; i < r.paths.size(); i++) {
            r.fullPath.addAll(r.paths.get(i).getPoints());
            if (i < r.paths.size() - 1)
                r.fullPath.remove(r.fullPath.size() - 1);
        }
    }


    public String getRouteTag() {
        return routeTag;
    }

    public void setRouteTag(String routeTag) {
        this.routeTag = routeTag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<BusStop> getBusstops() {
        return busstops;
    }

    public void setBusstops(List<BusStop> busstops) {
        this.busstops = busstops;
    }

    public List<RouteDirection> getBusDirections() {
        return busDirections;
    }

    public void setBusDirections(List<RouteDirection> busDirections) {
        this.busDirections = busDirections;
    }

    public List<LatLng> getFullPath() {
        return fullPath;
    }


    public String getTmpTimeStrng() {
        return tmpTimeStrng;
    }

    public void setTmpTimeStrng(String tmpTimeStrng) {
        this.tmpTimeStrng = tmpTimeStrng;
    }

    static public class BusStop {
        private String tag = "";
        private String title = "";
        private String stopId = "";
        private LatLng latlon = new LatLng(0, 0);

        private int indexInPath = 0;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getStopId() {
            return stopId;
        }

        public void setStopId(String stopId) {
            this.stopId = stopId;
        }

        public LatLng getLatlon() {
            return latlon;
        }

        public void setLatlon(LatLng latlon) {
            this.latlon = latlon;
        }
    }

    static public class RouteDirection {
        private String tag = "";
        private String title = "";
        private String name = "";
        private boolean useForUI = true;
        private List<String> stopTags = new ArrayList<>();

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isUseForUI() {
            return useForUI;
        }

        public void setUseForUI(boolean useForUI) {
            this.useForUI = useForUI;
        }

        public List<String> getStopTags() {
            return stopTags;
        }

        public void setStopTags(List<String> stopTags) {
            this.stopTags = stopTags;
        }
    }

    static class Path {
        private List<LatLng> points = new ArrayList<>();

        public void addPoint(LatLng p) {
            points.add(p);
        }

        public List<LatLng> getPoints() {
            return points;
        }

        public void setPoints(List<LatLng> points) {
            this.points = points;
        }
    }
}
