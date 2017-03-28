<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
<head>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>

</head>
  
<body>
<p class="helperText pad30">Upload to Album</p>
<div id="albumYearSelect"><span class="helperText">Year: </span></div>
<div id="accordian" name="photoSets">
	<ul>
		
	</ul>
</div>


<div id="mulitplefileuploader"></div>
 <div id="status"></div>         </p>
              
<div style="margin-left: auto; margin-right: auto; text-align: center;">
<br /><br />

    	</div><!-- / #canvas -->
    </div><!-- / #canvasWrapper -->

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="/includes/jquery.fileuploadmulti.min.js"></script>
<script type="text/javascript">
 // document.domain = "miyafoto";
  
  var photoSetId;  
$(document).ready(function(){
	
	var minOffset = 0, maxOffset = 7; // Change to whatever you want
	var thisYear = (new Date()).getFullYear();
	var select = $('<select id="albumYear">');
	var typeSet = ['formal','shower','festival','holiday','athletic','misc' ];
	var difference = [];

	for (var i = minOffset; i <= maxOffset; i++) {
    	var year = thisYear - i;
    	$('<option>', {value: year, text: year}).appendTo(select);
	}
	select.appendTo('#albumYearSelect');
	
	
	$("#albumYearSelect").bind("change", function(){
		$("#accordian > ul").empty(); 
		var year = $("#albumYearSelect").find(":selected").text();
		$.getJSON('/flickr/albums?albumYear='+year, function(json) {
		  $.each(json,function(c, coll){
		 // console.log(year + " size: " + Object.keys(coll).length  + " " + Object.keys(coll) + $.isEmptyObject(coll));
			$.each(coll, function(photoType, photoSet) {
			  var html = "<li><p>" + photoType + "</p><ul>";      
			  $.each(photoSet, function(i, album) {   
				html += "<li><a href=\"#\" photoSetId=\""+album.setId+"\">"+album.setName+"</a></li>";             
			  });
		 
			  html += "<li><a href=\"#\" class=\"new-album\">add new album</a><form class=\"new-album-form\" " + 
						"action=\"/flickr/albums\"><input type=\"submit\" value=\"add\"><input type=\"text\" " +
						"name=\"albumName\" pattern=\"[a-zA-Z0-9\\s]+\" oninvalid=\"setCustomValidity('please no punctuation')\" oninput=\"setCustomValidity('')\"></form></li>";
			  html += "</ul></li>";
		
			  $("#accordian > ul").append(html);   
			  //$("#accordian > ul > li:nth-child(2) > p").next().slideDown();		
			});
		    
		    if (Object.keys(coll).length < 6) {
		      difference = $.grep(typeSet,function(x) {return $.inArray(x, Object.keys(coll)) < 0});
		    } 

		   
		   //if difference has stuff
		   if (!$.isEmptyObject(difference)) {
		     $.each(difference, function(missIn, missCat) {
		   		var emptyAddHtml = "<li><p>" + missCat + "</p><ul>" +
		   				"<li><a href=\"#\" class=\"new-album\">add new album</a><form class=\"new-album-form\" " + 
						"action=\"/flickr/albums\"><input type=\"submit\" value=\"add\"><input type=\"text\" " +
						"name=\"albumName\"></form></li></ul></li>";
				$("#accordian > ul").append(emptyAddHtml);  
		   	});
		   }	
		   $("#accordian > ul > li:nth-child(2) > p").next().slideDown();	   
		  });
		});
		return false;
	});	
	
	$('#albumYearSelect').trigger('change');

	
	$("#accordian").on("click", "ul > li > p", function(){
		$("#accordian ul ul").slideUp();
		$("#accordian ul li").removeClass("active");
		if(!$(this).next().is(":visible")) {
			$(this).next().slideDown();
			$(this).parent().addClass("active");
		} 
		return false;
	});


	$("#accordian").on("click", "ul > li > ul > li > a.new-album", function () {
		$('.selected').removeClass('selected');				
		$(this).addClass('selected');
		$(this).next().fadeIn(200);
		return false;      
    });
    
    
	$("#accordian").on("click", "ul > li > ul > li > a", function () {
		$('#ajax-upload').css({"display":"none"});
		photoSetId = "";
		$('.selected').removeClass('selected');				
		$(this).addClass('selected');
		if (!$(this).hasClass('new-album')) {
		  $('#ajax-upload').css({"display":"block"});
		  photoSetId = $(this).attr('photoSetId');
		  $('.new-album-form').hide();
		} 
		return false;     
    });
        	
	var settings = {
    url: "/flickr/upload",
    method: "POST",
    allowedTypes:"jpg,jpeg,png,gif",
    multiple: true,
    onSubmit:function(responseData,files) {
        $("#status").html("<div style='color:red;margin:15px;'>Uploading...</div>");
        $('#status').insertAfter("#mulitplefileuploader");
    },
    onSuccess:function(files,responseData,xhr) {
        $.post( "/flickr/albums", { photoSetId: photoSetId, photoId: responseData.trim() } );
    },
    afterUploadAll:function() {
        $("#status").html("<div style='color:green;margin:15px;'>Upload successful</div>");
		$('#ajax-upload').parent().find("input[type=file]").val("");
		$('#ajax-upload').parent().css({"display":"block"});
        //$('#right').contentWindow.location.reload(true);
        
        //var folders = window.parent.document.getElementById('right');
		//folders.src = folders.src;
		var folders = window.parent.document.getElementById('right');
		folders.src = "http://"+$(location).attr('host') + "/folders.jsp#"+photoSetId;
		console.log(folders.src);
		folders.contentWindow.location.reload();
		folders.src = folders.src;
		
		loadCenter();
    },
    onError: function(files,status,errMsg) {        
        $("#status").html("<font color='red;margin-left:15px;'>Upload failed</font>");
    }}
    
    
    $("#mulitplefileuploader").uploadFile(settings);
    
    //
    // might not be able to do
    //
	function loadCenter() {
    	var viewport = window.parent.document.getElementById('center');
		viewport.src = "http://"+$(location).attr('host') + "/success.jsp";
		console.log(viewport.src);
		viewport.contentWindow.location.reload();
		viewport.src = viewport.src;
	}
    
    
    $("#accordian").on("submit", "ul > li > ul > li > form.new-album-form", function () {
    	console.log('here');
		event.preventDefault();
		var $form = $(this),
			$list = $form.parents().eq(2),
    		albumEntry = $form.find("input[name='albumName']").val(),
    		albumPrefix = $('#albumYear').val() + "-" + $list.children('p').text().toLowerCase(),
    		url = $form.attr("action");
    		
    	console.log(albumPrefix + " " + albumEntry);
 
  		var newAlbum = $.post(url, {"albumName": albumEntry, "albumPrefix": albumPrefix});

  		newAlbum.done(function(data) {
    		var setId = data;
			$form.fadeOut(400);
  			$('a.new-album.selected').removeClass('selected');
  			$list.children('ul').find(' > li:nth-last-child(1)').before('<li><a href="#" photoSetId="'+ setId +'" class="selected">'+albumEntry+'</a></li>');
  			$('#ajax-upload').css({"display":"block"});
		  	photoSetId = setId;
  		});
	});
});

  </script>

  </body>
</html>
