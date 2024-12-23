package cz.geek.fio;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.time.LocalDate;

import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadler;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;

public class FioClientIT {

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
                .havingPathEqualTo("/ib_api/rest/by-id/token/2016/1/transactions.xml")
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
                .havingPathEqualTo("/ib_api/rest/by-id/token/2016/1/transactions.xml")
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
                .havingPathEqualTo("/ib_api/rest/periods/token/2016-01-01/2016-01-02/transactions.xml")
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
                .havingPathEqualTo("/ib_api/rest/periods/token/2016-01-01/2016-01-02/transactions.xml")
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
                .havingPathEqualTo("/ib_api/rest/last/token/transactions.xml")
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
                .havingPathEqualTo("/ib_api/rest/last/token/transactions.xml")
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
                .havingPathEqualTo("/ib_api/rest/set-last-id/token/1/")
                .respond()
                .withStatus(200);

        fio.setLast(new BigInteger("1"));
    }

    @Test
    public void shouldSetLastDate() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/ib_api/rest/set-last-date/token/2016-01-02/")
                .respond()
                .withStatus(200);

        fio.setLast(LocalDate.of(2016, 1, 2));
    }

    @AfterMethod
    public void tearDown() {
        closeJadler();
    }

}
