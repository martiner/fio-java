package cz.geek.fio;

import lombok.Data;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;

@Data
public class FioTransaction {
    
    private String idPohybu;
    private LocalDate datum;
    private BigDecimal objem;
    private String mena;
    private String protiucet;
    private String bankaKod;
    private String bankaNazev;
    private String uzivatelskaIdentifikace;
    private String typ;
    private String provedl;
    private String komentar;
    private long idPokynu;

}
