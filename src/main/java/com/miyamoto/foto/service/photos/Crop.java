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
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class Crop {

    public enum Label {    
    	SQUARE 			("Square", 			75),
    	LARGE_SQUARE 	("Large Square", 	150),
    	THUMBNAIL 		("Thumbnail", 		100),
    	SMALL 			("Small", 			240),
    	SMALL_320 		("Small 320", 		320),
    	MEDIUM 			("Medium", 			500),
    	MEDIUM_640 		("Medium 640", 		640),
    	MEDIUM_800 		("Medium 800", 		800),
    	LARGE 			("Large", 			1024),
    	ORIGINAL 		("Original", 		2400);

		private final String labelName;
    	private final int fixedDimension;
    	
    	public int getFixedDimension() { return fixedDimension;}
    	public String getLabel() { return labelName;}
    	
    	Label(String label, int fixedDimension) {
        	this.labelName = label;
        	this.fixedDimension = fixedDimension;
    	}
    }

	private Label cropLabel;
	private String cropSrc, pixelWidth, pixelHeight;
	private double aspectRatio;
				   
	public Crop(Label label, double aspectRatio, String src, String width, String height) {
		this.cropLabel = label;
		this.aspectRatio = aspectRatio;
		this.cropSrc = src;
		this.pixelWidth = width;
		this.pixelHeight = height;
	}

	public String getCropSrc() {
		return this.cropSrc;
	}
	
	public String getCropLabel() {
		return this.cropLabel.getLabel();
	}
	
	public String getWidth() {
		return pixelWidth;
	}
	
	public String getHeight() {
		return pixelHeight;
	}
}

