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
import java.util.TreeSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONObject;
import org.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheBuilder;

import com.miyamoto.foto.service.OAuth;
import com.miyamoto.foto.service.OAuth.User;
import com.miyamoto.foto.service.OAuth.Permission;

import com.miyamoto.foto.service.photos.Crop;
import com.miyamoto.foto.service.photos.Photo;
import com.miyamoto.foto.service.photos.PhotoSet;

public class Integration {
    
    private static final String EMPTY_PHOTO = "15508109759";
    
    private ImageMeta imageMeta;
    private OAuth authorized;
    private User user;

	//
	//
	// Uses Google Guava local mem cache 
	//
	//
	public enum AlbumSetCache {
		INSTANCE;
		
		private static Cache<String,PhotoSet> setIdCache =
				CacheBuilder.newBuilder()
						.maximumSize(400)
						.build();
						
		private static Cache<String,PhotoSet> titleCache =
				CacheBuilder.newBuilder()
						.maximumSize(50)
						.build();				
		
		public void expireAndAdd(String photoSetId, PhotoSet photoSet) {
			setIdCache.invalidate(photoSetId);
			setIdCache.put(photoSetId, photoSet);
		}

		public boolean hasKey(String photoSetId) {
			return setIdCache.getIfPresent(photoSetId) == null ? false : true;
		}

		public PhotoSet getPhotoSet(String photoSetId) {
			return setIdCache.getIfPresent(photoSetId);
		}
		
		public boolean hasTitles(){
			return titleCache.size() > (long) 0 ? true : false;
		}
		
		public boolean hasTitle(String title){
			return titleCache.getIfPresent(title) == null ? false : true;
		}
		
		public PhotoSet getByTitle(String title){
			return titleCache.getIfPresent(title);
		}
		
		public void addByTitle(String title, PhotoSet photoSet) {
			titleCache.invalidate(title);
			titleCache.put(title, photoSet);
		}
		
		public ConcurrentMap<String,PhotoSet> getTitles() {
			return titleCache.asMap();
		}
	}

	//
	//
	// OAuth call to Flickr, authorize all transactions. To get these vals, use separate script located at ROOT directory
	// TODO: Call these secret vals from server file
	//
	//
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
    
    //
    //
    // Post to Flickr: Proxy uploaded image to Flickr
    //
    //
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
    
    //
    //
    // Create Album/photoSet on Flickr: Tell Flickr to create a new Photo Set/Album with this name
    //
    //
    public String createNewAlbum(String albumName, String albumPrefix) {
    	
    	Map<String, Object> parameters = new HashMap<String, Object>();
		String albumTitle = albumPrefix + "-" + albumName.replaceAll(" ","-").toLowerCase(); //regex on name
        
//DO NOT        //parameters.put("title", albumTitle);
//NEED        //parameters.put("primary_photo_id", EMPTY_PHOTO);
        
        System.out.println("Creating new album with title : "+albumTitle);
        
    	OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.flickr.com/services/rest/");
            	  
        request.addHeader("Content-Type", "multipart/form-data; boundary=" + getMultipartBoundary());		
		request.addQuerystringParameter("method", "flickr.photosets.create");
		request.addQuerystringParameter("title", albumTitle);
		request.addQuerystringParameter("description", WordUtils.capitalize(albumName)); //regex to make capitalized
		request.addQuerystringParameter("primary_photo_id", EMPTY_PHOTO);
               
        Token requestToken = new Token(getAuthorize().getToken(), getAuthorize().getTokenSecret());
        ServiceBuilder serviceBuilder = new ServiceBuilder().provider(FlickrApi.class).apiKey(getAuthorize().getApiKey()).apiSecret(getAuthorize().getSharedSecret());
        OAuthService service = serviceBuilder.build();
        
        service.signRequest(requestToken, request);
        
        parameters.putAll(request.getOauthParameters());
        request.addPayload(buildMultipartBody(parameters, getMultipartBoundary()));
        
        Response scribeResponse = request.send();
        String strXml = scribeResponse.getBody();
        
        
        String setId = strXml.replaceAll("\\n","").replaceAll("(.+id=\"|\".*)","");
        String[] extraAlbumInfo = albumPrefix.split("-");

        // Update title cache with new PhotoSet here. 
        PhotoSet aPhotoSet = new PhotoSet(setId, albumTitle, WordUtils.capitalize(albumName), 1, "");
		aPhotoSet.addSetYear(extraAlbumInfo[0]);
		aPhotoSet.addSetType(extraAlbumInfo[1]);
		AlbumSetCache.INSTANCE.addByTitle(albumTitle, aPhotoSet);
		
        return setId;
    
    }
    
