package com.miyamoto.foto.service;

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
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.miyamoto.foto.service.OAuth;
import com.miyamoto.foto.service.OAuth.User;
import com.miyamoto.foto.service.OAuth.Permission;

import com.miyamoto.foto.service.photos.Crop;
import com.miyamoto.foto.service.photos.Photo;
import com.miyamoto.foto.service.photos.PhotoSet;

public class Integration {
    
    private ImageMeta imageMeta;
    private OAuth authorized;
    private User user;

	//RestfulWrapper
	//Flickr
    public Integration() {
    	this.authorized = new OAuth("72157647029609305-838676ff0f16554f","8c281edcae082331","eccbfec6885f6adefb2fd063dce81a20","b114d59e550cf4e9", Permission.DELETE);
    	this.user = authorized.new User("126576272@N06","miyamoto.foto","Lindsay Papp"); 
    }
    
    public Integration(ImageMeta imageMeta) {	
    	this.authorized = new OAuth("72157647029609305-838676ff0f16554f","8c281edcae082331","eccbfec6885f6adefb2fd063dce81a20","b114d59e550cf4e9", Permission.DELETE);
    	this.user = authorized.new User("126576272@N06","miyamoto.foto","Lindsay Papp"); 
    	this.imageMeta = imageMeta;
    }
    
    public void setImageMeta(ImageMeta im) {
    	imageMeta = im;
    }
    
    public Map<String, Object> getParameters() {
    	return imageMeta.getMeta();
    }
    
    public OAuth getAuthorize() {
    	return this.authorized;
    }
    
    public User getUser() {
    	return this.user;
    }
    
    public String postToFlikr() {  	
    	OAuthRequest request = new OAuthRequest(Verb.POST, "https://up.flickr.com/services/upload/");
            	
        Map<String, Object> metaParams = getParameters();
        request.addHeader("Content-Type", "multipart/form-data; boundary=" + getMultipartBoundary());
        for (Map.Entry<String, Object> entry : metaParams.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("photo") && !key.equals("filename") &&  !key.equals("filemimetype")) {
                request.addQuerystringParameter(key, String.valueOf(entry.getValue()));
            }
        }
        
        Token requestToken = new Token(getAuthorize().getToken(), getAuthorize().getTokenSecret());
        ServiceBuilder serviceBuilder = new ServiceBuilder().provider(FlickrApi.class).apiKey(getAuthorize().getApiKey()).apiSecret(getAuthorize().getSharedSecret());
        OAuthService service = serviceBuilder.build();
        
        service.signRequest(requestToken, request);
        metaParams.putAll(request.getOauthParameters());
        request.addPayload(buildMultipartBody(metaParams, getMultipartBoundary()));
                
        Response scribeResponse = request.send();
        String strXml = scribeResponse.getBody();
        
