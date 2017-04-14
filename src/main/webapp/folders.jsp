<%@ page import="com.miyamoto.foto.service.Integration" %>
<%
   final String emptyPhoto = Integration.EMPTY_PHOTO;
%>

<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
<head>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>

</head>
  <body style="padding: 0px; width: 150px; margin: auto; padding-right: 3px;">

	<div id="wrapper">
		<h5 class="recently-updated">recent</h5>
		<img class="loading-gif" src="/includes/spinningwheel.gif"/>
	</div>

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script type="text/javascript">
  $(document).ready(function ($) {

  function moveLeft(sliderName) {
        $('div[name="' + sliderName + '"] section').animate({
        	opacity: .75,
            left: +200
        }, 200, function () {
        	for (var i = 0; i < 2; i++) {
              $('div[name="' + sliderName + '"] section img:last-child').prependTo('div[name="' + sliderName + '"] section');
              $('div[name="' + sliderName + '"] section img:first-child').css('margin-right', '2px');
              $('div[name="' + sliderName + '"] section').fadeTo(200,1);
            }
        });
    };

    function moveRight(sliderName) {  
        $('div[name="' + sliderName + '"] section').animate({
            opacity: .75,
            left: -200
        }, 200, function () {
          for (var i = 0; i < 2; i++) {
            $('div[name="' + sliderName + '"] section img:first-child').appendTo('div[name="' + sliderName + '"] section');
            $('div[name="' + sliderName + '"] section img:last-child').css('margin-right', '2px');
            $('div[name="' + sliderName + '"] section').fadeTo(200,1);
          }
        });
    };
    
    $("#wrapper").on("click", "div > div > div.title-overlay > div.arrow-right", function () {
        var parentTag = $(this).parent().parent().get(0).getAttribute("name");
        moveRight(parentTag);
        return false;
    });
    
    $("#wrapper").on("click", "div > div > div.title-overlay > div.arrow-left", function () {
        var parentTag = $(this).parent().parent().get(0).getAttribute("name");
        moveLeft(parentTag);
        return false;
    });
    
    $("#wrapper").on("click", "div > div > div.title-overlay", function () {
        var viewport = window.parent.document.getElementById('center');
        var parentTag = $(this).parent().get(0).getAttribute("name");
		viewport.src = "http://"+$(location).attr('host') + "/viewport.jsp#"+parentTag;
		//console.log(viewport.src);
		viewport.contentWindow.location.reload();
		viewport.src = viewport.src;
    });

});   

  var hash = window.location.hash.substring(1);
  var photoEmpty = "<%= emptyPhoto %>";
  var isEmpty = "";

  $.getJSON('/flickr/albums?invalidateAlbum='+hash, function(json) {
  	$.each(json,function(c, coll){
  	  $.each(coll, function(i, value) {
  	  
  	    if (parseInt(value.setCount) > 1) {
  	    //console.log("writing " + i );
   		var html = "";
		html += "<div id=\"sectionWrapper\" name=\""+ value.setId+"\">" +
	    		"<div id=\"slider\" name=\""+ value.setId+"\"><section class=\"imgSet\">";
		
		$.each(value.photos,function(photoId, data){	
		   if (photoId != photoEmpty) {		
			$.each(data,function(size, crops){
				if (size === 'Square') {
				    html += "<img src=\""+ crops.source +"\" id=\""+ photoId +"\"/>\n";
				}
			}); }
    	});
    	html += "</section><div class=\"title-overlay\"><span class=\"year\">"+value.setYear + " " + 
    			value.setName+"</span><div class=\"arrow-left\"/><div class=\"arrow-right\"/></div></div></div>";
		
		$("#wrapper > img").css({"display": "none"});
		$("#wrapper").append(html);
		}
		return i<14;
    });
  });
});

$(window).bind('load', function() {
    var height = $(window.parent.document).height() + 100;
    $("#right",parent.document).css("height", height + "px"); // I've moved up the right div, account for it
});

</script> 
  </body>
</html>
