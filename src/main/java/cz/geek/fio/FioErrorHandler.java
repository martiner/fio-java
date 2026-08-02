package cz.geek.fio;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.Charset;

class FioErrorHandler extends DefaultResponseErrorHandler {

    private static final JAXBContext ERROR_RESPONSE_CONTEXT;

    static {
        try {
            ERROR_RESPONSE_CONTEXT = JAXBContext.newInstance(FioErrorResponse.class);
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    protected void handleError(ClientHttpResponse response, HttpStatusCode statusCode, URI uri, HttpMethod method) throws IOException {
        switch (statusCode.value()) {
            case HttpURLConnection.HTTP_CONFLICT:
                throw new FioTooMuchRequestsException(statusCode, response.getStatusText(), getResponseBody(response), getCharset(response));
            default:
                final byte[] body = getResponseBody(response);
                final Charset charset = getCharset(response);
                final FioErrorResponse.Result result = tryParseErrorResponse(body);
                if (result != null && result.getStatus() != null && result.getMessage() != null) {
                    throw new FioErrorResponseException(statusCode, response.getStatusText(), body, charset,
                            result.getErrorCode(), result.getStatus(), result.getMessage());
                }
                throw new FioRestException(statusCode, response.getStatusText(), body, charset);
        }
    }

    private static FioErrorResponse.Result tryParseErrorResponse(byte[] body) {
        if (body == null || body.length == 0) {
            return null;
        }
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            final XMLReader reader = factory.newSAXParser().getXMLReader();
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            // the byte stream lets the parser honour the encoding declared in the prolog
            final SAXSource source = new SAXSource(reader, new InputSource(new ByteArrayInputStream(body)));
            return ((FioErrorResponse) ERROR_RESPONSE_CONTEXT.createUnmarshaller().unmarshal(source)).getResult();
        } catch (Exception e) {
            // response body is not a structured Fio error, fall back to a plain FioRestException
            return null;
        }
    }

}
