package smugmug;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

/**
 *
 * @author jacobschoen
 */
public class Sample {
    
    public static String APP_NAME="TEST";
    public static String API_SECRET="<Your Secret>";
    public static String API_KEY="<Your Key>";
    
    public static final String OAUTH_ORIGIN = "https://secure.smugmug.com";
    public static final String REQUEST_TOKEN_URL = OAUTH_ORIGIN + "/services/oauth/1.0a/getRequestToken?oauth_callback=oob";
    public static final String ACCESS_TOKEN_URL = OAUTH_ORIGIN + "/services/oauth/1.0a/getAccessToken";
    public static final String AUTHORIZE_URL = OAUTH_ORIGIN + "/services/oauth/1.0a/authorize";
    
    
    public static void main(String[] args) throws IOException, URISyntaxException{
        ConsumerCredentials consumerCredentials = new ConsumerCredentials(API_KEY, API_SECRET);
        OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport.builder(consumerCredentials)
            .authorizationFlow(
                REQUEST_TOKEN_URL,
                ACCESS_TOKEN_URL,
                AUTHORIZE_URL)
            .build();
        
        String authorizationUri = authFlow.start();
        Desktop.getDesktop().browse(new URI(authorizationUri));
        System.out.println("Once you authenticated with SmugMug and granted permissions to this app, press Enter to continue.");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String code = in.readLine();
        
        AccessToken accessToken = authFlow.finish(code);
        System.out.println(accessToken);
        System.out.println("Below you will find the 2 peices of information you need to store, OAUTH_TOKEN_ID and OAUTH_TOKEN_SECRET:");
        System.out.println("OAUTH_TOKEN_ID     => '" + accessToken.getToken() + "'");
        System.out.println("OAUTH_TOKEN_SECRET => '" + accessToken.getAccessTokenSecret() + "'");
        
        //now that we have the token we need to build the client
        Feature filterFeature = OAuth1ClientSupport.builder(consumerCredentials)
            .feature()
            .accessToken(accessToken)
            .build();
        Client client = ClientBuilder.newBuilder()
            .register(filterFeature)
            .build();
        //now any requests made using this client will already have the oauth details
        WebTarget target = client.target("https://api.smugmug.com/").path("/api/v2!authuser");
        String result = target.request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        System.out.println(result);
    }
    
}
