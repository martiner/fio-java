package cz.geek.fio;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.springframework.util.Assert.isTrue;

/**
 * Gather various additional settings of {@link FioClient}. Can be passed to the {@link FioClient} constructor to tune up
 * its behaviour.
 * <p>
 * Settings are applied only once at the beginning. Changing this bean after it's passed to {@link FioClient} has
 * no effect.
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "fio.client")
public class FioClientSettings {

    private String token;
    private int maxConnections = 20;
    private int connectionTimeout = secondsToMillis(10);
    private int socketTimeout = secondsToMillis(60);

    public FioClientSettings(String token) {
        this.token = notEmpty(token);
    }

    /**
     * API token
     * @return API token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets API token
     * @param token API token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Set maximum number of connections used. This applies same for connections per host as for total connections.
     * (As we assume FioClient connects to single host).
     * <p>
     * The default value is 20.
     *
     * @param maxConnections maximum number of connections used.
     */
    public void setMaxConnections(int maxConnections) {
        isTrue(maxConnections > 0, "maxConnections must be greater than zero");
        this.maxConnections = maxConnections;
    }

    /**
     * Maximum number of connection used
     *
     * @return maximum number of connection used
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * Set timeout milliseconds until connection established.
     * <p>
     * The default value is 10 seconds (10000 ms).
     * <p>
     * Set to 0 for infinite.
     *
     * @param connectionTimeout connection timeout milliseconds
     */
    public void setConnectionTimeout(int connectionTimeout) {
        isTrue(connectionTimeout >= 0, "connectionTimeout must be not negative");
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Set timeout seconds until connection established.
     * <p>
     * The default value is 10 seconds.
     * <p>
     * Set to 0 for infinite.
     *
     * @param connectionTimeout connection timeout seconds
     */
    public void setConnectionTimeoutSeconds(int connectionTimeout) {
        setConnectionTimeout(secondsToMillis(connectionTimeout));
    }

    /**
     * Milliseconds until connection established.
     *
     * @return milliseconds until connection established
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Set socket timeout (maximum period inactivity between two consecutive data packets) milliseconds.
     * <p>
     * The default value is 60 seconds (60000 ms).
     * <p>
     * Set to 0 for infinite.
     *
     * @param socketTimeout socket timeout milliseconds
     */
    public void setSocketTimeout(int socketTimeout) {
        isTrue(socketTimeout >= 0, "socketTimeout must be not negative");
        this.socketTimeout = socketTimeout;
    }

    /**
     * Set socket timeout (maximum period inactivity between two consecutive data packets) seconds.
     * <p>
     * The default value is 60 seconds.
     * <p>
     * Set to 0 for infinite.
     *
     * @param socketTimeout socket timeout seconds
     */
    public void setSocketTimeoutSeconds(int socketTimeout) {
        setSocketTimeout(secondsToMillis(socketTimeout));
    }

    /**
     * Milliseconds for inactivity between two consecutive data packets.
     *
     * @return milliseconds for inactivity between two consecutive data packets
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    private static int secondsToMillis(int seconds) {
        return (int) TimeUnit.SECONDS.toMillis(seconds);
    }

    static Duration millisToDuration(int millis) {
        return Duration.ofMillis(millis);
    }
}
