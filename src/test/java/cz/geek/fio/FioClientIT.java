package cz.geek.fio;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

public class FioClientIT {

    private static final String ENV_VARIABLE = "TOKEN";

    private FioClient fio;

    @BeforeMethod
    public void setUp() {
        String token = System.getenv().get(ENV_VARIABLE);
        requireNonNull(token, "Please set " + ENV_VARIABLE + " environment variable");
        fio = new FioClient(token);
    }

    @Test
    public void testName() {
        LocalDate now = LocalDate.now();
        fio.getStatement(now.minusMonths(2), now);
    }
}
