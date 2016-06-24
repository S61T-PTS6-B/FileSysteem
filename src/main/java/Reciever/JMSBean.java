package Reciever;


import Controller.FileSysteemConnect;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import model.Location;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Max
 */
@MessageDriven(
   name = "JMSBean",
   activationConfig = {
      @ActivationConfigProperty( propertyName = "destinationType", 
                                 propertyValue = "javax.jms.Queue"),
      @ActivationConfigProperty( propertyName = "destination", 
                                 propertyValue ="FileSysteem")
   }
)

public class JMSBean implements MessageListener {
 
   @Resource
   private MessageDrivenContext mdctx;  
 
   public JMSBean(){        
   }
 
   @Override
   public void onMessage(Message message) {
       try {
            System.out.println("message received");
            FileSysteemConnect connect = FileSysteemConnect.getInstance();
            String content = message.getStringProperty("content");
            JSONObject object = new JSONObject(content);
            long date = object.getLong("date");
            double longtitude = object.getDouble("long");
            double lat = object.getDouble("lat");
            String license = object.getString("licenseplate");
            Location car = new Location(license,lat,longtitude,date);
            connect.AddCarLocation(car);

        } catch (JMSException | JSONException ex) {
            Logger.getLogger(JMSBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) { 
           Logger.getLogger(JMSBean.class.getName()).log(Level.SEVERE, null, ex);
       } 
   }
}