package pae.com.wa.vanmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Pae on 5/20/15.
 */
public class distan_duration extends HttpGet {
    private double fromLat;
    private double fromLon;
    private double toLat;
    private double toLon;
    private int timeToDestination;
    private int distanceToDestination;
    private  int mint;
    private int sec;
    private int kilo;
    private int mater;
    private int hr;

  public String distan(String lati, String longti){
       String resultall = null;
        String url = getUrl(lati,longti);

        String resultServer = null;

        resultServer = getHttpGet(url);

        try {
            JSONObject rootObj = new JSONObject(resultServer); //rootObj
            JSONArray routes = (JSONArray) rootObj.get("routes");
            if(routes.length()<1)
                resultall="ERROR no route there";
            JSONObject firstRoute = routes.getJSONObject(0);
            JSONArray legs = (JSONArray) firstRoute.get("legs");
            if(legs.length()<1)
                resultall="ERROR no legs there";

            JSONObject firstLeg = legs.getJSONObject(0);
            JSONObject durationObject = (JSONObject) firstLeg.get("duration");
            JSONObject distanceObject = (JSONObject) firstLeg.get("distance");
            // finally we will get the values distance in meters and time in seconds!!
            timeToDestination = (Integer) durationObject.get("value");
            distanceToDestination = (Integer) distanceObject.get("value");
            hr=timeToDestination/3600;
            mint=timeToDestination%3600/60;
            sec=timeToDestination%3600%60;
            kilo=distanceToDestination/1000;
            mater=distanceToDestination%1000;

          resultall="time: "+hr+" hr "+mint+" mint "+sec+" sec , distan: "+ kilo+" kilometers "+mater+" Meters";

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



       return resultall;
    }
    public static String getUrl(String fromAdress, String toAdress)
    {// connect to map web service
        StringBuffer urlString = new StringBuffer();
        urlString.append("http://maps.google.com/maps/api/directions/json?origin=");
        urlString.append(fromAdress.toString());
        urlString.append("&destination=");
        urlString.append(toAdress.toString());
        urlString.append("&sensor=false");
        return urlString.toString();
    }

}
