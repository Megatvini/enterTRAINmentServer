package ge.edu.freeuni.android.entertrainment.server.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationInfo {

    @JsonProperty("station")
    private String station;

    @JsonProperty("time")
    private String estimatedTime;

    @JsonProperty("distance")
    private double distance;

    public StationInfo(String station, String  estimatedTime, double distance) {
        this.station = station;
        this.estimatedTime = estimatedTime;
        this.distance = distance;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}