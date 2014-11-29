package c8y.example;


import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.sdk.client.notification.SubscriberBuilder;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import com.cumulocity.sdk.client.notification.SubscriptionNameResolver;


/**
 * Variant of CepCustomNotificationsSubscriber for CEL statements.
 */
public class StreamReader implements Subscriber<String, Object> {

    public static final String CEP_STATEMENT_URL = "cep/notifications";

    private final Subscriber<String, Object> subscriber;

    public StreamReader(PlatformParameters parameters) {
        subscriber = createSubscriber(parameters);
    }

    private Subscriber<String, Object> createSubscriber(PlatformParameters parameters) {
        // @formatter:off
        return SubscriberBuilder.<String, Object>anSubscriber()
                .withParameters(parameters)
                .withEndpoint(CEP_STATEMENT_URL)
                .withSubscriptionNameResolver(new Identity())
                .withDataType(Object.class)
                .build();
        // @formatter:on
    }

    @Override
    public Subscription<String> subscribe(final String channelId,
                                          final SubscriptionListener<String, Object> handler) {
        return subscriber.subscribe(channelId, handler);
    }

    @Override
    public void disconnect() {
        subscriber.disconnect();
    }

    private static final class Identity implements SubscriptionNameResolver<String> {
        @Override
        public String apply(String id) {
            return id;
        }
    }
}
