package com.miyamoto.foto.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class ImageMeta {
    
    private Map<String, Object> metaParams = new HashMap<String, Object>() { 
    { 
    	put("photo",	 	null);
    	put("filename", 	null);
/*    	put("filemimetype", null);
    	put("title", 		null);
    	put("description", 	null);
    	put("tags", 		null);
*/    	put("hidden", 		"0");
    	put("safety_level", "1");
    	put("content_type", "1");
    	put("is_public", 	"1");
    	put("is_family", 	"1");
    	put("is_friend", 	"1");
    	put("async", 		"0");
   		}
   	};

    private boolean async, isFinal = false;
    private List<String> tags;
    
    public ImageMeta(String filename, byte[] image) {
    	metaParams.put("filename", clean(filename));
    	metaParams.put("photo", image);
    }  
    
    public ImageMeta(String filename, File image) {
    	metaParams.put("filename", clean(filename));
    	metaParams.put("photo", image);
    }
    
    public ImageMeta(String filename, InputStream image) {
    	metaParams.put("filename", clean(filename));
    	metaParams.put("photo", image);
    }
    
    public Map<String, Object> finalizeMeta() {
    	for (String k : metaParams.keySet()) {
    		if (metaParams.get(k) == null) 
    			metaParams.remove(k);
    	}
    	
    	this.isFinal = true;
    	return metaParams;
    }
    
    public String clean(String name) {
    	return name;
    }
    
    public boolean isFinalized() {
    	return isFinal; 
    }
    
    public void synchronize() {
    	metaParams.put("async", "0");
    }
    
    public void asynchronize() {
    	metaParams.put("async", "1");
    }
    
    public void setDesc(String desc) {
    	metaParams.put("description", desc);
    }
    
    public Map<String, Object> getMeta() {
    	return metaParams;
    }
}