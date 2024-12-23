package cz.geek.fio;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.OutputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static cz.geek.fio.FioClientSettings.millisToDuration;
import static cz.geek.fio.FioExtractor.statementExtractor;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.springframework.http.HttpMethod.GET;

/**
 * Fio Bank Client
 */
@Slf4j
public class FioClient {

    private static final String ROOT = "/ib_api/rest/";
    private static final String STATEMENT_BY_ID = ROOT + "by-id/{token}/{year}/{id}/transactions.{format}";
    private static final String STATEMENT_PERIODS = ROOT + "periods/{token}/{start}/{end}/transactions.{format}";
    private static final String STATEMENT_LAST = ROOT + "last/{token}/transactions.{format}";
    private static final String LAST_DATE = ROOT + "set-last-date/{token}/{date}/";
    private static final String LAST_ID = ROOT + "set-last-id/{token}/{id}/";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String token;

    private final RestTemplate restTemplate;
    private final HttpMessageConverter<Object> jaxb2Converter;
    private final FioConversionService conversionService;

    /**
     * Constructs a new Fio Client
     * @param token API token
     */
    public FioClient(final String token) {
        this(new FioClientSettings(token));
    }

    /**
     * Constructs a new Fio Client
     * @param settings HTTP settings
     */
    public FioClient(@NonNull FioClientSettings settings) {
        this("https", "www.fio.cz", null, settings, null);
    }

    /**
     * Constructs a new Fio Client
     * @param settings HTTP settings
     * @param builder optional builder
     */
    public FioClient(@NonNull FioClientSettings settings, RestTemplateBuilder builder) {
        this("https", "www.fio.cz", null, settings, builder);
    }

    FioClient(String protocol, String host, Integer port, FioClientSettings settings, RestTemplateBuilder builder) {
        final UriComponentsBuilder base = UriComponentsBuilder.newInstance()
                .scheme(notEmpty(protocol))
                .host(notEmpty(host));
        if (port != null) {
            base.port(port);
        }
        this.token = notEmpty(settings.getToken());
        this.restTemplate = createRestTemplate(base, settings, builder);
        this.jaxb2Converter = new NamespaceIgnoringJaxb2HttpMessageConverter();
        this.conversionService = new FioConversionService();
        log.info("Fio client configured {} {}", base.toUriString(), restTemplate.getRequestFactory().getClass().getSimpleName());
    }

    private RestTemplate createRestTemplate(UriComponentsBuilder base, FioClientSettings settings, RestTemplateBuilder builder) {
        return Optional.ofNullable(builder).orElseGet(RestTemplateBuilder::new)
                .errorHandler(new FioErrorHandler())
                .connectTimeout(millisToDuration(settings.getConnectionTimeout()))
                .readTimeout(millisToDuration(settings.getSocketTimeout()))
                .rootUri(base.toUriString())
                .build();
    }

    /**
     * Get account statement within the given period
     * @param start start date
     * @param end end date
     * @return account statement
     * @throws FioException
     */
    public FioAccountStatement getStatement(final LocalDate start, final LocalDate end) {
        notNull(start);
        notNull(end);
        String from = DATE_FORMATTER.format(start);
        String to = DATE_FORMATTER.format(end);
        log.info("Getting statement from {} to {}", from, to);
        return restTemplate.execute(STATEMENT_PERIODS, GET, null,
                statementExtractor(jaxb2Converter, conversionService),
                token, from, to, ExportFormat.xml);
    }

    /**
     * Export account statement within the given period
     * @param start start date
     * @param end end date
     * @param format export format
     * @param target target stream
     * @throws FioException
     */
    public void exportStatement(final LocalDate start, final LocalDate end, final ExportFormat format, final OutputStream target) {
        notNull(start);
        notNull(end);
        notNull(format);
        String from = DATE_FORMATTER.format(start);
        String to = DATE_FORMATTER.format(end);
        log.info("Exporting statement {} from {} to {}", format, from, to);
        restTemplate.execute(STATEMENT_PERIODS, GET, null, new OutputStreamResponseExtractor(target),
                token, from, to, format);
    }

    /**
     * Get account statement with the given number within the given year
     * @param year year
     * @param id statement number
     * @return account statement
     * @throws FioException
     */
    public FioAccountStatement getStatement(final int year, final int id) {
        log.info("Getting statement for year {} id {}", year, id);
        return restTemplate.execute(STATEMENT_BY_ID, GET, null,
                statementExtractor(jaxb2Converter, conversionService),
                token, year, id, ExportFormat.xml);
    }

    /**
     * Export account statement with the given number within the given year
     * @param year year
     * @param id statement number
     * @param format export format
     * @param target target stream
     * @throws FioException
     */
    public void exportStatement(final int year, final int id, final ExportFormat format, final OutputStream target) {
        notNull(format);
        log.info("Exporting statement {} for year {} id {}", format, year, id);
        restTemplate.execute(STATEMENT_BY_ID, GET, null, new OutputStreamResponseExtractor(target),
                token, year, id, format);
    }

    /**
     * Get account statement from the last download
     * @return account statement
     * @throws FioException
     */
    public FioAccountStatement getStatement() {
        log.info("Getting statement from the last download");
        return restTemplate.execute(STATEMENT_LAST, GET, null,
                statementExtractor(jaxb2Converter, conversionService),
                token, ExportFormat.xml);
    }

    /**
     * Export account statement from the last download
     * @param format export format
     * @param target target stream
     * @throws FioException
     */
    public void exportStatement(final ExportFormat format, final OutputStream target) {
        notNull(format);
        log.info("Exporting statement {} from the last download", format);
        restTemplate.execute(STATEMENT_LAST, GET, null, new OutputStreamResponseExtractor(target),
                token, format);
    }

    /**
     * Set last downloaded statement by transaction id
     * @param id transaction id
     * @throws FioException
     */
    public void setLast(final BigInteger id) {
        notNull(id);
        setLast(id.toString());
    }

    /**
     * Set last downloaded statement by transaction id
     * @param id transaction id
     * @throws FioException
     */
    public void setLast(final String id) {
        notEmpty(id);
        log.info("Setting last download from {}", id);
        restTemplate.execute(LAST_ID, GET, null, null,
                token, id);
    }

    /**
     * Set last downloaded statement by date
     * @param date date
     * @throws FioException
     */
    public void setLast(final LocalDate date) {
        notNull(date);
        String last = DATE_FORMATTER.format(date);
        log.info("Setting last download from {}", date);
        restTemplate.execute(LAST_DATE, GET, null, null,
                token, last);
    }

}
