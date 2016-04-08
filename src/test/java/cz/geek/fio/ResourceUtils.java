package cz.geek.fio;

import java.io.InputStream;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

public class ResourceUtils {

    public static InputStream readFromResource(String resourcePath) {
        final Class<?> clazz = FioClientIT.class;
        return readFromResource(resourcePath, clazz);
    }

    private static InputStream readFromResource(String resourcePath, Class<?> testClass) {
        notEmpty(resourcePath, "resourcePath");
        notNull(testClass, "testClass cannot be null!");
        return testClass.getResourceAsStream(resourcePath);
    }
}
