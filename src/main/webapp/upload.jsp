<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
<head>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>

</head>
  
<body>
<p class="helperText pad30">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Select an Album . . .</p>
<div id="accordian" name="photoSets">
	<ul>
		<li>
			<p><span class="icon-tasks"></span>Athletic</p>
			<ul>
				<li><a href="#" photoSetId="72157648683693719">Badman Events</a></li>
				<li><a href="#" photoSetId="72157646772374793">Badgirl Events</a></li>
			</ul>
		</li>
		<li class="active">
			<p><span class="icon-dashboard"></span>Festival</p>
			<ul>
				<li><a href="#" photoSetId="72157648683096147">Spring Fest</a></li>
				<li><a href="#" photoSetId="72157648684846708">Awa Odori</a></li>
				<li><a href="#" photoSetId="72157648684796828">Bon Odori</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-tasks"></span>Formal</p>
			<ul>
				<li><a href="#" photoSetId="72157648683737159">Badgirl Octoberfest Ball</a></li>
			    <li><a href="#" photoSetId="72157649102848015">Change of Command</a></li>
				<li><a href="#" photoSetId="72157649091148702">Badman Ball</a></li>
				<li><a href="#" photoSetId="72157648683131097">Badgirl Miss Universe Ball</a></li>
				<li><a href="#" photoSetId="72157648692195938">Christmas Mask at New Sanno</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-calendar"></span>Social</p>
			<ul>
				<li><a href="#" photoSetId="72157648690945630">OSC Treat Exchange</a></li>
				<li><a href="#" photoSetId="72157649090185101">Squadron Treat Drop</a></li>
				<li><a href="#" photoSetId="72157649036603486">MWR Trips</a></li>
				<li><a href="#" photoSetId="72157649090206761">All-Spouse Events</a></li>
				<li><a href="#" photoSetId="72157649036628026">Dinners & Drinks</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-heart"></span>Shower</p>
			<ul>
				<li><a href="#" photoSetId="72157649102927455">Bethany</a></li>
				<li><a href="#" photoSetId="72157649090230061">Kaitlin</a></li>
				<li><a href="#" photoSetId="72157648692319738">Teena</a></li>
				<li><a href="#" photoSetId="72157648692265598">Andrea</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-heart"></span>Holiday</p>
			<ul>
				<li><a href="#" photoSetId="72157648683909119">Feed the Sailors</a></li>
				<li><a href="#" photoSetId="72157648692349318">Christmas Luau</a></li>
				<li><a href="#" photoSetId="72157648683936719">Christmas Ugly Sweater</a></li>
				<li><a href="#" photoSetId="72157646772620263">Christmas Spouse Party</a></li>
				<li><a href="#" photoSetId="72157649091340772">Christmas Tree Decorating</a></li>
				<li><a href="#" photoSetId="72157648683354757">Secret Santa Reveal</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-heart"></span>Misc</p>
			<ul>
				<li><a href="#" photoSetId="72157648692389178">OSC Gatherings</a></li>
				<li><a href="#" photoSetId="72157648692394138">Hail & Farewell</a></li>
				<li><a href="#" photoSetId="72157649090368331">Friends & Family</a></li>
				<li><a href="#" photoSetId="72157649090373231">Uncategorized</a></li>
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
        
        //var folders = window.parent.document.getElementById('right');
		//folders.src = folders.src;
		var folders = window.parent.document.getElementById('right');
		folders.src = "http://"+$(location).attr('host') + "/folders.jsp#"+photoSetId;
		console.log(folders.src);
		folders.contentWindow.location.reload();
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
