<%@ page import="com.miyamoto.foto.service.AuthProxy" %>
<%
if(AuthProxy.isAuthorized(request)) {
   response.sendRedirect("index-view.jsp");
}
%>

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
  //document.domain = "miyafoto";
  function resizeIframe(iframe) {
   // iframe.height = iframe.contentWindow.document.body.scrollHeight + "px";
   // alert(iframe.contentWindow.document.body.scrollHeight + 'px');
  }
</script> 
</head>

<body>
<div class="login-body"></div>
<section class="login-wrapper">
  <div class="form">
  <h2>miya<span class="add">foto</span>-clone</h2>
    <form id="logIn" class="login-form" action="/flickr/authorize">
      <input id="userId" name="userId" type="text" placeholder="username"/>
      <input id="userPass" name="userPass" type="password" placeholder="password"/>
      <input class="button" type="submit" value="login" />
    </form>
  </div>
</section>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script>

$(document).ready(function ($) {
  	$.getJSON('/flickr/albums?invalidateAlbum=', function(json) {});
});

$("#logIn").submit(function(event) {

  event.preventDefault(); 
  
  var $form = $( this ),
    user = $form.find( "input[name='userId']" ).val(),
    pass = $form.find( "input[name='userPass']" ).val(),
    url = $form.attr( "action" );
    
 
  // Send the data using post
  var posting = $.post( url, { userId: user, userPass: pass } );
  posting.done(function( data ) {
    //go to view
    location.reload();
  });
});
</script>




  </body>
</html>