    //
    //
    // Post to Flickr: Tell Flickr to move image with specific ID to specific photo set with ID
    //
    //
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
    
    //
    //
    // JSON: send list of albums by title-PhotoSet to use for side-navigation
	// 
	//    
    public String albumListToJson(String selectedYear) {
		JSONObject jsonObject = new JSONObject();							//invalTitle
		TreeMap<String,PhotoSet> completeList = retrieveFlickrPhotoSets("year-type-title","title");
	
		JSONObject collObj = new JSONObject();
		JSONArray typeArr = new JSONArray();
		String prevType = "", currentType;
		for (String album : completeList.keySet()) {			
			if (album.startsWith(selectedYear)) {
				currentType = completeList.get(album).getSetType();
				prevType = prevType.isEmpty()? currentType : prevType;
				if (!currentType.matches(prevType)) {
				   collObj.put(prevType, typeArr);
				   typeArr = new JSONArray();
				}
				
				JSONObject setJson = new JSONObject();
				PhotoSet ps = completeList.get(album);
				setJson.put("setName", ps.getPhotoSetName());
				setJson.put("setId", ps.getPhotoSetId());
				setJson.put("setTitle", ps.getPhotoSetTitle());
				setJson.put("setCount", ps.getPhotoSetCount());
				typeArr.put(setJson);
				prevType = currentType;
			}
		}
		
		jsonObject.put("result", collObj);		
		return jsonObject.toString();           
    }
    
    
    //
    //
    // Post to Flickr: Query all Photo Sets (this returns basic album list)
    //
    //
    public String getPhotoSetList() {
 		long pslS = System.nanoTime();      	
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
        
		long pslE = System.nanoTime();
		long pslD = (pslE - pslS)/1000000;
		System.out.println("retrieve photo set list : "+pslD);
        return strXml;          
    }
    
    
    //
    //
    // Post to Flickr: Get all photos associated with specific Photo Set Id (specific album)
    // TODO: clean this--it is the most time-consuming method from Flickr.
    //
    //
	public String getPhotosOfSet(String setId) {
		long posS = System.nanoTime();    	
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
        
		long posE = System.nanoTime();
		long posD = (posE - posS)/1000000;
		System.out.println("getPhotosOfSet()->function time: "+posD);

        return strXml;    
    }
    
    //
    //
    // Helper: Cleans JSON response
    //
    //
    public String cleanJsonResponse(String responseString) {
    	return responseString.substring(responseString.indexOf("{"));
    }
    
    //
    //
    // JSON: JSON writer helper for PhotoSet to JSON
    //
    //
    public String psToJson(PhotoSet ps) {
        JSONObject setJson = new JSONObject();		//TODO: fill these values 
    	setJson.put("setTitle", ps.getPhotoSetTitle());
    	setJson.put("setName", ps.getPhotoSetName());
    	setJson.put("setYear", ps.getSetYear());
    	
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
				
		return setJson.toString();  
    }
    
