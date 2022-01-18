package io.smooch.demoapp;

import android.app.Application;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.smooch.core.InitializationStatus;
import io.smooch.core.Settings;
import io.smooch.core.Smooch;
import io.smooch.core.SmoochCallback;
import io.smooch.core.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import static io.jsonwebtoken.JwsHeader.KEY_ID;

import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.Support;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Below is where you would put your app's android-sdk integrationId to initialize the Smooch class;
        // Find it on the Sunshine Conversations dashboard, or via API: https://docs.smooch.io/rest/#get-sdk-ids
        Smooch.init(this, new Settings("61df4d7377cda500eb6cc50f"), new SmoochCallback<InitializationStatus>() {
            @Override
            public void run(Response response) {
                // Handle init response here!
                String jwt = Jwts.builder()
                        .claim("scope", "appUser")
                        .claim("userId", "12345")
                        .setHeaderParam(KEY_ID, "app_61df5492906c2300ec3492be")
                        .signWith(
                                Keys.hmacShaKeyFor("Y7hizIdIfFGzkY5mNq-ZGBDfbEuw9VVcd7xYepnKi33gKiZA04Hos3rkfInxXWZeKbtqe1CwNNMk9DiwI9GzeQ".getBytes()),
                                SignatureAlgorithm.HS256
                        )
                        .compact();

                Smooch.login("12345", jwt, new SmoochCallback() {
                    @Override
                    public void run(Response response) {
                        if (response.getError() == null) {
                            // Your code after login is complete
                            String teste = "3123312";
                        } else {
                            // Something went wrong during login. Your JWT might be invalid
                        }
                    }
                });
            }
        });

        addSomeProperties(User.getCurrentUser());

        String zendeskUrl = "https://z3nprototiponext.zendesk.com";
        String appId = "23ef23e692b7e32adb765adaae1464a4adbe90dc15c46615";
        String clientId = "mobile_sdk_client_4fbf5f6c94c6129343b4";

        Zendesk.INSTANCE.init(this, zendeskUrl, appId, clientId);
        Support.INSTANCE.init(Zendesk.INSTANCE);

        Identity identity = new AnonymousIdentity.Builder()
                .withNameIdentifier("Test")
                .withEmailIdentifier("test@test.com").build();

        Zendesk.INSTANCE.setIdentity(identity);

    }

    private void addSomeProperties(final User user) {
        final Map<String, Object> customProperties = new HashMap<>();

        // Identify user with default properties
        user.setFirstName("Demo");
        user.setLastName("App");
        user.setEmail("demo.app@smooch.io");
        user.setSignedUpAt(new Date(1420070400000L));

        // Add your own custom properties
        customProperties.put("Last Seen", new Date());
        customProperties.put("Awesome", true);
        customProperties.put("Karma", 1337);
        user.addMetadata(customProperties);
    }
}
