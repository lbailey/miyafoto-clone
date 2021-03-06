<%@ page import="com.miyamoto.foto.service.AuthProxy" %>
<% AuthProxy.authorizeSession(request, response); %>

<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
  <head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">    
    <meta name="viewport" content="width=device-width, initial-scale=1">
<base href="">
<meta charset="utf-8" />
<title>miyamoto.foto clone</title>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>
<link rel="shortcut icon" href="includes/favicon.ico" type="image/x-icon">
<link rel="icon" href="includes/favicon.ico" type="image/x-icon">
<script type="text/javascript">
  function resizeIframe(iframe) {
  }
</script> 
</head>
  
  <body class="miyamoto logo-image">

    <div id="canvasWrapper">
    	<div id="canvas">

    		<header id="header">
    		<h2>miya<span class="add">foto</span>-clone</h2>
			  <div id="topNav" style="margin-right: 45px;"  data-content-field="navigation-navigation">
				  <nav class="main-nav">
					<ul id="siteNav">
					  <li class="toggle"><a href="#mobileNav" id="mobile-show-nav" class="icon-list"></a></li>
						<li class="page-collection"><a href="#" id="introPage">About</a></li>
						<li class="page-collection active-link"><a href="#" id="viewSection">View</a></li>
						<li class="page-collection"><a href="#" id="uploadSection">Upload</a></li>
						<li class="page-collection"><a href="" id="logoutButton">Logout</a></li>
					</ul>
				  </nav>
			</div>
			<div id="loginForm"></div>
    </header>


	<!--<img src="http://i184.photobucket.com/albums/x172/DavidAtwell/sourcegradient.png" style="right: 0px; position: absolute; z-index:101;"/>-->
	<div id="frameWrapper">
		<iframe id="left" src="viewlist.jsp" scrolling="no">
		</iframe>
	
		<iframe id="center-wide" src="rss.jsp" scrolling="no" name="viewportFrame">
		</iframe>
	</div>


        <div class="page-divider"></div>
        

    	</div><!-- / #canvas -->
    </div><!-- / #canvasWrapper -->

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="/includes/jquery.collagePlus.js"></script>
<script src="/includes/jquery.collagePlus.min.js"></script>
<script type="text/javascript">
  $(document).ready(function ($) {

    $("#introPage").on("click", function () {
		$( '#center-wide' ).attr( 'src', function ( i, val ) { return $(location).attr('protocol')+"//"+$(location).attr('host') + "/intro.jsp"; });
    });
    
    $("#uploadSection").on("click", function () {
    	window.location.href = $(location).attr('protocol')+"//"+$(location).attr('host') + "/index-upload.jsp";
    });
    
    $("#logoutButton").on("click", function() {
    	$.post( "/flickr/authorize?logout", { logout: "" } );
    	 location.reload();
    });
    

});   
</script> 
  

  </body>
</html>
