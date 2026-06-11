package cz.geek.fio;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

class FioErrorHandler extends DefaultResponseErrorHandler {

    @Override
    protected void handleError(ClientHttpResponse response, HttpStatusCode statusCode, URI uri, HttpMethod method) throws IOException {
        switch (statusCode.value()) {
            case HttpURLConnection.HTTP_CONFLICT:
                throw new FioTooMuchRequestsException(statusCode, response.getStatusText(), getResponseBody(response), getCharset(response));
            default:
                throw new FioRestException(statusCode, response.getStatusText(), getResponseBody(response), getCharset(response));
        }
    }

}
