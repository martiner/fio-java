package cz.geek.fio;

import org.testng.annotations.Test;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

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
        info.setBic("bic");
        info.setClosingBalance(BigDecimal.TEN);
        info.setCurrency("czk");
        info.setDateEnd(getGregorianCalendar(2017));
        info.setDateStart(getGregorianCalendar(2016));
        info.setIban("iban");
        info.setIdFrom(BigInteger.ONE);
        info.setIdLastDownload(BigInteger.ZERO);
        info.setIdList(new BigInteger("11"));
        info.setIdTo(new BigInteger("12"));
        info.setOpeningBalance(new BigDecimal("9"));
        info.setYearList(BigInteger.ZERO);

        final AccountStatement source = new AccountStatement();
        source.setInfo(info);
        final TransactionList transactionList = new TransactionList();
        transactionList.getTransaction().add( this.getTransaction("999", "888", "777") );
        source.setTransactionList(transactionList);

        final FioAccountStatement target = service.convert(source, FioAccountStatement.class);

        assertNotNull(target);

        assertNotNull(target.getInfo());
        assertEquals(target.getInfo().getAccountId(), info.getAccountId());
        assertEquals(target.getInfo().getBankId(), info.getBankId());
        assertEquals(target.getInfo().getBic(), info.getBic());
        assertEquals(target.getInfo().getCurrency(), info.getCurrency());
        assertEquals(target.getInfo().getIban(), info.getIban());
        assertEquals(target.getInfo().getClosingBalance(), info.getClosingBalance());
        assertEquals(target.getInfo().getCurrency(), info.getCurrency());
        assertEquals(target.getInfo().getDateEnd(), LocalDate.of(2017,1,1));
        assertEquals(target.getInfo().getDateStart(), LocalDate.of(2016,1,1));
        assertEquals(target.getInfo().getIdFrom(), info.getIdFrom());
        assertEquals(target.getInfo().getIdLastDownload(), info.getIdLastDownload());
        assertEquals(target.getInfo().getIdList(), info.getIdList());
        assertEquals(target.getInfo().getIdTo(), info.getIdTo());
        assertEquals(target.getInfo().getOpeningBalance(), info.getOpeningBalance());
        assertEquals(target.getInfo().getYearList(), info.getYearList());

        assertNotNull(target.getTransactions());
        assertEquals(target.getTransactions().size(), 1);

        FioTransaction tr = target.getTransactions().get(0);
        assertEquals(tr.getIdPohybu(), "22");
        assertEquals(tr.getDatum(), LocalDate.of(2000,1,1));
        assertEquals(tr.getObjem(), BigDecimal.ONE);
        assertEquals(tr.getTyp(), "8");
        assertEquals(tr.getMena(), "14");
        assertEquals(tr.getVariabilniSymbol(), "999");
        assertEquals(tr.getSpecifickySymbol(), "888");
        assertEquals(tr.getKonstantniSymbol(), "777");
    }

    private static XMLGregorianCalendar getGregorianCalendar(int year) throws Exception {
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        cal.setYear(year);
        cal.setMonth(1);
        cal.setDay(1);
        return cal;
    }

    @Test
    public void shouldConvertTransactionWithoutColumn8() throws Exception {
        final AccountStatement source = new AccountStatement();
        source.setInfo(new Info());
        final TransactionList transactionList = new TransactionList();
        final Transaction transaction = getTransaction("1", "2", "3");
        transaction.setColumn8(null);
        transactionList.getTransaction().add(transaction);
        source.setTransactionList(transactionList);

        final FioAccountStatement target = service.convert(source, FioAccountStatement.class);
        assertEquals(target.getTransactions().size(), 1);
        assertNull(target.getTransactions().get(0).getTyp());
    }

    @Test
    public void shouldConvertNewColumns() throws Exception {
        final AccountStatement source = new AccountStatement();
        source.setInfo(new Info());
        final TransactionList transactionList = new TransactionList();
        final Transaction transaction = getTransaction("1", "2", "3");

        final Column10 c10 = new Column10();
        c10.setId(BigInteger.ONE);
        c10.setName("column_10");
        c10.setValue("nazev protiuctu");
        transaction.setColumn10(c10);

        final Column16 c16 = new Column16();
        c16.setId(BigInteger.ONE);
        c16.setName("column_16");
        c16.setValue("zprava pro prijemce");
        transaction.setColumn16(c16);

        final Column18 c18 = new Column18();
        c18.setId(BigInteger.ONE);
        c18.setName("column_18");
        c18.setValue("upresneni");
        transaction.setColumn18(c18);

        final Column26 c26 = new Column26();
        c26.setId(BigInteger.ONE);
        c26.setName("column_26");
        c26.setValue("FIOBCZPPXXX");
        transaction.setColumn26(c26);

        transactionList.getTransaction().add(transaction);
        source.setTransactionList(transactionList);

        final FioAccountStatement target = service.convert(source, FioAccountStatement.class);
        final FioTransaction tr = target.getTransactions().get(0);
        assertEquals(tr.getNazevProtiuctu(), "nazev protiuctu");
        assertEquals(tr.getZprava(), "zprava pro prijemce");
        assertEquals(tr.getUpresneni(), "upresneni");
        assertEquals(tr.getBic(), "FIOBCZPPXXX");
    }

    @Test
    public void shouldConvertNullNewColumns() throws Exception {
        final AccountStatement source = new AccountStatement();
        source.setInfo(new Info());
        final TransactionList transactionList = new TransactionList();
        transactionList.getTransaction().add(getTransaction("1", "2", "3"));
        source.setTransactionList(transactionList);

        final FioAccountStatement target = service.convert(source, FioAccountStatement.class);
        final FioTransaction tr = target.getTransactions().get(0);
        assertNull(tr.getNazevProtiuctu());
        assertNull(tr.getZprava());
        assertNull(tr.getUpresneni());
        assertNull(tr.getBic());
    }

    private Transaction getTransaction(String vs, String ss, String ks) throws Exception {
        final Transaction transaction = new Transaction();

        final Column22 c = new Column22();
        c.setId(BigInteger.ONE);
        c.setName("column_22");
        c.setValue("22");
        transaction.setColumn22(c);

        final Column0 c0 = new Column0();
        c0.setId(BigInteger.ONE);
        c0.setName("column_0");
        c0.setValue(getGregorianCalendar(2000));
        transaction.setColumn0(c0);

        final Column1 c1 = new Column1();
        c1.setId(BigInteger.ONE);
        c1.setName("column_1");
        c1.setValue(BigDecimal.ONE);
        transaction.setColumn1(c1);

        final Column8 c8 = new Column8();
        c8.setId(BigInteger.ONE);
        c8.setName("column_8");
        c8.setValue("8");
        transaction.setColumn8(c8);

        final Column14 c14 = new Column14();
        c14.setId(BigInteger.ONE);
        c14.setName("column_14");
        c14.setValue("14");
        transaction.setColumn14(c14);

        final Column5 vsCol = new Column5();
        vsCol.setId(BigInteger.ONE);
        vsCol.setName("column_5");
        vsCol.setValue(vs);
        transaction.setColumn5(vsCol);

        final Column4 ksCol = new Column4();
        ksCol.setId(BigInteger.ONE);
        ksCol.setName("column_4");
        ksCol.setValue(ks);
        transaction.setColumn4(ksCol);

        final Column6 ssCol = new Column6();
        ssCol.setId(BigInteger.ONE);
        ssCol.setName("column_6");
        ssCol.setValue(ss);
        transaction.setColumn6(ssCol);

        return transaction;
    }
}
