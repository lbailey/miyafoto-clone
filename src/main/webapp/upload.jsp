<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
<head>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>

</head>
  
<body>
<p class="helperText">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Select an Album . . .</p>
<div id="accordian" name="photoSets">
	<ul>
		<li class="active">
			<p><span class="icon-dashboard"></span>Culture</p>
			<ul>
				<li><a href="#" photoSetId="">Awaodori</a></li>
				<li><a href="#" photoSetId="72157647007092415">Bonodori</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-tasks"></span>Formal</p>
			<ul>
				<li><a href="#" photoSetId="">April Badman Ball</a></li>
				<li><a href="#" photoSetId="">November Badman Ball</a></li>
				<li><a href="#" photoSetId="">Christmas Party</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-calendar"></span>Social</p>
			<ul>
				<li><a href="#" photoSetId="">Ziplining</a></li>
				<li><a href="#" photoSetId="">Pool Party</a></li>
				<li><a href="#" photoSetId="">Lunches</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-heart"></span>Shower</p>
			<ul>
				<li><a href="#" photoSetId="">Katelyn</a></li>
				<li><a href="#" photoSetId="">Teena</a></li>
			</ul>
		</li>
		<li>
			<p><span class="icon-heart"></span>Misc</p>
			<ul>
				<li><a href="#" photoSetId="">Misc</a></li>
			</ul>
		</li>
	</ul>
</div>

<div id="mulitplefileuploader"></div>
<div id="status"></div>
          </p>
              
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
		$('#ajax-upload').css({"visibility":"visible"});
		photoSetId = $(this).attr('photoSetId');
		return false;
	});
	
	var settings = {
    url: "/flickr/upload",
    method: "POST",
    allowedTypes:"jpg,jpeg,png,gif",
    multiple: true,
    onSuccess:function(files,responseData,xhr) {
        $.post( "/flickr/albums", { photoSetId: photoSetId, photoId: responseData.trim() } );
    },
    afterUploadAll:function() {
        $("#status").html("<div style='color:green;margin-left:15px;'>Upload successful</div>");
		$('#ajax-upload').parent().find("input[type=file]").val("");
		$('#ajax-upload').parent().show();
        //$('#right').contentWindow.location.reload(true);
        var folders = window.parent.document.getElementById('right');
		folders.src = folders.src;
    },
    onError: function(files,status,errMsg) {        
        $("#status").html("<font color='red'>Upload failed</font>");
    }
}
$("#mulitplefileuploader").uploadFile(settings);
});

  </script>

  </body>
</html>
