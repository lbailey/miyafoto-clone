package com.miyamoto.foto.service.photos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.File;

import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;


import com.miyamoto.foto.service.photos.Crop;
import com.miyamoto.foto.service.photos.Crop.Label;

public class Photo implements Comparable<Photo>{
    
	// square, medium, original
	private List<Crop> cropList;
	
	private double photoAspect;
	private int photoUpdated;
	
	private String photoId,
				   photoSecret,
				   photoTitle;
	
	public Photo(String id, String secret, String title, double aspect, int updated) {
		this.photoId = id;
		this.photoSecret = secret;
		this.photoTitle = title;
		this.photoAspect = aspect;
		this.photoUpdated = updated;
		this.cropList = new LinkedList<Crop>();
	}
	
	public int compareTo(Photo other) {
        return this.photoUpdated - other.getUpdated();
    }
	
	public int getUpdated() {
		return this.photoUpdated;
	}
	
	public void addSquareCrop(String squareSrc, String w, String h) {
		Crop square = new Crop(Label.SQUARE, photoAspect, squareSrc, w, h);
		this.cropList.add(square);
	}
	
	public void addMediumCrop(String medSrc, String w, String h) {
		double dWidth = 300 * photoAspect;				// force height to always 300px
		w = String.format("%d", (long)dWidth);
		Crop med = new Crop(Label.MEDIUM, photoAspect, medSrc, w, "300");
		this.cropList.add(med);
	}
	
	public void addOriginal(String oSrc, String w, String h) {
		Crop original = new Crop(Label.ORIGINAL, photoAspect, oSrc, w, h);
		this.cropList.add(original);
	}
	
	//TODO: dynamically decipher with aspectRatio and 600x800 max sizes
	public void addLargeCrop(String lgSrc, String w, String h) {       	
		Crop large = new Crop(Label.LARGE, photoAspect, lgSrc, w, h);
		this.cropList.add(large);
	}
	
	public double aspectRatio(double width, double height) {
		return width/height;
	}
	
	public String getPhotoId() {
		return photoId;
	}
	
	public double getPhotoAspect() {
		return photoAspect;
	}
	
	public String getPhotoTitle() {
		return photoTitle;
	}
	
	public List<Crop> getCropList() {
		return cropList;
	}
	
	/*
	* TODO: Add the other Photo methods: tags, description.
	*/
}
