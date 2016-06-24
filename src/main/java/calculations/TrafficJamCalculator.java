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

        String fileString = "<br /><br />Traffic jams: <br /><br />";

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
                            .target("http://145.93.80.153:8080/RekeningrijdersApplicatie/rest/trafficjam/Remove/" + tjRoad.replace(" ", "+"))
                            .request()
                            .get();
            }
        }
        
        trafficJams = activeTrafficJams;
        

        System.out.println("CALCULATIONS: " + fileString);

        return fileString;

    }
}
