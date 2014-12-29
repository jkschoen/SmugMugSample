package smugmug;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

/**
 *
 * @author jacobschoen
 */
public class Sample {
    
    public static String APP_NAME="TEST";
    public static String API_SECRET="<YOUR API SECRET>";
    public static String API_KEY="<YOUR API KEY>";
    
    //this is a base client that jersey will use for requests
    public static Client CLIENT = Client.create();
    
    public static void main(String[] args){
        //this is the url we want to send a request to
        WebResource resource = CLIENT.resource("https://api.smugmug.com/services/oauth/1.0a/getRequestToken");

        //since this is for a non web app we need to add a query param
        resource = resource.queryParam("oauth_callback", "oob");
        //not sure if this makes a difference or not, but I noticed this while
        //using the api browser, so figured I would add it
        resource = resource.queryParam("_accept", "application/json");
        
        //this adds a logger to log the requests and responses for debugging
        resource.addFilter(new LoggingFilter());
        
        //this sets the api key and secret
        OAuthSecrets secrets = new OAuthSecrets().consumerSecret(API_SECRET);
        OAuthParameters oauthParams = new OAuthParameters().consumerKey(API_KEY).
                signatureMethod("HMAC-SHA1").version("1.0a");
        OAuthClientFilter filter = new OAuthClientFilter(CLIENT.getProviders(), oauthParams, secrets);
        resource.addFilter(filter);
        
        WebResource.Builder builder = resource.getRequestBuilder();
        //tell it we want json back
        builder = builder.accept("application/json");
        //set the User agent to the app name
        builder = builder.header("User-Agent", APP_NAME);
        
        //get the response back as a string
        String response = builder.get(String.class); 
    }
    
}
