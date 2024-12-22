package cz.geek.fio;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

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
