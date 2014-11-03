<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
<head>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>

</head>
  
<body>
<p class="helperText">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Select an Album . . .</p>
<div id="accordian" name="photoSets">
	<ul>
		<li>
			<p><span class="icon-tasks"></span>Athletic</p>
			<ul>
				<li><a href="#" photoSetId="">Badman Events</a></li>
				<li><a href="#" photoSetId="">Badgirl Events</a></li>
			</ul>
		</li>
		<li class="active">
			<p><span class="icon-dashboard"></span>Festival</p>
			<ul>
				<li><a href="#" photoSetId="72157648684846708">Spring Fest</a></li>
				<li><a href="#" photoSetId="72157648684846708">Awa Odori</a></li>
				<li><a href="#" photoSetId="72157648684796828">Bon Odori</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-tasks"></span>Formal</p>
			<ul>
				<li><a href="#" photoSetId="">Badgirl Octoberfest Ball</a></li>
			    <li><a href="#" photoSetId="">Change of Command</a></li>
				<li><a href="#" photoSetId="">Badman Ball</a></li>
				<li><a href="#" photoSetId="">Badgirl Miss Universe Ball</a></li>
				<li><a href="#" photoSetId="">Christmas Mask at New Sanno</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-calendar"></span>Social</p>
			<ul>
				<li><a href="#" photoSetId="">OSC Treat Exchange</a></li>
				<li><a href="#" photoSetId="">Squadron Treat Drop</a></li>
				<li><a href="#" photoSetId="">MWR Trips</a></li>
				<li><a href="#" photoSetId="">All-Spouse Events</a></li>
				<li><a href="#" photoSetId="">Dinners & Drinks</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-heart"></span>Shower</p>
			<ul>
				<li><a href="#" photoSetId="">Bethany</a></li>
				<li><a href="#" photoSetId="">Kaitlin</a></li>
				<li><a href="#" photoSetId="">Teena</a></li>
				<li><a href="#" photoSetId="">Andrea</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-heart"></span>Holiday</p>
			<ul>
				<li><a href="#" photoSetId="">Feed the Sailors</a></li>
				<li><a href="#" photoSetId="">Christmas Luau</a></li>
				<li><a href="#" photoSetId="">Christmas Ugly Sweater</a></li>
				<li><a href="#" photoSetId="">Christmas Spouse Party</a></li>
				<li><a href="#" photoSetId="">Christmas Tree Decorating</a></li>
				<li><a href="#" photoSetId="">Secret Santa Reveal</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-heart"></span>Misc</p>
			<ul>
				<li><a href="#" photoSetId="">OSC Gatherings</a></li>
				<li><a href="#" photoSetId="">Hail & Farewell</a></li>
				<li><a href="#" photoSetId="">Friends & Family</a></li>
				<li><a href="#" photoSetId="">Uncategorized</a></li>
			</ul>
		</li>
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
	$("#accordian p").click(function(){
		$("#accordian ul ul").slideUp();
		if(!$(this).next().is(":visible")) {
			$(this).next().slideDown();
		}
		return false;
	});
	
	$("#accordian ul li ul li a").click(function() {
		$('.selected').removeClass('selected');
		$(this).addClass('selected');
		$('#ajax-upload').css({"display":"block"});
		photoSetId = $(this).attr('photoSetId');
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
        var folders = window.parent.document.getElementById('right');
		folders.src = folders.src;
    },
    onError: function(files,status,errMsg) {        
        $("#status").html("<font color='red;margin-left:15px;'>Upload failed</font>");
    }
}
$("#mulitplefileuploader").uploadFile(settings);
});

  </script>

  </body>
</html>
