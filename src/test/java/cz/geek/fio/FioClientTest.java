package cz.geek.fio;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.time.LocalDate;

import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadler;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static net.jadler.Jadler.verifyThatRequest;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.startsWith;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.expectThrows;

public class FioClientTest {

    private FioClient fio;

    @BeforeMethod
    public void commonSetUp() {
        initJadler();
        fio = new FioClient("http", "localhost", port(), new FioClientSettings("token"), null);
    }

    @Test
    public void shouldExportTransactionsById() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/v1/rest/by-id/token/2016/1/transactions.xml")
                .respond()
                .withBody(ResourceUtils.readFromResource("/transactions-id.xml"))
                .withContentType("text/xml")
                .withStatus(200);

        fio.exportStatement(2016, 1, ExportFormat.xml, new ByteArrayOutputStream());
    }

    @Test
    public void shouldGetTransactionsById() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/v1/rest/by-id/token/2016/1/transactions.xml")
                .respond()
                .withBody(ResourceUtils.readFromResource("/transactions-id.xml"))
                .withContentType("text/xml")
                .withStatus(200);

        fio.getStatement(2016, 1);
    }

    @Test
    public void shouldExportTransactionsByDate() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/v1/rest/periods/token/2016-01-01/2016-01-02/transactions.xml")
                .respond()
                .withBody(ResourceUtils.readFromResource("/transactions-from-to.xml"))
                .withContentType("text/xml")
                .withStatus(200);

        fio.exportStatement(LocalDate.of(2016, 1, 1), LocalDate.of(2016, 1, 2), ExportFormat.xml, new ByteArrayOutputStream());
    }

    @Test
    public void shouldGetTransactionsByDate() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/v1/rest/periods/token/2016-01-01/2016-01-02/transactions.xml")
                .respond()
                .withBody(ResourceUtils.readFromResource("/transactions-from-to.xml"))
                .withContentType("text/xml")
                .withStatus(200);

        fio.getStatement(LocalDate.of(2016, 1, 1), LocalDate.of(2016, 1, 2));
    }

    @Test
    public void shouldExportLastTransactions() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/v1/rest/last/token/transactions.xml")
                .respond()
                .withBody(ResourceUtils.readFromResource("/transactions-last.xml"))
                .withContentType("text/xml")
                .withStatus(200);

        fio.exportStatement(ExportFormat.xml, new ByteArrayOutputStream());
    }

    @Test
    public void shouldGetLastTransactions() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/v1/rest/last/token/transactions.xml")
                .respond()
                .withBody(ResourceUtils.readFromResource("/transactions-last.xml"))
                .withContentType("text/xml")
                .withStatus(200);

        fio.getStatement();
    }

    @Test
    public void shouldSetLastId() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/v1/rest/set-last-id/token/1/")
                .respond()
                .withStatus(200);

        fio.setLast(new BigInteger("1"));
    }

    @Test
    public void shouldSetLastDate() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/v1/rest/set-last-date/token/2016-01-02/")
                .respond()
                .withStatus(200);

        fio.setLast(LocalDate.of(2016, 1, 2));
    }

    @Test
    public void shouldSendUserAgentHeader() throws Exception {
        onRequest()
                .respond()
                .withStatus(200);

        fio.setLast(LocalDate.of(2016, 1, 2));

        verifyThatRequest()
                .havingHeader("User-Agent", contains(startsWith("FioJava/")))
                .receivedOnce();
    }

    @Test(expectedExceptions = FioTooMuchRequestsException.class)
    public void shouldThrowFioTooMuchRequestsExceptionOn409() {
        onRequest().havingMethodEqualTo("GET").respond().withStatus(409);
        fio.setLast(LocalDate.of(2016, 1, 1));
    }

    @Test(expectedExceptions = FioRestException.class)
    public void shouldThrowFioRestExceptionOn500() {
        onRequest().havingMethodEqualTo("GET").respond().withStatus(500);
        fio.setLast(LocalDate.of(2016, 1, 1));
    }

    @Test
    public void shouldThrowFioErrorResponseExceptionOn500WithErrorBody() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .respond()
                .withBody(ResourceUtils.readFromResource("/error-response.xml"))
                .withContentType("text/xml; charset=UTF-8")
                .withStatus(500);

        FioErrorResponseException e = expectThrows(FioErrorResponseException.class, () -> fio.setLast(LocalDate.of(2016, 1, 1)));
        assertEquals(e.getErrorCode(), Integer.valueOf(21));
        assertEquals(e.getStatus(), "error");
        assertEquals(e.getErrorMessage(), "Výpis neexistuje");
        assertEquals(e.getMessage(), "500 Internal Server Error: Výpis neexistuje (errorCode=21, status=error)");
    }

    @Test
    public void shouldParseErrorBodyDeclaringNonUtf8Encoding() {
        onRequest()
                .havingMethodEqualTo("GET")
                .respond()
                .withBody(("<?xml version=\"1.0\" encoding=\"windows-1250\"?><response><result><errorCode>21</errorCode>"
                        + "<status>error</status><message>Výpis neexistuje</message></result></response>")
                        .getBytes(Charset.forName("windows-1250")))
                .withContentType("text/xml")
                .withStatus(500);

        FioErrorResponseException e = expectThrows(FioErrorResponseException.class, () -> fio.setLast(LocalDate.of(2016, 1, 1)));
        assertEquals(e.getErrorMessage(), "Výpis neexistuje");
    }

    @Test
    public void shouldHaveNullErrorCodeWhenMissingInErrorBody() {
        onRequest()
                .havingMethodEqualTo("GET")
                .respond()
                .withBody("<response><result><status>error</status><message>Výpis neexistuje</message></result></response>")
                .withContentType("text/xml; charset=UTF-8")
                .withStatus(500);

        FioErrorResponseException e = expectThrows(FioErrorResponseException.class, () -> fio.setLast(LocalDate.of(2016, 1, 1)));
        assertNull(e.getErrorCode());
    }

    @Test
    public void shouldThrowFioRestExceptionOn500WithUnparsableBody() {
        onRequest()
                .havingMethodEqualTo("GET")
                .respond()
                .withBody("<html><body>Internal Server Error</body></html>")
                .withContentType("text/html")
                .withStatus(500);

        assertEquals(expectThrows(FioRestException.class, () -> fio.setLast(LocalDate.of(2016, 1, 1))).getClass(), FioRestException.class);
    }

    @Test
    public void shouldThrowFioRestExceptionOn500WithDoctypeInBody() {
        onRequest()
                .havingMethodEqualTo("GET")
                .respond()
                .withBody("<!DOCTYPE response [<!ENTITY expanded \"gotcha\">]><response><result><errorCode>21</errorCode>"
                        + "<status>error</status><message>&expanded;</message></result></response>")
                .withContentType("text/xml; charset=UTF-8")
                .withStatus(500);

        assertEquals(expectThrows(FioRestException.class, () -> fio.setLast(LocalDate.of(2016, 1, 1))).getClass(), FioRestException.class);
    }

    @Test
    public void shouldThrowFioRestExceptionOn500WithEmptyResult() {
        onRequest()
                .havingMethodEqualTo("GET")
                .respond()
                .withBody("<response><result/></response>")
                .withContentType("text/xml; charset=UTF-8")
                .withStatus(500);

        assertEquals(expectThrows(FioRestException.class, () -> fio.setLast(LocalDate.of(2016, 1, 1))).getClass(), FioRestException.class);
    }

    @AfterMethod
    public void tearDown() {
        closeJadler();
    }

}
