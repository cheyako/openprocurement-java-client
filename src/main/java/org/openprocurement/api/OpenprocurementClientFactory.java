package org.openprocurement.api;

import com.fasterxml.jackson.datatype.joda.JodaMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.logging.LoggingFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.URI;

public class OpenprocurementClientFactory {

    public static final URI SANDBOX_0_LATEST_URL = URI.create("https://api-sandbox.ea.openprocurement.org/api/0");
    public static final URI SANDBOX_2_3_URL = URI.create("https://api-sandbox.ea.openprocurement.org/api/dev");
    public static final URI SANDBOX_2_3_PUBLIC_URL = URI.create("http://public.api-sandbox.ea.openprocurement.org/api/dev");


    public static ClientConfig defaultLoggingConfig() {
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);
        return clientConfig;
    }

    /**
     * Creates a new client-side representation of a OpenprocurementApi.
     * <p/>
     *
     * @param rootUri           resource uri.
     * @param connectionTimeout Connect timeout interval, in milliseconds. A value of zero is equivalent to an interval of infinity.
     * @param socketTimeout     WebTarget pointing to the resource or the parent of the resource.
     * @return Instance of a class implementing the OpenprocurementApi interface.
     */
    public static OpenprocurementApi newApiProxyClient(URI rootUri, long connectionTimeout, long socketTimeout) {
        return newApiProxyClient(rootUri, defaultLoggingConfig(), connectionTimeout, socketTimeout);
    }

    public static OpenprocurementApi newApiProxyClient(URI rootUri,
                                                       ClientConfig clientConfig,
                                                       long connectionTimeout,
                                                       long socketTimeout) {
        final Client client = jerseyClient(clientConfig, connectionTimeout, socketTimeout);
        final WebTarget rootTarget = rootTarget(rootUri, client);
        return WebResourceFactory.newResource(OpenprocurementApi.class, rootTarget);
    }

    private static WebTarget rootTarget(URI rootUri, Client client) {
        final WebTarget rootTarget = client.target(rootUri);

        // joda support
        final JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(new JodaMapper());
        rootTarget.register(provider);
        return rootTarget;
    }

    private static Client jerseyClient(ClientConfig clientConfig, long connectionTimeout, long socketTimeout) {
        final Client client = ClientBuilder.newClient(clientConfig);
        client.property(ClientProperties.CONNECT_TIMEOUT, Long.toString(connectionTimeout));
        client.property(ClientProperties.READ_TIMEOUT, Long.toString(socketTimeout));
        return client;
    }

}