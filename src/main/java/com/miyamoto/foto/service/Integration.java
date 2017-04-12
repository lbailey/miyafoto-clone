package com.miyamoto.foto.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.InputStream;

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

import com.miyamoto.foto.service.files.Store;

public class Integration {
	
	private final Logger log = LoggerFactory.getLogger(Integration.class);

	// Caching static variables
	public static final String CACHE_TYPE_TITLE = "title";
	public static final String CACHE_TYPE_PID 	= "id";
	public static final String CACHE_INVALIDATE = "invalidate";
	   
    // Used for album creation; need to change for new projects
    public static final String EMPTY_PHOTO = "15508109759";
    public static final String PROJECT_NAME = "miyamoto-foto";
       
    // Flickr Service/API calls
    private static final String POST_UPLOAD = "https://up.flickr.com/services/upload/";
    private static final String POST_REST   = "https://api.flickr.com/services/rest/";
    private static final String API_QUERY_ALBUM_PHOTOS = "flickr.photosets.getPhotos";
    private static final String API_QUERY_ALBUMS = "flickr.photosets.getList";
    private static final String API_REMOVE_PHOTO = "flickr.photosets.removePhoto";
    private static final String API_MOVE_PHOTO 	 = "flickr.photosets.addPhoto";
    private static final String API_CREATE_ALBUM = "flickr.photosets.create"; 
    
    // Flickr service variables
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MULTIPART	 = "multipart/form-data; boundary=";
    private static final String METHOD		 = "method";
    private static final String PHOTO_TITLE	 = "title";
    private static final String PHOTO_DESC   = "description";
    private static final String PHOTO_PRIMARY_ID = "primary_photo_id";
    private static final String PHOTOSET_ID  = "photoset_id";
    private static final String PHOTO_ID	 = "photo_id";
    private static final String USER_ID      = "user_id";
    private static final String FORMAT_TYPE	 = "format";
    private static final String FORMAT_JSON  = "json";  
    private static final String EXTRA_PARAMS = "extras";
    private static final String PHOTO_EXTRAS = "url_sq, url_m, url_o, date_upload";  
    
    // Flickr response variables
    private static final String PHOTO = "photo";
    private static final String PHOTOS = "photos";
    private static final String FILENAME = "filename";
    private static final String FILETYPE = "filemimetype";
    private static final String PHOTOSET = "photoset";
    private static final String PHOTOSETS = "photosets";
    private static final String PS_TOTAL = "total";
    private static final String PS_TITLE = "title";   
    private static final String ID = "id";
    private static final String SECRET = "secret";
    private static final String TITLE = "title";
    private static final String DATE_UPDATE = "date_update";
    private static final String DATEUPLOAD = "dateupload";
    private static final String CONTENT = "_content";
    
    // JSON static variables
    private static final String JSON_SET_NAME = "setName";
    private static final String JSON_SET_ID = "setId";
    private static final String JSON_SET_TITLE = "setTitle";
    private static final String JSON_SET_COUNT = "setCount";
    private static final String JSON_SET_YEAR = "setYear";
    
    private static final String JSON_PHOTOS = "photos";
    private static final String JSON_PHOTO_SRC = "source";
    private static final String JSON_PHOTO_WIDTH = "width";
    private static final String JSON_PHOTO_HEIGHT = "height";
    private static final String JSON_RESULT = "result";
    
    // Photo URL size options
    private static final String WIDTH_O = "width_o",   WIDTH_SQ = "width_sq",   WIDTH_M = "width_m";    
    private static final String HEIGHT_O = "height_o", HEIGHT_SQ = "height_sq", HEIGHT_M = "height_m"; 
    private static final String URL_O = "url_o", URL_SQ = "url_sq", URL_M = "url_m";       
    
    // Integration variables
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
						.maximumSize(5000)
						.build();
						
		private static Cache<String,PhotoSet> titleCache =
				CacheBuilder.newBuilder()
						.maximumSize(5000)
						.build();				
		
		public void expireAndAdd(String photoSetId, PhotoSet photoSet) {
			setIdCache.invalidate(photoSetId);
			setIdCache.put(photoSetId, photoSet);
		}
		
