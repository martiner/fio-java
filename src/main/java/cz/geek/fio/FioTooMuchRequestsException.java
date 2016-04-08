package cz.geek.fio;

import java.nio.charset.Charset;

public class FioTooMuchRequestsException extends FioRestException {

    public FioTooMuchRequestsException(final int statusCode, final String statusText, final byte[] responseBody, final Charset responseCharset) {
        super(statusCode, statusText, responseBody, responseCharset);
    }
}
