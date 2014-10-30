<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
  <head>
<script type="text/javascript" src="//use.typekit.net/ik/u4kyPVoC1DK1XbInUOo0gs6KZw0qu2IbnLULsowbjg9feC9gfFHN4UJLFRbh52jhWD9k5eIhZ2JaZQsKw2jhZR4yjc9hw2yKFUT1iaiaO1m0-WZ8S1Fyde8DjABROcFzdPUudc8hZW4DjAUTShByjkoRdhXC-WFyjAwldc8R-eNCSkoDSWmyScmDSeBRZPoRdhXCHKoTShByjku0-AFGdhUDO1FUiABkZWF3jAF8OcFzdPJ4Z1mXiW4yOWgXH6qJyB9bMg6IJMJ7fbKfmsMMegI6MKG4fJBmIMMjgkMfH6qJym9bMg65JMJ.js"></script>
<script type="text/javascript">try{Typekit.load();}catch(e){}</script>
<link rel="stylesheet" type="text/css" href="//fonts.googleapis.com/css?family=Actor:normal|Open+Sans:300,300italic,400,400italic,600,600italic,700,700italic,800,800italic"/>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>

</head>
  <body style="padding: 28px 0 0 0;">
    <div id="canvasWrapper">
    	<div id="canvas">

		<div id="viewport">
		  <a href="#" class="v_next">>></a>
		  <a href="#" class="v_prev"><<</a>
		  <ul id="viewWrapper">
		  </ul>  
		</div>

    	</div><!-- / #canvas -->
    </div><!-- / #canvasWrapper -->

 

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script type="text/javascript">
  //document.domain = "miyafoto";  
$(document).ready(function ($) {
	
	var innerWidth = $("#canvas").innerWidth();
		properScale = innerWidth/4;
		
	var viewheight = $('body', window.parent.document).height();
	
	var hash = window.location.hash.substring(1);// === "" ? "72157646827612988" : window.location.hash; //setId
	var oSrc, oWidth, oHeight;	
	var mSrc, mWidth, mHeight;	

	 $.getJSON('/flickr/albums', function(json) {
		$.each(json,function(c, coll){
		  $.each(coll, function(i, value) {
		    if (hash === value.setId) {
			var html = "";
			$.each(value.photos,function(photoId, data){	
				$.each(data,function(size, crops){
					if (size == 'Original') {
						oSrc = crops.source;
						oWidth = crops.width;
						oHeight = crops.height;
					}
					if (size === 'Medium') {
						mSrc = crops.source;
						mWidth = crops.width;
						mHeight = crops.height;
					}
				});
				html += "<li class=\"lightbox\" style=\"background: url("+ mSrc +") no-repeat center center; background-size:"+ mWidth +
						"px "+ mHeight +"px; width: "+ mWidth +"px;\" origsource=\""+ oSrc + "\" origwidth=\""+ oWidth + 
						"\" origheight=\""+ oHeight + "\"></li>\n";
			});
			$("#viewWrapper").append(html);
			}
		});
	  });
	  
		var center = $('#viewport ul').width()/2;    //center
		var liFirst = $('#viewport ul li:nth-child(1)').width();
		var liLast = $('#viewport ul li:last-child').width();
	
		var leftShift = center - (liFirst/2 + liLast);
		$('#viewport ul').css({ left: leftShift });
		$('#viewport ul li:first-child').fadeTo(100,1);
    	$('#viewport ul li:last-child').prependTo('#viewport ul');
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
		console.log("allo");
		event.preventDefault();

		var lightbox = '<div id="lightbox">' +
		'<div id="content">' + 
		'<img src="' + $(this).attr( "origsource" ) +'" style="height: '+viewheight/4+'px; margin-top: '+ viewheight/32 +'px;" />' + 
		'<span class="zoomed"></span></img></div>' +	
		'</div>';
		$('body', window.parent.document).append(lightbox);
	});
	
	$('body', window.parent.document).on("click", function (event) {
		$('body', window.parent.document).on("click", "div#lightbox", function() {
			$(this).hide();
		});
	});

}); 

  </script>
  </body>
</html>
