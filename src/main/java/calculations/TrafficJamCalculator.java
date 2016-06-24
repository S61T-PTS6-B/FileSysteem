/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import model.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Max
 */
@Stateless
public class TrafficJamCalculator {

    private static List<String> trafficJams;
    
    public TrafficJamCalculator() {
        this.trafficJams = new ArrayList<>();
    }

    public static String checkForTrafficJam(List<Location> locations) throws ParseException {

        //Stap 2: Naam van de locatie opvragen en in het locatie object zetten
        List<Location> helplist = new ArrayList<>();

        for (Location loc : locations) {
            double latitude = loc.getLatitude();
            double longitude = loc.getLongitude();
            Client client = ClientBuilder.newClient();
            WebTarget myResource = client.target("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=AIzaSyCWNHGkdYtjHewMhV4f6JoA3BPdv7IGhDY");
            String response = myResource.request(MediaType.APPLICATION_JSON).get(String.class);

            JSONParser parser = new JSONParser();
            JSONObject jsonobj = (JSONObject) parser.parse(response);
            JSONArray resultslist = (JSONArray) jsonobj.get("results");
            JSONObject result = (JSONObject) resultslist.get(0);
            String resultroad = (String) result.get("formatted_address");
            resultroad = resultroad.substring(0, resultroad.indexOf(","));

            if (resultroad.lastIndexOf(" ") != -1 && resultroad.substring(resultroad.lastIndexOf(" "), resultroad.length()).matches(".*\\d+.*")) {
                resultroad = resultroad.substring(0, resultroad.lastIndexOf(" "));
            }

            Location helpLoc = new Location(loc.getLicense(), loc.getLatitude(), loc.getLongitude(), loc.getDate().getTime());
            helpLoc.setRoad(resultroad);

            try {
                result = (JSONObject) resultslist.get(resultslist.size() - 3);
                String resultcity = (String) result.get("formatted_address");
                resultcity = resultcity.substring(0, resultcity.indexOf(","));
                helpLoc.setCity(resultcity);
            } catch (Exception e) {

            }
            helplist.add(helpLoc);
        }

        locations = helplist;

        Location testloc1 = new Location("auto1", 0.1, 0.1, new Date().getTime());
        testloc1.setRoad("road1");
        Location testloc2 = new Location("auto2", 0.1, 0.1, new Date().getTime());
        testloc2.setRoad("road1");
        Location testloc3 = new Location("auto3", 0.1, 0.1, new Date().getTime());
        testloc3.setRoad("road2");
        Location testloc4 = new Location("auto4", 0.1, 0.1, new Date().getTime());
        testloc4.setRoad("road3");
        Location testloc5 = new Location("auto5", 0.1, 0.1, new Date().getTime());
        testloc5.setRoad("road14");
        Location testloc6 = new Location("auto6", 0.1, 0.1, new Date().getTime());
        testloc6.setRoad("road1");
        Location testloc7 = new Location("auto7", 0.1, 0.1, new Date().getTime());
        testloc7.setRoad("road1");

        locations.add(testloc1);
        locations.add(testloc2);
        locations.add(testloc3);
        locations.add(testloc4);
        locations.add(testloc5);
        locations.add(testloc6);
        locations.add(testloc7);

        //Stap 4: Kijken welke locaties op een snelweg liggen
        // - Lijst van snelweg namen hebben
        // - Als de naam van de locatie een snelweg is, maak een lijst aan van die snelweg
        //      en gooi die locatie erin. verwijder de locatie uit de originele lijst
        List<TrafficJamSeriesOfLocationsOnRoad> seriesOfLocationsOnRoad = new ArrayList<>();

        for (Location loc : locations) {
            
            if (seriesOfLocationsOnRoad.isEmpty()) {
                TrafficJamSeriesOfLocationsOnRoad newSerie = new TrafficJamSeriesOfLocationsOnRoad(loc.getRoad());
                newSerie.getLocations().add(loc);
                seriesOfLocationsOnRoad.add(newSerie);
            }
            else {
                boolean existsInList = false;
                for (TrafficJamSeriesOfLocationsOnRoad tjs : seriesOfLocationsOnRoad) {
                    if (tjs.getRoad().equals(loc.getRoad())) {
                        existsInList = true;
                        tjs.getLocations().add(loc);
                    }
                }
                if (!existsInList) {
                    TrafficJamSeriesOfLocationsOnRoad newSerie = new TrafficJamSeriesOfLocationsOnRoad(loc.getRoad());
                    newSerie.getLocations().add(loc);
                    seriesOfLocationsOnRoad.add(newSerie);
                }
            }
        }
        
        List<String> activeTrafficJams = new ArrayList<>();

        String fileString = "<br /><br />Files: <br /><br />";

        for (TrafficJamSeriesOfLocationsOnRoad tjs : seriesOfLocationsOnRoad) {
            if (tjs.getLocations().size() > 1) {
                fileString += tjs.getLocations().size() + " cars are driving on road " + tjs.getRoad() + ".<br />";
                activeTrafficJams.add(tjs.getRoad());
                if (!trafficJams.contains(tjs.getRoad())) {
                    System.out.println("Traffic jam STARTED on road: " + tjs.getRoad());
                    
                    Client client = ClientBuilder.newClient();
                    client
                            .target("http://145.93.80.153:8080/RekeningrijdersApplicatie/rest/trafficjam/Add/" + tjs.getRoad().replace(" ", "+"))
                            .request()
                            .get();
                }
            }
        }
        
        for (String tjRoad : trafficJams) {
            if (!activeTrafficJams.contains(tjRoad)) {
                System.out.println("Traffic jam ENDED on road: " + tjRoad);
                Client client = ClientBuilder.newClient();
                    client
                            .target("http://145.93.80.153:8080/RekeningrijdersApplicatie/rest/trafficjam/Add/" + tjRoad.replace(" ", "+"))
                            .request()
                            .get();
            }
        }
        
        trafficJams = activeTrafficJams;
        

        System.out.println("CALCULATIONS: " + fileString);

        return fileString;

    }
}
