package cz.geek.fio;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;
import java.io.OutputStream;

import static org.apache.commons.lang3.Validate.notNull;

class OutputStreamResponseExtractor implements ResponseExtractor<Integer> {

    private final OutputStream output;

    public OutputStreamResponseExtractor(final OutputStream output) {
        this.output = notNull(output);
    }

    @Override
    public Integer extractData(ClientHttpResponse response) throws IOException {
        return FileCopyUtils.copy(response.getBody(), output);
    }
}