       	return strXml.replaceAll("\\<([^<>]+)\\>","").replaceAll("\\%0A","");
        

    }	
    
    public String moveToAlbum(String photosetId, String photoId) {
    	
    	Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put("photoset_id", photosetId);
        parameters.put("photo_id", photoId);
        
    	OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.flickr.com/services/rest/");
            	  
        request.addHeader("Content-Type", "multipart/form-data; boundary=" + getMultipartBoundary());		
		request.addQuerystringParameter("method", "flickr.photosets.addPhoto");
		request.addQuerystringParameter("photoset_id", photosetId);
		request.addQuerystringParameter("photo_id", photoId);
               
        Token requestToken = new Token(getAuthorize().getToken(), getAuthorize().getTokenSecret());
        ServiceBuilder serviceBuilder = new ServiceBuilder().provider(FlickrApi.class).apiKey(getAuthorize().getApiKey()).apiSecret(getAuthorize().getSharedSecret());
        OAuthService service = serviceBuilder.build();
        
        service.signRequest(requestToken, request);
        
        parameters.putAll(request.getOauthParameters());
        request.addPayload(buildMultipartBody(parameters, getMultipartBoundary()));
        
        Response scribeResponse = request.send();
        String strXml = scribeResponse.getBody();
        
        return strXml;
    
    }
    
    // Retrieves the list of albums
    public String getPhotoSetList() {
    	
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("user_id", getUser().getUserId());
	
    	OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.flickr.com/services/rest/");
            	       
        request.addHeader("Content-Type", "multipart/form-data; boundary=" + getMultipartBoundary());		
		request.addQuerystringParameter("method", "flickr.photosets.getList");
		request.addQuerystringParameter("user_id", getUser().getUserId());
		request.addQuerystringParameter("format", "json");
                
        Token requestToken = new Token(getAuthorize().getToken(), getAuthorize().getTokenSecret());
        ServiceBuilder serviceBuilder = new ServiceBuilder().provider(FlickrApi.class).apiKey(getAuthorize().getApiKey()).apiSecret(getAuthorize().getSharedSecret());
        OAuthService service = serviceBuilder.build();
        
        service.signRequest(requestToken, request);
        
        parameters.putAll(request.getOauthParameters());
        request.addPayload(buildMultipartBody(parameters, getMultipartBoundary()));
              
        Response scribeResponse = request.send();
        String strXml = scribeResponse.getBody();
        return strXml;          
    }
    
    
    
    
	public String getPhotosOfSet(String setId) {
    	
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("photoset_id", setId);
        
        
    	OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.flickr.com/services/rest/");     
        request.addHeader("Content-Type", "multipart/form-data; boundary=" + getMultipartBoundary());
		
		
		request.addQuerystringParameter("method", "flickr.photosets.getPhotos");
		request.addQuerystringParameter("photoset_id", setId);
		request.addQuerystringParameter("extras", "url_sq, url_m, url_o, date_upload");
		request.addQuerystringParameter("format", "json");
              
        Token requestToken = new Token(getAuthorize().getToken(), getAuthorize().getTokenSecret());
        ServiceBuilder serviceBuilder = new ServiceBuilder().provider(FlickrApi.class).apiKey(getAuthorize().getApiKey()).apiSecret(getAuthorize().getSharedSecret());
        OAuthService service = serviceBuilder.build();
        
        service.signRequest(requestToken, request);
        
        parameters.putAll(request.getOauthParameters());
        request.addPayload(buildMultipartBody(parameters, getMultipartBoundary()));
        
        Response scribeResponse = request.send();
        String strXml = scribeResponse.getBody();

        return strXml;    
    }
    
    
    public String cleanJsonResponse(String responseString) {
    	return responseString.substring(responseString.indexOf("{"));
    }
    
    public String compileJsonResponse() {
		JSONObject jsonObject = new JSONObject();
		TreeMap<String,PhotoSet> completeStuff = retrieveFlickrPhotoSets();
		
		JSONArray collArray = new JSONArray();
		
		for (String album : completeStuff.descendingKeySet()) {
			JSONObject setJson = new JSONObject();
			PhotoSet ps = completeStuff.get(album);
			setJson.put("setName", ps.getPhotoSetName());
			setJson.put("setId", ps.getPhotoSetId());
			setJson.put("setTitle", ps.getPhotoSetTitle());
			
			// create descending iterator
     		Iterator<Photo> photosDescending = ps.getPhotoSet().descendingIterator();
			
			JSONObject photoJson = new JSONObject();
			while (photosDescending.hasNext()){
				Photo p = photosDescending.next();
				JSONObject cropJson = new JSONObject();
				for (Crop crop : p.getCropList()) {
					JSONObject cropInfo = new JSONObject();
					cropInfo.put("source",crop.getCropSrc());
					cropInfo.put("width", crop.getWidth());
					cropInfo.put("height", crop.getHeight());
					cropJson.put(crop.getCropLabel(), cropInfo);
				}
				photoJson.put(p.getPhotoId(), cropJson);
			}
			setJson.put("photos", photoJson);
			collArray.put(setJson);
		}
		
		jsonObject.put("result", collArray);		
		return jsonObject.toString();           
    }
    
    public TreeMap<String,PhotoSet> retrieveFlickrPhotoSets() {
    		
    	TreeMap<String,PhotoSet> entireCollection = new TreeMap<String,PhotoSet>();
		
    	String photoSetListString = cleanJsonResponse(getPhotoSetList());
        JSONObject setListJson = new JSONObject(photoSetListString);
        JSONObject setListObject = setListJson.getJSONObject("photosets");
        
        int setCount = Integer.parseInt(setListObject.get("total").toString());        
        JSONArray setArr = setListObject.getJSONArray("photoset");
        
        
        for (int i = 0; i < setCount; i++) {			
			JSONObject photoSetOne = (JSONObject) setArr.get(i);
			String setId = photoSetOne.get("id").toString();
			String dateUpdate =  photoSetOne.get("date_update").toString();                    // <--- order! long date val
			String setTitle = photoSetOne.getJSONObject("title").get("_content").toString();
			String setDesc = photoSetOne.getJSONObject("description").get("_content").toString();
			PhotoSet aPhotoSet = new PhotoSet(setId, setTitle, setDesc);

			String photoJsonString = cleanJsonResponse(getPhotosOfSet(setId));
			JSONObject photosJson = new JSONObject(photoJsonString);
		
			JSONObject photosObject = photosJson.getJSONObject("photoset");
			int photoCount = Integer.parseInt(photosObject.get("total").toString());
						
			JSONArray photosArr = photosObject.getJSONArray("photo");
			
			// TODO: Pull the date in extras, check against the date and create a chron list
			// "date_upload" is the key
			for (int k = 0; k < photoCount; k++) {
				JSONObject photoOne = (JSONObject) photosArr.get(k);
				String photoId = photoOne.get("id").toString();
				String photoSecret = photoOne.get("secret").toString();
				String photoTitle = photoOne.get("title").toString();
				String photoUpdated = photoOne.get("dateupload").toString(); 
		
				double w = Double.parseDouble(photoOne.get("width_m").toString());
				double h = Double.parseDouble(photoOne.get("height_m").toString());
				double photoAspect = w/h;
				
				Photo aPhoto = new Photo(photoId, photoSecret, photoTitle, photoAspect, Integer.parseInt(photoUpdated));
				aPhoto.addSquareCrop(photoOne.get("url_sq").toString(), photoOne.get("width_sq").toString(), photoOne.get("height_sq").toString());  
				aPhoto.addMediumCrop(photoOne.get("url_m").toString(), photoOne.get("width_m").toString(), photoOne.get("height_m").toString());  
				aPhoto.addOriginal(photoOne.get("url_o").toString(), photoOne.get("width_o").toString(), photoOne.get("height_o").toString());
				aPhotoSet.addPhotoToSet(aPhoto);  
			}      
			//entireCollection.put(setTitle, aPhotoSet);
			entireCollection.put(dateUpdate, aPhotoSet);   
        }
    
    	return entireCollection;
    }
      
    
    private String getMultipartBoundary() {
        return "---------------------------7d273f7a0d3";
    }
    
    private byte[] buildMultipartBody(Map<String, Object> parameters, String boundary) {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
        	String filename = (String) parameters.get("filename");

        	String fileMimeType = "image/jpeg";
        	
            buffer.write(("--" + boundary + "\r\n").getBytes("UTF-8"));
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                String key = entry.getKey();
                if(!key.equals("filename") && !key.equals("filemimetype"))
                	writeParam(key, entry.getValue(), buffer, boundary, filename, fileMimeType);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return buffer.toByteArray();
    }
    
    private void writeParam(String name, Object value, ByteArrayOutputStream buffer, String boundary, String filename, String fileMimeType) throws IOException {
        if (value instanceof InputStream) {
            buffer.write(("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\";\r\n").getBytes("UTF-8"));
            buffer.write(("Content-Type: " + fileMimeType + "\r\n\r\n").getBytes("UTF-8"));
            InputStream in = (InputStream) value;
            byte[] buf = new byte[512];

            int res = -1;
            while ((res = in.read(buf)) != -1) {
                buffer.write(buf, 0, res);
            }
            buffer.write(("\r\n" + "--" + boundary + "\r\n").getBytes("UTF-8"));
        } else if (value instanceof byte[]) {
            buffer.write(("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\";\r\n").getBytes("UTF-8"));
            buffer.write(("Content-Type: " + fileMimeType + "\r\n\r\n").getBytes("UTF-8"));
            buffer.write((byte[]) value);
            buffer.write(("\r\n" + "--" + boundary + "\r\n").getBytes("UTF-8"));
        } else {
            buffer.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes("UTF-8"));
            buffer.write(((String) value).getBytes("UTF-8"));
            buffer.write(("\r\n" + "--" + boundary + "\r\n").getBytes("UTF-8"));
        }
    }
}

//[^(\W{ticketid})].*?(?=</ticketid>)
//[^(\W{photoid})].*?(?=</photoid>)