    //
    //
    // JSON: Calls a request to Flickr for a single PhotoSet (album)
    //
    //
    public String specificPhotoSetJson(String setId) {
		String photoJsonString = cleanJsonResponse(getPhotosOfSet(setId));
		JSONObject photosJson = new JSONObject(photoJsonString);
		
		PhotoSet ps = parsePhotoSetResponse(setId, "", "", photoJsonString, ""); // missing pieces filled by cache
		
		return psToJson(ps);
		         
    }
    
    //
    //
    // JSON: Uses cache to return a single/specific PhotoSet (album)
    //
    //
    public String specificCachePhotoSetJson(String setId) {
    	PhotoSet aPhotoSet;
		if (AlbumSetCache.INSTANCE.hasKey(setId)) {
			aPhotoSet = AlbumSetCache.INSTANCE.getPhotoSet(setId);
			return psToJson(aPhotoSet);
		} else {
			return specificPhotoSetJson(setId);
		}
    }
    
    //
    //
    // JSON: Calls the Bulky method to retrieve all Photo Sets with corresponding Photos
    //
    //
    public String allPhotoSets(String invalidateId) {
		JSONObject jsonObject = new JSONObject();
		TreeMap<String,PhotoSet> completeStuff = retrieveFlickrPhotoSets(invalidateId, "id");
	
		JSONArray collArray = new JSONArray();
		
		for (String album : completeStuff.descendingKeySet()) {
			JSONObject setJson = new JSONObject();
			PhotoSet ps = completeStuff.get(album);
			setJson.put("setName", ps.getPhotoSetName());
			setJson.put("setId", ps.getPhotoSetId());
			setJson.put("setTitle", ps.getPhotoSetTitle());
			setJson.put("setCount", ps.getPhotoSetCount());
			setJson.put("setYear", ps.getSetYear());
			
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
    
    //
    //
    // Interpreting: Parses single PhotoSet response from Flickr to PhotoSet object
    //
    //
    public PhotoSet parsePhotoSetResponse(String setId, String setTitle, String setDesc, String photoJsonString, String dateOfSet) {    
		JSONObject photosJson = new JSONObject(photoJsonString);
		JSONObject photosObject = photosJson.getJSONObject("photoset");
		
		int photoCount = Integer.parseInt(photosObject.get("total").toString());
		String photoSetTitle = photosObject.get("title").toString();
		
		PhotoSet aPhotoSet = AlbumSetCache.INSTANCE.hasTitle(photoSetTitle) ? 
							 AlbumSetCache.INSTANCE.getByTitle(photoSetTitle) :
							 new PhotoSet(setId, setTitle, setDesc, photoCount, dateOfSet);
					
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
			
			String url_c = getLargeSrcUrl(photoOne.get("url_m").toString());
			Photo aPhoto = new Photo(photoId, photoSecret, photoTitle, photoAspect, Integer.parseInt(photoUpdated));
			aPhoto.addSquareCrop(photoOne.get("url_sq").toString(), photoOne.get("width_sq").toString(), photoOne.get("height_sq").toString());  
			aPhoto.addMediumCrop(photoOne.get("url_m").toString(), photoOne.get("width_m").toString(), photoOne.get("height_m").toString());    
			aPhoto.addOriginal(photoOne.get("url_o").toString(), photoOne.get("width_o").toString(), photoOne.get("height_o").toString());
			aPhoto.addLargeCrop(url_c, "800", "600");
			aPhotoSet.addPhotoToSet(aPhoto);  
		}
		
    	return aPhotoSet;
    }
    
    
    //
    //
    // Muscle: This references multiple Flickr queries and compiles a map response
    // Uses Google cache to speed up PhotoSet query responses
    //
    //
    public TreeMap<String,PhotoSet> retrieveFlickrPhotoSets(String invalidate, String cacheType) {
    	
    	TreeMap<String,PhotoSet> entireCollection = new TreeMap<String,PhotoSet>();
    	
    	//speed up if keys are in cache for cacheType "title"
    	if (cacheType.equalsIgnoreCase("title") && AlbumSetCache.INSTANCE.hasTitles() &&
    			!invalidate.equalsIgnoreCase("invalidate")) {
    		entireCollection.putAll(AlbumSetCache.INSTANCE.getTitles());
    		return entireCollection;
    	}
    				
		long pslS = System.nanoTime();
    	String photoSetListString = cleanJsonResponse(getPhotoSetList()); //can't speed up
		long pslE = System.nanoTime();
		long pslD = (pslE - pslS)/1000000;
		System.out.println("getPhotoSetList()->function time: "+pslD);

		long plS = System.nanoTime();
        JSONObject setListJson = new JSONObject(photoSetListString);
        JSONObject setListObject = setListJson.getJSONObject("photosets");
        
        int setCount = Integer.parseInt(setListObject.get("total").toString());        
        JSONArray setArr = setListObject.getJSONArray("photoset");
        
        // Scan PhotoSetList
        for (int i = 0; i < setCount; i++) {			
			JSONObject photoSetOne = (JSONObject) setArr.get(i);
			String setId = photoSetOne.get("id").toString();
			String dateUpdate =  photoSetOne.get("date_update").toString();                    // <--- order! long date val
			String setTitle = photoSetOne.getJSONObject("title").get("_content").toString();
			String setDesc = photoSetOne.getJSONObject("description").get("_content").toString();
			String setYear = setTitle.replaceAll("-.+","");
			String setType = setTitle.split("-")[1];
			int count = (Integer)photoSetOne.get("photos");
			
			PhotoSet aPhotoSet;
			
			//
			// If we are invalidating a specific photoSetId in the complete indv cache
			//
			if (cacheType.equalsIgnoreCase("id")){
				if (AlbumSetCache.INSTANCE.hasKey(setId) && !invalidate.equals(setId)) {
					aPhotoSet = AlbumSetCache.INSTANCE.getPhotoSet(setId);
				} else {
					String photoJsonString = cleanJsonResponse(getPhotosOfSet(setId)); // Only do if cache is empty
					aPhotoSet = parsePhotoSetResponse(setId, setTitle, setDesc, photoJsonString, dateUpdate);
					aPhotoSet.addSetYear(setYear);
					aPhotoSet.addSetType(setType);
					AlbumSetCache.INSTANCE.expireAndAdd(setId, aPhotoSet);							
				}
				entireCollection.put(aPhotoSet.getDateUpdated(), aPhotoSet);
			
			//
			// If we are invalidating the list version for menu/navigation
			//	
			} else if (cacheType.equalsIgnoreCase("title")) {
				if (AlbumSetCache.INSTANCE.hasTitle(setTitle)) {
					aPhotoSet = AlbumSetCache.INSTANCE.getByTitle(setTitle);
				} else {
					aPhotoSet = new PhotoSet(setId, setTitle, setDesc, count, dateUpdate);
					aPhotoSet.addSetYear(setYear);
					aPhotoSet.addSetType(setType);
					AlbumSetCache.INSTANCE.addByTitle(setTitle, aPhotoSet);							
				}			
				entireCollection.put(aPhotoSet.getPhotoSetTitle(), aPhotoSet);
			}
   
        }
		
		long plE = System.nanoTime();
		long plD = (plE - plS)/1000000;
		System.out.println("retrieveFlickrPhotoSets()->function time: "+plD);

    	return entireCollection;
    }
    
    
    //
    //
    // Helper: Finds the Large Crop from source URL 
    //
    //
    private String getLargeSrcUrl(String mUrl) {
    	return mUrl.replaceAll("\\.(?=[^.]+$)", "_c.");
    }
    
    //
    //
    // Helper: Query boundary
    //
    //
    private String getMultipartBoundary() {
        return "---------------------------7d273f7a0d3";
    }
    
    //
    //
    // Helper: Mulipart body
    //
    //
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
    
    //
    //
    // Helper: Initializes query request
    //
    //
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