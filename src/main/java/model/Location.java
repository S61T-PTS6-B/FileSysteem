/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

/**
 *
 * @author Max
 */
public class Location implements Serializable, Comparable<Location> {

	private double longitude;
	private double latitude;
	private String road;
	private Date date;
	private String city;
        private String license;

	public Location() {
	}
	
	

	public Location(double latitude, double longitude) {
		this.date = new Date();
		this.longitude = longitude;
		this.latitude = latitude;
	}

        public Location(double latitude, double longitude, Date date) {
            this.date = date;
            this.longitude = longitude;
            this.latitude = latitude;
        }

    public Location(String license, double latitude, double longitude, long date) {
        this.date = new Date(date);
            this.longitude = longitude;
            this.latitude = latitude;
            this.license = license;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRoad() {
		return road;
	}

	public void setRoad(String road) {
		this.road = road;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "(" + this.latitude + ", " + this.longitude + ") on road: " + this.road;
	}

	@Override
	public int compareTo(Location o) {
		return getDate().compareTo(o.getDate());
	}
}
