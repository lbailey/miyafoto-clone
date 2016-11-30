<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
<head>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>

</head>
  <body style="padding: 0px; width: 150px; margin: auto; padding-right: 3px;">

	<div id="wrapper">
		<h5 class="recently-updated">recent</h5>
		<img class="loading-gif" src="/includes/hex-loader.gif"/>
	</div>

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script type="text/javascript">
//  document.domain = "miyafoto";
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
    
    $("#wrapper").on("click", "div > div > span > a.control_next", function () {
        var parentTag = $(this).parent().parent().get(0).getAttribute("name");
        moveRight(parentTag);
        return false;
    });
    
    $("#wrapper").on("click", "div > div > span > a.control_prev", function () {
        var parentTag = $(this).parent().parent().get(0).getAttribute("name");
        moveLeft(parentTag);
        return false;
    });
    
    $("#wrapper").on("click", "div > div > div.albumTitle", function () {
        var viewport = window.parent.document.getElementById('center');
        var parentTag = $(this).parent().get(0).getAttribute("name");
		viewport.src = "http://"+$(location).attr('host') + "/viewport.jsp#"+parentTag;
		console.log(viewport.src);
		viewport.contentWindow.location.reload();
		viewport.src = viewport.src;
    });

});   

  var hash = window.location.hash.substring(1);
  var photoEmpty = "15508109759";
  var isEmpty = "";
  console.log("corrent to inval" + hash);
  $.getJSON('/flickr/albums?invalidateAlbum='+hash, function(json) {
  	$.each(json,function(c, coll){
  	  $.each(coll, function(i, value) {
  	  
  	    if (parseInt(value.setCount) > 1) {
  	    console.log("writing " + i );
   		var html = "";
		html += "<div id=\"sectionWrapper\" name=\""+ value.setId+"\">" +
	    		"<div id=\"slider\" name=\""+ value.setId+"\">" +
	    			"<div class=\"albumTitle\">"+ value.setYear+ " " + value.setName+"</div>" +
	    			"<span class=\"nav-wrapper\">" + 
	    			"<a href=\"#\" class=\"control_prev\">&#8672;</a>" +
	    			"<span class=\"quick-view\">quick view</span>" +
	  		    	"<a href=\"#\" class=\"control_next\">&#8674;</a></span>" +
					"<section class=\"imgSet\">";
		
		console.log('length '+ value.setCount);
		
		$.each(value.photos,function(photoId, data){	
		   if (photoId != photoEmpty) {		
			$.each(data,function(size, crops){
				if (size === 'Square') {
				    html += "<img src=\""+ crops.source +"\" id=\""+ photoId +"\"/>\n";
				}
			}); }
    	});
    	html += "</section></div></div>";
		$("#wrapper > img").css({"display": "none"});
		$("#wrapper").append(html);
		}
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
