package cz.geek.fio;

import org.springframework.http.HttpStatusCode;

import java.nio.charset.Charset;

/**
 * @deprecated use {@link FioTooManyRequestsException}
 */
@Deprecated
public class FioTooMuchRequestsException extends FioTooManyRequestsException {

    public FioTooMuchRequestsException(final HttpStatusCode statusCode, final String statusText, final byte[] responseBody, final Charset responseCharset) {
        super(statusCode, statusText, responseBody, responseCharset);
    }
}
