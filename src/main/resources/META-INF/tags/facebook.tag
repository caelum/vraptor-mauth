<div id="fb-root"></div>
<script>
  window.fbAsyncInit = function() {
    FB.init({
      appId      : '202401153113037',
      status     : true // check login status
    });
  };

  // Load the SDK Asynchronously
  (function(d){
     var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
     if (d.getElementById(id)) {return;}
     js = d.createElement('script'); js.id = id; js.async = true;
     js.src = "//connect.facebook.net/en_US/all.js";
     ref.parentNode.insertBefore(js, ref);
   }(document));
</script>
<script>
function MauthFacebook(cb) {
	 FB.login(function(response) {
		   if (response.authResponse) {
		     FB.api('/me', cb);
		   } else {
		     console.log('User cancelled login or did not fully authorize.');
		   }
		 }, {scope: "user_education_history,offline_access,user_likes,user_location,user_hometown,user_website,publish_stream"});
}
</script>
