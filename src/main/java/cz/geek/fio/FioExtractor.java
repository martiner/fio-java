package cz.geek.fio;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;

import static org.apache.commons.lang3.Validate.notNull;

class FioExtractor<T> implements ResponseExtractor<T> {

    private final Class<T> targetClass;
    private final Class<?> sourceClass;
    private final HttpMessageConverter<Object> converter;
    private final FioConversionService conversionService;

    public FioExtractor(final Class<T> targetClass, final Class<?> sourceClass,
                        final HttpMessageConverter<Object> converter, final FioConversionService conversionService) {
        this.targetClass = notNull(targetClass);
        this.sourceClass = notNull(sourceClass);
        this.converter = notNull(converter);
        this.conversionService = notNull(conversionService);
        conversionService.validate(sourceClass, targetClass);
    }

    @Override
    public T extractData(final ClientHttpResponse response) throws IOException {
        final Object source = converter.read(sourceClass, response);
        return conversionService.convert(source, targetClass);
    }

    public static FioExtractor<FioAccountStatement> statementExtractor(
            final HttpMessageConverter<Object> jaxb2Converter, final FioConversionService conversionService) {
        return new FioExtractor<>(FioAccountStatement.class, AccountStatement.class, jaxb2Converter, conversionService);
    }
}
