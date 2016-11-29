<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
  <head>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>

</head>
  <body style="padding: 28px 20px 0 0;">
  
    <div id="canvasWrapper">
    	<div id="canvas" style="padding: 20px 0 20px 0; top: 15px;">
		
		<div id="canvasScrollbar" style="overflow-y: auto; overflow-x: hidden;">
		  <h5 id="albumTitleInfo"></h5>
		  <div id="collage"></div>
		  <!--<img src="/includes/hex-loader.gif"/>	-->
		</div><!-- / #canvasScrollbar -->
		
    	</div><!-- / #canvas -->
    </div><!-- / #canvasWrapper -->
 

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="/includes/jquery.collagePlus.js"></script>
<script src="/includes/jquery.collagePlus.min.js"></script>
<script type="text/javascript">
  //document.domain = "miyafoto";  
$(document).ready(function ($) {
	
	var innerWidth = $("#canvas").innerWidth();
		properScale = innerWidth/4;
		
	var viewheight = $('body', window.parent.document).height();
	
	var hash = window.location.hash.substring(1);
	var oSrc, oWidth, oHeight;	
	var mSrc, mWidth, mHeight;
	var photoEmpty = "15508109759";
	var imgCt = 0;	
	
	var setName, setYear;
/*
    $.getJSON('/flickr/albums?photoSetId='+hash, function(json) {
      	
      setName = json.setName;
      setYear = json.setYear;
      var htmlCollage = "";
		
		$.each(json.photos, function(photoId, data) {
		    if (photoId != photoEmpty) {	
			$.each(data,function(size, crops){
				if (size == 'Large') {
					cSrc = crops.source;
				}
				if (size === 'Medium') {
					mSrc = crops.source;
					mWidth = crops.width;
					mHeight = crops.height;
				}
			});
					
			htmlCollage += "<img src=\"" + mSrc +"\"  csource=\""+ cSrc + "\" />";
			imgCt++;	
			} 			
		  }); 
		  
		$("#collage").html(htmlCollage);
		$("#albumTitleInfo").html(setYear+ " " + setName);
	  
	});
*/
    $.ajax({
        url: '/flickr/albums?photoSetId='+hash,
        dataType:   "json",
    	success:    function(json){
	  	
		  setName = json.setName;
		  setYear = json.setYear;
		  var htmlCollage = "";
		
			$.each(json.photos, function(photoId, data) {
				if (photoId != photoEmpty) {	
				$.each(data,function(size, crops){
					if (size == 'Large') {
						cSrc = crops.source;
					}
					if (size === 'Medium') {
						mSrc = crops.source;
						mWidth = crops.width;
						mHeight = crops.height;
					}
				});
					
				htmlCollage += "<img src=\"" + mSrc +"\"  csource=\""+ cSrc + "\" />";
				imgCt++;	
				} 			
			  }); 
		  
			$("#collage").html(htmlCollage);
			$("#albumTitleInfo").html(setYear+ " " + setName);
		}
    });	
    
	$("#canvas").on("click", "div > ul > li", function (event) {
		event.preventDefault();

		var lightbox = '<div id="lightbox">' +
		'<div id="content">' + 
		'<img src="' + $(this).attr( "csource" ) +'" style="height: '+viewheight/3+'px; margin-top: '+ viewheight/32 +'px;" />' + 
		'<span class="zoomed"></span></img></div>' +	
		'</div>';
		$('body', window.parent.document).append(lightbox);
	});
	
	$("#canvas").on("click", "div > div > img", function (event) {
		event.preventDefault();

		var lightbox = '<div id="lightbox">' +
		'<div id="content">' + 
		'<img src="' + $(this).attr( "csource" ) +'" style="height: '+viewheight/3+'px; margin-top: '+ viewheight/32 +'px;" />' + 
		'<span class="zoomed"></span></img></div>' +	
		'</div>';
		$('body', window.parent.document).append(lightbox);
	});
	
	$('body', window.parent.document).on("click", function (event) {
		$('body', window.parent.document).on("click", "div#lightbox", function() {
			$(this).hide();
		});
		return false;
	});

	$(".downloadAll").on("click", function (event) {
		// download all files as zip
		return false;
	});
}); 

// dom needs to be fully loaded
function collage() {
    $("#collage").collagePlus({'targetHeight': 300, 'direction': 'horizontal', 'allowPartialLastRow': true });		
    if ($("#canvas").height() > 1200) {
      var height = $("#canvas").css("height");
      $("#left",parent.document).css("height", height);
      $("#center-wide",parent.document).css("height", height);
    }
};

var resizeTimer = null;
$(window).bind('resize', function() {
    $('.collage .Image_Wrapper').css("opacity", 0);
    if (resizeTimer) clearTimeout(resizeTimer);
    resizeTimer = setTimeout(collage, 200);
});

$(window).bind('load', function() {
    setTimeout(collage, 100);
});



  </script>
  </body>
</html>
