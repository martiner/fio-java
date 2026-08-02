package cz.geek.fio;

import org.springframework.http.HttpStatusCode;

import java.nio.charset.Charset;

public class FioErrorResponseException extends FioRestException {

    private final Integer errorCode;

    private final String status;

    private final String errorMessage;

    public FioErrorResponseException(final HttpStatusCode statusCode, final String statusText, final byte[] responseBody, final Charset responseCharset,
                                       final Integer errorCode, final String status, final String errorMessage) {
        super(statusCode.value() + " " + statusText + ": " + errorMessage + " (errorCode=" + errorCode + ", status=" + status + ")",
                statusCode, statusText, responseBody, responseCharset);
        this.errorCode = errorCode;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    /**
     * Return the Fio error code, e.g. 21, or null when the response didn't contain one.
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * Return the Fio error status, e.g. "error".
     */
    public String getStatus() {
        return status;
    }

    /**
     * Return the Fio error message, e.g. "Výpis neexistuje".
     */
    public String getErrorMessage() {
        return errorMessage;
    }

}
