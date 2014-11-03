<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
  <head>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>

</head>
  <body style="padding: 28px 0 0 0;">
  
    <div id="canvasWrapper">
        <span class="viewToggle">
    	  <a href="#" class="slide">slide</a>&nbsp;&nbsp;|&nbsp;&nbsp;
    	  <a href="#" class="grid">grid</a>
    	</span>
    
    	<div id="canvas" style="padding: 20px 0 20px 0; top: 15px;">

		<div id="viewport">
		  <a href="#" class="v_next">>></a>
		  <a href="#" class="v_prev"><<</a>
		  <ul id="viewWrapper">
		  	  	<img src="/includes/hex-loader.gif"/>	
		  </ul>  
		</div>
		
		<div id="canvasScrollbar" style="height: 600px; overflow-y: auto; overflow-x: hidden;">
		  <div id="collage"></div>
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
	
	var hash = window.location.hash.substring(1);// === "" ? "72157646827612988" : window.location.hash; //setId
	var oSrc, oWidth, oHeight;	
	var mSrc, mWidth, mHeight;	

	 $.getJSON('/flickr/albums?photoSetId='+hash, function(json) {
		$.each(json,function(c, coll){
		var htmlViewport = "";
		var htmlCollage = "";
		  $.each(coll, function(photoId, data) {
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
			htmlViewport += "<li class=\"lightbox\" style=\"background: url("+ mSrc +") no-repeat center center; background-size:"+ mWidth +
					"px "+ mHeight +"px; width: "+ mWidth +"px;\" csource=\""+ cSrc + "\"></li>\n";
					
			htmlCollage += "<img src=\"" + mSrc +"\"  csource=\""+ cSrc + "\" />";
			$("#viewWrapper").html(htmlViewport);
			$("#collage").append(htmlCollage);	
		  });
	    });
	  
		var center = $('#viewport ul').width()/2;    //center
		var liFirst = $('#viewport ul li:nth-child(1)').width();
		var liLast = $('#viewport ul li:last-child').width();
	
		var leftShift = center - (liFirst/2 + liLast);
		$('#viewport ul').css({ left: leftShift });
		$('#viewport ul li:first-child').fadeTo(100,1);
    	$('#viewport ul li:last-child').prependTo('#viewport ul');
    	    	
    	//$("#collage").collagePlus('targetHeight': 260);
	});   

	$("#canvas").on("click", "div > a.v_prev", function () {
	    var width1 = $('#viewport ul li:first-child').width();;
		var width2 = $('#viewport ul li:nth-child(2)').width();
		var aniWidth = (width1 + width2)/2;
		
		var aniLeft = $('#viewport ul').position().left + aniWidth - 22;    //I don't even know why
		var center = $('#viewport ul').width()/2;    //center
		var liFirst = $('#viewport ul li:first-child').width();
		var liLast = $('#viewport ul li:last-child').width();
		
		var leftShift = center - (liFirst/2 + liLast);
		$('#viewport ul li:first-child').fadeTo(200,1);
		$('#viewport ul li:nth-child(2)').fadeTo(300,.25);
        $('#viewport ul').animate({
            left: + aniLeft
        }, 200, function () {
            $('#viewport ul li:last-child').prependTo('#viewport ul');
            $('#viewport ul').css('left', leftShift);
        });
        return false;
    });

	$("#canvas").on("click", "div > a.v_next", function () {
		var width1 = $('#viewport ul li:nth-child(3)').width();;
		var width2 = $('#viewport ul li:nth-child(2)').width();
		var aniWidth = (width1 + width2)/2;
		
		var aniRight = aniWidth - $('#viewport ul').position().left;    //center
		var center = $('#viewport ul').width()/2;    //center
		var liSecond = $('#viewport ul li:nth-child(2)').width();
		var liThird = $('#viewport ul li:nth-child(3)').width();
		
		var rightShift = center - (liThird/2 + liSecond);
		$('#viewport ul li:nth-child(3)').fadeTo(200,1);
		$('#viewport ul li:nth-child(2)').fadeTo(300,.25);
		$('#viewport ul').animate({
            left: - aniRight
        }, 200, function () {
            $('#viewport ul li:first-child').appendTo('#viewport ul');
            $('#viewport ul').css('left', rightShift);
        });
        return false;
	});
	
	$("#canvas").on("click", "div > ul > li", function (event) {
		event.preventDefault();

		var lightbox = '<div id="lightbox">' +
		'<div id="content">' + 
		'<img src="' + $(this).attr( "csource" ) +'" style="height: '+viewheight/4+'px; margin-top: '+ viewheight/32 +'px;" />' + 
		'<span class="zoomed"></span></img></div>' +	
		'</div>';
		$('body', window.parent.document).append(lightbox);
	});
	
	$("#canvas").on("click", "div > div > img", function (event) {
		event.preventDefault();

		var lightbox = '<div id="lightbox">' +
		'<div id="content">' + 
		'<img src="' + $(this).attr( "csource" ) +'" style="height: '+viewheight/4+'px; margin-top: '+ viewheight/32 +'px;" />' + 
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

	$(".grid").on("click", function (event) {
		$('.grid').css('font-weight', 'bold');
		$('.slide').css('font-weight', 'normal');
		$('#viewport').css('display', 'none');
		$('#collage').fadeIn();
		$("#collage").collagePlus({'targetHeight': 300, 'direction': 'horizontal'});
		return false;
	});
	
	$(".slide").on("click", function (event) {
		$('.slide').css('font-weight', 'bold');
		$('.grid').css('font-weight', 'normal');
		$('#viewport').fadeIn();
		$('#collage').css('display', 'none');
		return false;
	});
}); 

var resizeTimer = null;
$(window).bind('resize', function() {
    $('.collage .Image_Wrapper').css("opacity", 0);
    if (resizeTimer) clearTimeout(resizeTimer);
    resizeTimer = setTimeout(collage, 200);
});

  </script>
  </body>
</html>
