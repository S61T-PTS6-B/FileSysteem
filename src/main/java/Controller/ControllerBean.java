package Controller;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import model.Location;
import org.json.simple.parser.ParseException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Max
 */
@ManagedBean(name = "ControllerBean")
@SessionScoped
public class ControllerBean {

    private FileSysteemConnect fsc;
    
    public List<Location> getLocations() {
        return fsc.getLocations();
    }
    
    public String getTrafficJams() {
        return fsc.getTrafficJams();
    }

    public ControllerBean() {
        fsc = FileSysteemConnect.getInstance();
    }
    
    public void update() {
    }
    
    public void calculateTrafficJam() throws ParseException, Exception {
        fsc.calculateTrafficJams();
    }
}
