<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
<head>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>

</head>
  
<body>
<p class="helperText pad30 pad27">Viewing Albums</p>
<div id="albumYearSelect"><span class="helperText pad27">Year: </span></div>
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
		   console.log(year + " size: " + Object.keys(coll).length  + " " + Object.keys(coll) + $.isEmptyObject(coll));
			$.each(coll, function(photoType, photoSet) {
			  var html = "<li><p>" + photoType + "</p><ul>";      
			  $.each(photoSet, function(i, album) {   
				html += "<li><a href=\"#\" photoSetId=\""+album.setId+"\">"+album.setName+"</a></li>";             
			  });
		 
			  html += "</ul></li>";
		
			  $("#accordian > ul").append(html);  			   
			  //$("#accordian > ul > li:nth-child(2) > p").next().slideDown();		 
			});
			
			if (Object.keys(coll).length < 6) {
		      difference = $.grep(typeSet,function(x) {return $.inArray(x, Object.keys(coll)) < 0});
		    } 
			console.log(difference);
		   
		   //if difference has stuff
		   if (!$.isEmptyObject(difference)) {
		     $.each(difference, function(missIn, missCat) {
		   		var emptyAddHtml = "<li><p>" + missCat + "</p><ul>";
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
    
    
	$("#accordian").on("click", "ul > li > ul > li > a", function () {
		photoSetId = "";
		$('.selected').removeClass('selected');				
		$(this).addClass('selected');
		photoSetId = $(this).attr('photoSetId');
		return false;     
    });
    
    $("#accordian").on("click", "ul > li > ul > li > a", function () {
        var viewport = window.parent.document.getElementById('center-wide');
		viewport.src = "http://"+$(location).attr('host') + "/viewport.jsp#"+photoSetId;
		console.log(viewport.src);
		viewport.contentWindow.location.reload();
		viewport.src = viewport.src;
    });
        	
});

  </script>

  </body>
</html>
