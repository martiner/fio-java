package cz.geek.fio;

import org.joda.time.LocalDate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import static cz.geek.fio.FioExtractor.statementExtractor;
import static cz.geek.fio.ResourceUtils.readFromResource;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class FioExtractorTest {

    @Test
    public void shouldName() throws Exception {
        final FioExtractor<FioAccountStatement> extractor = statementExtractor(new NamespaceIgnoringJaxb2HttpMessageConverter(), new FioConversionService());
        final FioAccountStatement statement = extractor.extractData(new FakeClientHttpResponse());

        assertThat(statement, is(notNullValue()));

        assertThat(statement.getInfo(), is(notNullValue()));
        assertThat(statement.getInfo().getAccountId(), is("2111111111"));
        assertThat(statement.getInfo().getBankId(), is("2010"));

        assertThat(statement.getTransactions(), hasSize(2));
        final FioTransaction transaction = statement.getTransactions().iterator().next();
        assertThat(transaction.getIdPohybu(), is("1147301403"));
        assertThat(transaction.getDatum(), is(new LocalDate(2012, 6, 30)));
    }

    static class FakeClientHttpResponse extends AbstractClientHttpResponse {

        private final HttpHeaders httpHeaders = new HttpHeaders() {{
            add("content-type", "text/xml");
        }};

        @Override
        public int getRawStatusCode() throws IOException {
            return 200;
        }

        @Override
        public String getStatusText() throws IOException {
            return null;
        }

        @Override
        public void close() {
        }

        @Override
        public InputStream getBody() throws IOException {
            return readFromResource("/transactions-id.xml");
        }

        @Override
        public HttpHeaders getHeaders() {
            return httpHeaders;
        }
    }
}