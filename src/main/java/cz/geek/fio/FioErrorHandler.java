package cz.geek.fio;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.net.HttpURLConnection;

class FioErrorHandler extends DefaultResponseErrorHandler {

    public void handleError(final ClientHttpResponse response) throws IOException {
        final HttpStatusCode statusCode = response.getStatusCode();

        switch (statusCode.value()) {
            case HttpURLConnection.HTTP_CONFLICT:
                throw new FioTooMuchRequestsException(statusCode, response.getStatusText(), getResponseBody(response), getCharset(response));
            default:
                throw new FioRestException(statusCode, response.getStatusText(), getResponseBody(response), getCharset(response));
        }
    }

}
