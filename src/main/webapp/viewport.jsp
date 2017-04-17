<%@ page import="com.miyamoto.foto.service.AuthProxy,
				 com.miyamoto.foto.service.Integration" %>
<%
   boolean canRemove = AuthProxy.canRemove(request);
   final String emptyPhoto = Integration.EMPTY_PHOTO;
%>

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
		  <img class="loading-gif" src="/includes/hex-loader.gif"/>
		  <div id="collage"></div>
		  <!--<img src="/includes/hex-loader.gif"/>	-->
		</div><!-- / #canvasScrollbar -->
		
    	</div><!-- / #canvas -->
    </div><!-- / #canvasWrapper -->
 

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="/includes/jquery.collagePlus.min.js"></script>
<script src="/includes/jquery.collageCaption.min.js"></script>
<script type="text/javascript">  
$(document).ready(function ($) {
	
	var innerWidth = $("#canvas").innerWidth();
		properScale = innerWidth/4;
		
	var viewheight = $('body', window.parent.document).height();
	
	var hash = window.location.hash.substring(1);
	var oSrc, oWidth, oHeight;	
	var mSrc, mWidth, mHeight;
	var photoEmpty = "<%= emptyPhoto %>";
	var imgCt = 0;	
	
	var setName, setYear, setTitle;
	var canRemove = <%= canRemove %>;
	var htmlCollage = "";
	var isEmpty;

    $.ajax({
        url: '/flickr/albums?photoSetId='+hash,
        dataType:   "json",
    	success:    function(json){
	  	
		  setName = json.setName;
		  setYear = json.setYear;
		  setTitle = json.setTitle;
		
			$.each(json.photos, function(photoId, data) {
				if (photoId != photoEmpty) {	
				$.each(data,function(size, crops){
					if (size == 'Original') {
						oSrc = crops.source;
					}
					if (size === 'Medium') {
						mSrc = crops.source;
						mWidth = crops.width;
						mHeight = crops.height;
					}
				});
					
				htmlCollage += "<div class=\"image-wrapper\" data-caption=\"<a href='" + oSrc + 
							   "' style='font-size: 13pt;color: white;font-weight: bold;float: right;margin-right: 5px;'" + 
							   "download><span style='font-size: 9pt;font-weight: 100;line-height: 16pt;letter-spacing: .5px;font-style: italic;'>download&nbsp;</span>&#9112;</a>&nbsp;&nbsp;";
							   
				if (canRemove) { 
					htmlCollage += "<a href='#' style='color: white;font-weight: bold;' class='delete-img' id='"+photoId+"'>&#10005;</a>"; 
				}
				
				htmlCollage += "\"><img src=\"" + mSrc +"\" /></div>";
				imgCt++;	
				} 			
			  }); 
			  
			isEmpty = Object.keys(json.photos).length == 1;
		},
		complete: function() {
			$("#collage").html(htmlCollage);
			$("#albumTitleInfo").html(setYear+ " " + setName);
			$("#albumTitleInfo").css({visibility:"visible"});
			
			if (isEmpty) {
			  $("#albumTitleInfo").append(" - empty");
			  $(".loading-gif").hide();
			}
			
			var time = $("#collage").children().size() * 100;
    		setTimeout(collage, 1000 + time);
		}
    });	
    
    $("#canvas").on("click", "div > div > div > div > div > a.delete-img", function (e) {
		e.preventDefault();
        if (window.confirm("Are you sure you want to delete this photo?")) {
		    var $tainer = $( this ),
    		    imgId = $tainer.attr('id') ;
    		
    	    // Send the data using post
  		    var posting = $.post( "/flickr/remove", { photoId: imgId, photoSetId: hash } );
  		        posting.done(function( data ) {
    		        console.log('posted ' + $tainer.parent().parent().parent().attr('class'));
    		        $tainer.parent().parent().parent().fadeOut().queue(function(nxt) { 
                		$(this).remove();
                		setTimeout(collage, 300);
                		nxt();
            		});
  		    	});
  			}
	  });
}); 

// dom needs to be fully loaded
function collage() {
	$(".loading-gif").hide();
    $("#collage").collagePlus({'targetHeight': 300, 'direction': 'horizontal', 'allowPartialLastRow': true });		
    $("#collage").collageCaption();
    if ($("#canvas").height() > 1200) {
      //var height = $("#canvas").css("height");
      var height = $("#canvas").height() + 42; //the answer to the universe
      $("#left",parent.document).css("height", height + "px");
      $("#center",parent.document).css("height", height+ "px");
      $("#center-wide",parent.document).css("height", height+ "px");
      $("#right",parent.document).css("height", (height+ 110) + "px");
    }
};

var resizeTimer = null;
$(window).bind('resize', function() {
    $('#collage .image-wrapper').css("opacity", 0);
    if (resizeTimer) clearTimeout(resizeTimer);
    resizeTimer = setTimeout(collage, 200);
});


  </script>
  </body>
</html>
