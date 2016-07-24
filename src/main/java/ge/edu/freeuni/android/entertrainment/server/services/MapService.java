package ge.edu.freeuni.android.entertrainment.server.services;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import ge.edu.freeuni.android.entertrainment.server.model.StationInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Path("/map")
public class MapService {

    @GET
    @Path("station/{station}")
    public StationInfo getStationInfo(@PathParam("station") String station){
        StationInfo stationInfo = new StationInfo(station, "", 0);

        estimateTimeAndDistLeft(station, stationInfo);
        return stationInfo;
    }

    private void estimateTimeAndDistLeft(String station, StationInfo stationInfo) {
        try {
            Location curr = getCurrentLocation();
            Location next = getStaionLocation(station);
            double distance = calculateDistance(curr.latitude, next.latitude, curr.longitude, next.longitude);
            stationInfo.setDistance(distance);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        LookupService cl = new LookupService("/var/geolite/GeoLiteCity.dat",
                LookupService.GEOIP_MEMORY_CACHE | LookupService.GEOIP_CHECK_CACHE);
        InetAddress server_ip = getIp();
        Location location = cl.getLocation(server_ip);
        return location;
    }

    private InetAddress getIp() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Current IP address : " + ip.getHostAddress());
        return ip;
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
