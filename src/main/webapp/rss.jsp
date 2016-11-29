<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
  <head>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>

</head>
  <body style="padding: 28px 20px 0 0;">
  
    <div id="canvasWrapper">
    	<div id="canvas" style="padding: 20px 0 20px 0; top: 15px;">
		
		<div id="canvasScrollbar" style="overflow-y: auto; overflow-x: hidden;">
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
	var htmlCollage = "";
	var imgCt = 0;	
	
	var setName, setYear;
	
	$.ajax({
        url: 'https://api.flickr.com/services/feeds/photos_public.gne?id=126576272@N06&format=json&jsoncallback=?',
        dataType:   "jsonp",
    	success:    function(data){
    	  $.each(data.items, function(k, val) {
    	  	$.each(val.media, function(m, mUrl) {
    	  	  console.log(mUrl);
    	  	  htmlCollage += "<img src=\"" + mUrl.replace("_m.","_z.") +"\" />";
			  imgCt++;
    	    });
    	  });
    	  
    	 $("#collage").html(htmlCollage); 
        }
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
