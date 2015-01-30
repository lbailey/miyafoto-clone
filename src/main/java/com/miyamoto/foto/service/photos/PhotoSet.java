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
import java.util.TreeSet;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import com.miyamoto.foto.service.photos.Photo;
import com.miyamoto.foto.service.photos.Crop;
import com.miyamoto.foto.service.photos.Crop.Label;

public class PhotoSet implements Comparator<Photo>{

	private int photoSetCount;
    private TreeSet<Photo> photoSet;    
    private String photoSetId,
    			   photoSetTitle,
    			   photoSetName,
    			   photoSetDateUpdate;

    public PhotoSet(String id, String title, String name, int count, String date) {
    	this.photoSetId = id;
    	this.photoSetTitle = title;
    	this.photoSetName = name;
    	this.photoSetCount = count;
    	this.photoSetDateUpdate = date;
    	this.photoSet = new TreeSet<Photo>();
    }
    
    @Override
	public int compare(Photo thisPhoto, Photo diffPhoto) {
        return thisPhoto.getUpdated() - diffPhoto.getUpdated();
    }
    
    public void addPhotoToSet(Photo aPhoto) {
    	this.photoSet.add(aPhoto);
    }

	public TreeSet<Photo> getPhotoSet() {
		return photoSet;
	}
	
	public String getPhotoSetId() {
		return photoSetId;
	}	
	
	public String getPhotoSetTitle() {
		return photoSetTitle;
	}
	
	public String getPhotoSetName() {
		return photoSetName;
	}
	
	public String getDateUpdated() {
		return photoSetDateUpdate;
	}
	
	public String getPhotoSetCount() {
		return String.format("%d", photoSetCount);
	}
			
	public int getPhotoCount() {
		return photoSetCount;
	}
}
