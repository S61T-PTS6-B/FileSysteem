/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import calculations.TrafficJamCalculator;
import java.util.ArrayList;
import java.util.List;
import model.Location;
import org.json.simple.parser.ParseException;

/**
 *
 * @author casva
 */
public class FileSysteemConnect {
    
    private static FileSysteemConnect instance = null;
    private static List<Location> locations;
    private static String trafficJams;
    private static boolean busy;

    public static FileSysteemConnect getInstance(){
        if(instance == null) {
           instance = new FileSysteemConnect();
           locations = new ArrayList<>();
           trafficJams = "";
           busy = false;
        }
        return instance;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public String getTrafficJams() {
        return trafficJams;
    }
    
    public void AddCarLocation(Location car) throws Exception{
        while (busy == true) {
            
        }
        if (busy == false) {
            busy = true;            
        }
        else {
            throw new Exception("Multithreading error! Report to Max Breuer");
        }
        Location remove = null;
        for (Location loc : locations) {
            if (loc.getLicense().equals(car.getLicense())) 
            {
                remove = loc;
                break;
            }
        }
        if (remove != null) {
            locations.remove(remove);
            remove = null;
        }
        
        locations.add(car);
        busy = false;
    }

    void calculateTrafficJams() throws ParseException, Exception {
        while (busy == true) {
            
        }
        if (busy == false) {
            busy = true;
        }
        else {
            throw new Exception("Multithreading error! Report to Max Breuer");
        }
        List<Location> copyList = locations;
        busy = false;
        trafficJams = TrafficJamCalculator.checkForTrafficJam(copyList);
    }
}