		public void expire(String photoSetId) {
			setIdCache.invalidate(photoSetId);
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
    public Integration() throws IOException {
    	this.authorized = new OAuth(Store.getOAuthFilePath());
    	this.user = authorized.new User(Store.getUserAuthFilePath());
    	
    	//this.authorized = new OAuth("72157647029609305-838676ff0f16554f","8c281edcae082331","eccbfec6885f6adefb2fd063dce81a20","b114d59e550cf4e9", Permission.DELETE);
    	//this.user = authorized.new User("126576272@N06","miyamoto.foto","Lindsay Papp"); 
    }
    
    public Integration(ImageMeta imageMeta) throws IOException {	
        this.authorized = new OAuth(Store.getOAuthFilePath());
    	this.user = authorized.new User(Store.getUserAuthFilePath());
    
 //   	this.authorized = new OAuth("72157647029609305-838676ff0f16554f","8c281edcae082331","eccbfec6885f6adefb2fd063dce81a20","b114d59e550cf4e9", Permission.DELETE);
 //   	this.user = authorized.new User("126576272@N06","miyamoto.foto","Lindsay Papp"); 
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
    	OAuthRequest request = new OAuthRequest(Verb.POST, POST_UPLOAD);
            	
        Map<String, Object> metaParams = getParameters();
        request.addHeader(CONTENT_TYPE, MULTIPART + getMultipartBoundary());
        for (Map.Entry<String, Object> entry : metaParams.entrySet()) {
            String key = entry.getKey();
            if (!key.equals(PHOTO) && !key.equals(FILENAME) &&  !key.equals(FILETYPE)) {
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
        
       	return cleanUploadResponse(strXml);
    }	
    
    //
    //
    // Create Album/photoSet on Flickr: Tell Flickr to create a new Photo Set/Album with this name
    //
    //
    public String createNewAlbum(String albumName, String albumPrefix) {
    	
    	Map<String, Object> parameters = new HashMap<String, Object>();
		String albumTitle = generateAlbumTitle(albumPrefix, albumName); 
              
        System.out.println("Creating new album with title : " + albumTitle);
        log.info("Creating new album with title: {}", albumTitle);
        
    	OAuthRequest request = new OAuthRequest(Verb.POST, POST_REST);          	  
        request.addHeader(CONTENT_TYPE, MULTIPART + getMultipartBoundary());		
		request.addQuerystringParameter(METHOD, API_CREATE_ALBUM);
		request.addQuerystringParameter(PHOTO_TITLE, albumTitle);
		request.addQuerystringParameter(PHOTO_DESC, WordUtils.capitalize(albumName)); //regex to make capitalized
		request.addQuerystringParameter(PHOTO_PRIMARY_ID, EMPTY_PHOTO);
               
        Token requestToken = new Token(getAuthorize().getToken(), getAuthorize().getTokenSecret());
        ServiceBuilder serviceBuilder = new ServiceBuilder().provider(FlickrApi.class).apiKey(getAuthorize().getApiKey()).apiSecret(getAuthorize().getSharedSecret());
        OAuthService service = serviceBuilder.build();
        
        service.signRequest(requestToken, request);
        
        parameters.putAll(request.getOauthParameters());
        request.addPayload(buildMultipartBody(parameters, getMultipartBoundary()));
        
        Response scribeResponse = request.send();
        String strXml = scribeResponse.getBody();
        
        
        String setId = cleanNewAlbumResponse(strXml);
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

        parameters.put(PHOTOSET_ID, photosetId);
        parameters.put(PHOTO_ID, photoId);
        
    	OAuthRequest request = new OAuthRequest(Verb.POST, POST_REST);
            	  
        request.addHeader(CONTENT_TYPE, MULTIPART + getMultipartBoundary());		
		request.addQuerystringParameter(METHOD, API_MOVE_PHOTO);
		request.addQuerystringParameter(PHOTOSET_ID, photosetId);
		request.addQuerystringParameter(PHOTO_ID, photoId);
               
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
    // Post to Flickr: Tell Flickr to move image with specific ID to specific photo set with ID
    //
    //
    public String removePhotoFromAlbum(String photoSetId, String photoId) {
    	
    	Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put(PHOTOSET_ID, photoSetId);
        parameters.put(PHOTO_ID, photoId);
        
    	OAuthRequest request = new OAuthRequest(Verb.POST, POST_REST);
            	  
        request.addHeader(CONTENT_TYPE, MULTIPART + getMultipartBoundary());		
		request.addQuerystringParameter(METHOD, API_REMOVE_PHOTO);
		request.addQuerystringParameter(PHOTOSET_ID, photoSetId);
		request.addQuerystringParameter(PHOTO_ID, photoId);
               
        Token requestToken = new Token(getAuthorize().getToken(), getAuthorize().getTokenSecret());
        ServiceBuilder serviceBuilder = new ServiceBuilder().provider(FlickrApi.class).apiKey(getAuthorize().getApiKey()).apiSecret(getAuthorize().getSharedSecret());
        OAuthService service = serviceBuilder.build();
        
        service.signRequest(requestToken, request);
        
        parameters.putAll(request.getOauthParameters());
        request.addPayload(buildMultipartBody(parameters, getMultipartBoundary()));
        
        Response scribeResponse = request.send();
        String strXml = scribeResponse.getBody();
        
        //kind of inappropriate hammer destroy method, rethink removing single photo from cache
        PhotoSet aPhotoSet = AlbumSetCache.INSTANCE.getPhotoSet(photoSetId);
        String setYear = aPhotoSet.getSetYear(), setType = aPhotoSet.getSetType();
        String photoJsonString = cleanJsonResponse(getPhotosOfSet(photoSetId)); // Only do if cache is empty
		aPhotoSet = parsePhotoSetResponse(photoSetId, aPhotoSet.getPhotoSetTitle(), aPhotoSet.getPhotoSetName(), photoJsonString, aPhotoSet.getDateUpdated());	
		aPhotoSet.addSetYear(setYear);
		aPhotoSet.addSetType(setType);
		
		AlbumSetCache.INSTANCE.expireAndAdd(photoSetId, aPhotoSet);		
        
        return strXml;
    
    }
 
    //
    //
    // JSON: send list of albums by title-PhotoSet to use for side-navigation
	// 
	//    
    public String albumListToJson(String selectedYear) {
		JSONObject jsonObject = new JSONObject();				//use cache when possible
		TreeMap<String,PhotoSet> completeList = retrieveFlickrPhotoSets("year-type-title",CACHE_TYPE_TITLE);
	
		JSONObject collObj = new JSONObject();
		JSONArray typeArr = new JSONArray();
		String prevType = "", currentType;
		int albumCount = completeList.size(), acc = 1;
		for (String album : completeList.keySet()) {	
				
			if (album.startsWith(selectedYear)) {		
				currentType = completeList.get(album).getSetType();
				
				//handles first iter
				if (prevType.isEmpty()) {
					collObj.put(currentType, typeArr);
					prevType = currentType;
				} 
				
				//I'm only saving when the type changes, meaning I need to save the last bit
				//maybe a set a flag for when data saved?
				//write at the end of the loop now
				if (!currentType.matches(prevType)) {
				   collObj.put(prevType, typeArr);
				   typeArr = new JSONArray();
				} 
				
				JSONObject setJson = new JSONObject();
				PhotoSet ps = completeList.get(album);
				setJson.put(JSON_SET_NAME, ps.getPhotoSetName());
				setJson.put(JSON_SET_ID, ps.getPhotoSetId());
				setJson.put(JSON_SET_TITLE, ps.getPhotoSetTitle());
				setJson.put(JSON_SET_COUNT, ps.getPhotoSetCount());
				typeArr.put(setJson);
				prevType = currentType;
			
			} 
			//if last album, grab the stuffs since I only write onchange
			if (albumCount == acc) {
				collObj.put(prevType, typeArr);
			}
			acc++;
		}
		
		jsonObject.put(JSON_RESULT, collObj);		
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
        parameters.put(USER_ID, getUser().getUserId());
	
    	OAuthRequest request = new OAuthRequest(Verb.POST, POST_REST);
            	       
        request.addHeader(CONTENT_TYPE, MULTIPART + getMultipartBoundary());		
		request.addQuerystringParameter(METHOD, API_QUERY_ALBUMS);
		request.addQuerystringParameter(USER_ID, getUser().getUserId());
		request.addQuerystringParameter(FORMAT_TYPE, FORMAT_JSON);
                
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
		log.info("retrieve photo set list : {}", pslD);
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
        parameters.put(PHOTOSET_ID, setId);
        
        
    	OAuthRequest request = new OAuthRequest(Verb.POST, POST_REST);     
        request.addHeader(CONTENT_TYPE, MULTIPART + getMultipartBoundary());
		
		
		request.addQuerystringParameter(METHOD, API_QUERY_ALBUM_PHOTOS);
		request.addQuerystringParameter(PHOTOSET_ID, setId);
		request.addQuerystringParameter(EXTRA_PARAMS, PHOTO_EXTRAS);
		request.addQuerystringParameter(FORMAT_TYPE, FORMAT_JSON);
              
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
		log.info("getPhotosOfSet()->function time: {}", posD);
		
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
        JSONObject setJson = new JSONObject(); 
    	setJson.put(JSON_SET_TITLE, ps.getPhotoSetTitle());
    	setJson.put(JSON_SET_NAME, ps.getPhotoSetName());
    	setJson.put(JSON_SET_YEAR, ps.getSetYear());
    	
    	// create descending iterator
     	Iterator<Photo> photosDescending = ps.getPhotoSet().descendingIterator();
		
			JSONObject photoJson = new JSONObject();
			while (photosDescending.hasNext()){
				Photo p = photosDescending.next();
				JSONObject cropJson = new JSONObject();
				for (Crop crop : p.getCropList()) {
					JSONObject cropInfo = new JSONObject();
					cropInfo.put(JSON_PHOTO_SRC,crop.getCropSrc());
					cropInfo.put(JSON_PHOTO_WIDTH, crop.getWidth());
					cropInfo.put(JSON_PHOTO_HEIGHT, crop.getHeight());
					cropJson.put(crop.getCropLabel(), cropInfo);
				}
				photoJson.put(p.getPhotoId(), cropJson);
			}
			setJson.put(JSON_PHOTOS, photoJson);
				
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
			setJson.put(JSON_SET_NAME, ps.getPhotoSetName());
			setJson.put(JSON_SET_ID, ps.getPhotoSetId());
			setJson.put(JSON_SET_TITLE, ps.getPhotoSetTitle());
			setJson.put(JSON_SET_COUNT, ps.getPhotoSetCount());
			setJson.put(JSON_SET_YEAR, ps.getSetYear());
			
			// create descending iterator
     		Iterator<Photo> photosDescending = ps.getPhotoSet().descendingIterator();
			
			JSONObject photoJson = new JSONObject();
			while (photosDescending.hasNext()){
				Photo p = photosDescending.next();
				JSONObject cropJson = new JSONObject();
				for (Crop crop : p.getCropList()) {
					JSONObject cropInfo = new JSONObject();
					cropInfo.put(JSON_PHOTO_SRC,crop.getCropSrc());
					cropInfo.put(JSON_PHOTO_WIDTH, crop.getWidth());
					cropInfo.put(JSON_PHOTO_HEIGHT, crop.getHeight());
					cropJson.put(crop.getCropLabel(), cropInfo);
				}
				photoJson.put(p.getPhotoId(), cropJson);
			}
			setJson.put(JSON_PHOTOS, photoJson);
			collArray.put(setJson);
		}
		
		jsonObject.put(JSON_RESULT, collArray);		
		return jsonObject.toString();           
    }
    
    //
    //
    // Interpreting: Parses single PhotoSet response from Flickr to PhotoSet object
    //
    //
    public PhotoSet parsePhotoSetResponse(String setId, String setTitle, String setDesc, String photoJsonString, String dateOfSet) {    
		JSONObject photosJson = new JSONObject(photoJsonString);
		JSONObject photosObject = photosJson.getJSONObject(PHOTOSET);
		
		int photoCount = Integer.parseInt(photosObject.get(PS_TOTAL).toString());
		String photoSetTitle = photosObject.get(PS_TITLE).toString();
		
		/*
		PhotoSet aPhotoSet = AlbumSetCache.INSTANCE.hasTitle(photoSetTitle) && !dateOfSet.isEmpty() ? 
							 AlbumSetCache.INSTANCE.getByTitle(photoSetTitle) :
							 new PhotoSet(setId, setTitle, setDesc, photoCount, dateOfSet);
		*/
		
		//System.out.println(setTitle + " " + setDesc + " " + dateOfSet);
		log.debug("{} {} {}", setTitle, setDesc, dateOfSet);
		PhotoSet aPhotoSet = new PhotoSet(setId, setTitle, setDesc, photoCount, dateOfSet);
					
		JSONArray photosArr = photosObject.getJSONArray(PHOTO);
		
		// TODO: Pull the date in extras, check against the date and create a chron list
		// "date_upload" is the key
		for (int k = 0; k < photoCount; k++) {
			JSONObject photoOne = (JSONObject) photosArr.get(k);
			String photoId = photoOne.get(ID).toString();
			String photoSecret = photoOne.get(SECRET).toString();
			String photoTitle = photoOne.get(TITLE).toString();
			String photoUpdated = photoOne.get(DATEUPLOAD).toString(); 
	
			double w = Double.parseDouble(photoOne.get(WIDTH_M).toString());
			double h = Double.parseDouble(photoOne.get(HEIGHT_M).toString());
			double photoAspect = w/h;
			
			String url_c = getLargeSrcUrl(photoOne.get(URL_M).toString());
			Photo aPhoto = new Photo(photoId, photoSecret, photoTitle, photoAspect, Integer.parseInt(photoUpdated));
			aPhoto.addSquareCrop(photoOne.get(URL_SQ).toString(), photoOne.get(WIDTH_SQ).toString(), photoOne.get(HEIGHT_SQ).toString());  
			aPhoto.addMediumCrop(photoOne.get(URL_M).toString(), photoOne.get(WIDTH_M).toString(), photoOne.get(HEIGHT_M).toString());    
			aPhoto.addOriginal(photoOne.get(URL_O).toString(), photoOne.get(WIDTH_O).toString(), photoOne.get(HEIGHT_O).toString());
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
    	if (cacheType.equalsIgnoreCase(CACHE_TYPE_TITLE) && AlbumSetCache.INSTANCE.hasTitles() &&
    			!invalidate.equalsIgnoreCase(CACHE_INVALIDATE)) {
    		entireCollection.putAll(AlbumSetCache.INSTANCE.getTitles());
    		return entireCollection;
    	}
    				
		long pslS = System.nanoTime();
    	String photoSetListString = cleanJsonResponse(getPhotoSetList()); //can't speed up
		long pslE = System.nanoTime();
		long pslD = (pslE - pslS)/1000000;
		System.out.println("getPhotoSetList()->function time: "+pslD);
		log.info("getPhotoSetList()->function time: {}", pslD);

		long plS = System.nanoTime();
        JSONObject setListJson = new JSONObject(photoSetListString);
        JSONObject setListObject = setListJson.getJSONObject(PHOTOSETS);
        
        int setCount = Integer.parseInt(setListObject.get(PS_TOTAL).toString());        
        JSONArray setArr = setListObject.getJSONArray(PHOTOSET);
        
        // Scan PhotoSetList
        for (int i = 0; i < setCount; i++) {			
			JSONObject photoSetOne = (JSONObject) setArr.get(i);
			String setId = photoSetOne.get(ID).toString();
			String dateUpdate =  photoSetOne.get(DATE_UPDATE).toString();    // <--- order! long date val
			String setTitle = photoSetOne.getJSONObject(PS_TITLE).get(CONTENT).toString();
			String setDesc = photoSetOne.getJSONObject(PHOTO_DESC).get(CONTENT).toString();
			String setYear = setTitle.replaceAll("-.+","");
			String setType = setTitle.split("-")[1];
			int count = (Integer)photoSetOne.get(PHOTOS);
			
			PhotoSet aPhotoSet;
			
			//
			// If we are invalidating a specific photoSetId in the complete indv cache
			//
			if (cacheType.equalsIgnoreCase(CACHE_TYPE_PID)){
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
			} else if (cacheType.equalsIgnoreCase(CACHE_TYPE_TITLE)) {			
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
		log.info("retrieveFlickrPhotoSets()->function time: {}", plD);

    	return entireCollection;
    }
    

	//
	//
	// Helper: Clean Flickr Image Upload response string
	//
	//
	private String cleanUploadResponse(String strXml) {
		return strXml.replaceAll("\\<([^<>]+)\\>","").replaceAll("\\%0A","");
	}
	
	//
	//
	// Helper: Clean Flickr New Album response string
	//
	//
	private String cleanNewAlbumResponse(String strXml) {
	 	return strXml.replaceAll("\\n","").replaceAll("(.+id=\"|\".*)","");
	}

	//
	//
	// Helper: Generate album title from name
	//
	//
	private String generateAlbumTitle(String prefix, String name) {
		return prefix + "-" + name.replaceAll(" ","-").toLowerCase(); //regex on name
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
            log.error("{}", e.getMessage());
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