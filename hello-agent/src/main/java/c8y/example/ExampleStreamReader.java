package c8y.example;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.ClientConfiguration;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class ExampleStreamReader {

    private static final Logger log = LoggerFactory.getLogger(ExampleStreamReader.class);

    public static void main(String[] args) throws InterruptedException, IOException {

        final Gson gson = new Gson();
        Properties prop = new Properties();

        String filename = "dc.properties";
        InputStream input = ExampleStreamReader.class.getClassLoader().getResourceAsStream(filename);
        prop.load(input);

        CumulocityCredentials credentials = new CumulocityCredentials(prop.getProperty("tenant"), prop.getProperty("user"), prop.getProperty("password"), prop.getProperty("apikey"));
        PlatformParameters pp = new PlatformParameters(prop.getProperty("host"), credentials, new ClientConfiguration());


        final StreamReader sr = new StreamReader(pp);
        sr.subscribe("/PB_MeasurementStream/measurementstream", new SubscriptionListener<String, Object>() {
            @Override
            public void onNotification(Subscription<String> stringSubscription, Object o) {
                HashMap<String, Object> measurementInfo = (HashMap) o;
                if (measurementInfo.containsKey("raw")) {
                    String measurement = gson.toJson(measurementInfo.get("raw"));
                    log.info(measurement);
                }


            }

            @Override
            public void onError(Subscription<String> stringSubscription, Throwable throwable) {
                log.error("Got error in listener", throwable);
            }
        });


        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Disconnecting");
                sr.disconnect();
            }
        });


        while (true) {
            Thread.sleep(1000);
        }

    }

}
