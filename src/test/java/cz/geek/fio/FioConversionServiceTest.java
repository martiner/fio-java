package cz.geek.fio;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class FioConversionServiceTest {

    private final FioConversionService service = new FioConversionService();

    @Test
    public void shouldValidate() throws Exception {
        service.validate(AccountStatement.class, FioAccountStatement.class);
    }

    @Test
    public void shouldConvert() throws Exception {
        final Info info = new Info();
        info.setAccountId("account");
        info.setBankId("bank");

        final AccountStatement source = new AccountStatement();
        source.setInfo(info);

        final FioAccountStatement target = service.convert(source, FioAccountStatement.class);

        assertThat(target, is(notNullValue()));

        assertThat(target.getInfo(), is(notNullValue()));
        assertThat(target.getInfo().getAccountId(), is(info.getAccountId()));
        assertThat(target.getInfo().getBankId(), is(info.getBankId()));
    }
}