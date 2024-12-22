package cz.geek.fio;

import org.springframework.http.HttpStatusCode;

import java.nio.charset.Charset;

public class FioRestException extends FioException {

    private final int statusCode;

    private final String statusText;

    private final byte[] responseBody;

    private final Charset responseCharset;

    public FioRestException(final HttpStatusCode statusCode, final String statusText, final byte[] responseBody, final Charset responseCharset) {
        super(statusCode.value() + " " + statusText);
        this.statusCode = statusCode.value();
        this.statusText = statusText;
        this.responseBody = responseBody;
        this.responseCharset = responseCharset;
    }

    /**
     * Return the raw HTTP status code value.
     */
    public int getRawStatusCode() {
        return this.statusCode;
    }

    /**
     * Return the HTTP status text.
     */
    public String getStatusText() {
        return this.statusText;
    }

    /**
     * Return the response body as a byte array.
     */
    public byte[] getResponseBodyAsByteArray() {
        return this.responseBody;
    }

    /**
     * Return the response body as a string.
     */
    public String getResponseBodyAsString() {
        return new String(this.responseBody, this.responseCharset);
    }

}
