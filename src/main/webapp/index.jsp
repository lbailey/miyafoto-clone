<!doctype html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml" itemscope itemtype="http://schema.org/Thing" lang="en-US">
  <head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">    
    <meta name="viewport" content="width=device-width, initial-scale=1">
<base href="">
<meta charset="utf-8" />
<title>miyamoto.foto</title>
<link rel="stylesheet" type="text/css" href="/includes/site.css"/>
<link rel="shortcut icon" href="includes/favicon.ico" type="image/x-icon">
<link rel="icon" href="includes/favicon.ico" type="image/x-icon">
<script type="text/javascript">
  //document.domain = "miyafoto";
  function resizeIframe(iframe) {
   // iframe.height = iframe.contentWindow.document.body.scrollHeight + "px";
   // alert(iframe.contentWindow.document.body.scrollHeight + 'px');
  }
</script> 
</head>
  
  <body class="miyamoto logo-image">

    <div id="canvasWrapper">
    	<div id="canvas">

    		<header id="header">
    		<h2>miya<span class="add">moto</span>-foto</h2>
			  <div id="topNav"  data-content-field="navigation-navigation">
				  <nav class="main-nav">
					<ul id="siteNav">
					  <li class="toggle"><a href="#mobileNav" id="mobile-show-nav" class="icon-list"></a></li>
						<li class="page-collection"><a href="#" id="introPage">About</a></li>
						<li class="page-collection"><a href="#">Templating</a></li>
						<li class="page-collection active-link"><a href="#">Login</a></li>
					</ul>
				  </nav>
			</div>
    </header>


	<!--<img src="http://i184.photobucket.com/albums/x172/DavidAtwell/sourcegradient.png" style="right: 0px; position: absolute; z-index:101;"/>-->
	<div id="frameWrapper">
		<iframe id="left" src="upload.jsp" scrolling="no">
		</iframe>
	
		<iframe id="center" src="intro.jsp" scrolling="yes" name="viewportFrame">
		</iframe>
	
	
		<iframe id="right" src="folders.jsp" onload="resizeIframe(this)" scrolling="no" name="fileFrame">
		</iframe> 
	</div>


        <div class="page-divider"></div>
        

    	</div><!-- / #canvas -->
    </div><!-- / #canvasWrapper -->

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script type="text/javascript">
//  document.domain = "miyafoto";
  $(document).ready(function ($) {

    $("#introPage").on("click", function () {
		$( '#center' ).attr( 'src', function ( i, val ) { return "http://"+$(location).attr('host') + "/intro.jsp"; });
    });

});   
</script> 
  

  </body>
</html>
