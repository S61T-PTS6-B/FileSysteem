/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculations;

import model.Location;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Max
 */
public class TrafficJamSeriesOfLocationsOnRoad {

    private String road;
    private List<Location> locations;

    public String getRoad() {
        return road;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public TrafficJamSeriesOfLocationsOnRoad(String road) {
        this.road = road;
        this.locations = new ArrayList<>();
    }
}
