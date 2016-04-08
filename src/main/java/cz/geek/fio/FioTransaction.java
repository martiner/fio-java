package cz.geek.fio;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;

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

    public String getIdPohybu() {
        return idPohybu;
    }

    void setIdPohybu(final String idPohybu) {
        this.idPohybu = idPohybu;
    }

    public LocalDate getDatum() {
        return datum;
    }

    void setDatum(final LocalDate datum) {
        this.datum = datum;
    }

    public BigDecimal getObjem() {
        return objem;
    }

    void setObjem(final BigDecimal objem) {
        this.objem = objem;
    }

    public String getMena() {
        return mena;
    }

    void setMena(final String mena) {
        this.mena = mena;
    }

    public String getProtiucet() {
        return protiucet;
    }

    void setProtiucet(final String protiucet) {
        this.protiucet = protiucet;
    }

    public String getBankaKod() {
        return bankaKod;
    }

    void setBankaKod(final String bankaKod) {
        this.bankaKod = bankaKod;
    }

    public String getBankaNazev() {
        return bankaNazev;
    }

    void setBankaNazev(final String bankaNazev) {
        this.bankaNazev = bankaNazev;
    }

    public String getUzivatelskaIdentifikace() {
        return uzivatelskaIdentifikace;
    }

    void setUzivatelskaIdentifikace(final String uzivatelskaIdentifikace) {
        this.uzivatelskaIdentifikace = uzivatelskaIdentifikace;
    }

    public String getTyp() {
        return typ;
    }

    void setTyp(final String typ) {
        this.typ = typ;
    }

    public String getProvedl() {
        return provedl;
    }

    void setProvedl(final String provedl) {
        this.provedl = provedl;
    }

    public String getKomentar() {
        return komentar;
    }

    void setKomentar(final String komentar) {
        this.komentar = komentar;
    }

    public long getIdPokynu() {
        return idPokynu;
    }

    void setIdPokynu(final long idPokynu) {
        this.idPokynu = idPokynu;
    }

}
