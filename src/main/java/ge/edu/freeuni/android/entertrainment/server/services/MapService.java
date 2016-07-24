package ge.edu.freeuni.android.entertrainment.server.services;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.maxmind.geoip.Location;
import ge.edu.freeuni.android.entertrainment.server.model.StationInfo;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/map")
@Produces({MediaType.APPLICATION_JSON})
public class MapService {

    @GET
    @Path("location")
    public String getLocation() {
        JSONObject jsonObject = new JSONObject();
        try {
            Location curr = getCurrentLocation();
            jsonObject.put("latitude", curr.latitude);
            jsonObject.put("longitude", curr.longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @GET
    @Path("destReached/{destination}")
    public String destinationReached(@PathParam("destination") String destination) {
        JSONObject jsonObject = new JSONObject();
        if (destination == "Tbilisi")
            jsonObject.put("destination reached", "true");
        else jsonObject.put("destination reached", "false");
        return jsonObject.toString();
    }

    @GET
    @Path("station/{station}")
    public StationInfo getStationInfo(@PathParam("station") String station){
        StationInfo stationInfo = new StationInfo(station, "01:28", 0);

        estimateTimeAndDistLeft(station, stationInfo);
        return stationInfo;
    }

    private void estimateTimeAndDistLeft(String station, StationInfo stationInfo) {
        try {
            Location curr = getCurrentLocation();
            Location next = getStaionLocation(station);
            double distance = calculateDistance(curr.latitude, next.latitude, curr.longitude, next.longitude);
            stationInfo.setDistance(round(distance, 2));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private Location getStaionLocation(String station) throws IOException {
        Geocoder geocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(station + ", Georgia")
                .getGeocoderRequest();
        GeocodeResponse geocoderResponse;

        geocoderResponse = geocoder.geocode(geocoderRequest);
        List<GeocoderResult> results = geocoderResponse.getResults();
        float latitude = results.get(0).getGeometry().getLocation().getLat().floatValue();
        float longitude = results.get(0).getGeometry().getLocation().getLng().floatValue();

        Location stationLoc = new Location();
        stationLoc.latitude = latitude;
        stationLoc.longitude = longitude;
        return stationLoc;
    }

    private Location getCurrentLocation() throws IOException {
        Location loc = new Location();
        loc.latitude = 41.7f;
        loc.longitude = 44.8f;
        return loc;
    }

    /*
     * Calculate distance between two points in latitude and longitude
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * @returns Distance in Meters
     */
    private double calculateDistance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

}
