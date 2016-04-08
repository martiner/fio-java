package cz.geek.fio;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.BigInteger;

public class FioAccountInfo {

    private String accountId;
    private String bankId;
    private String currency;
    private String iban;
    private String bic;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private BigInteger yearList;
    private BigInteger idList;
    private BigInteger idFrom;
    private BigInteger idTo;
    private BigInteger idLastDownload;


    public String getAccountId() {
        return accountId;
    }

    void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public String getBankId() {
        return bankId;
    }

    void setBankId(final String bankId) {
        this.bankId = bankId;
    }

    public String getCurrency() {
        return currency;
    }

    void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getIban() {
        return iban;
    }

    void setIban(final String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    void setBic(final String bic) {
        this.bic = bic;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    void setOpeningBalance(final BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    void setClosingBalance(final BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    void setDateStart(final LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    void setDateEnd(final LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    public BigInteger getYearList() {
        return yearList;
    }

    void setYearList(final BigInteger yearList) {
        this.yearList = yearList;
    }

    public BigInteger getIdList() {
        return idList;
    }

    void setIdList(final BigInteger idList) {
        this.idList = idList;
    }

    public BigInteger getIdFrom() {
        return idFrom;
    }

    void setIdFrom(final BigInteger idFrom) {
        this.idFrom = idFrom;
    }

    public BigInteger getIdTo() {
        return idTo;
    }

    void setIdTo(final BigInteger idTo) {
        this.idTo = idTo;
    }

    public BigInteger getIdLastDownload() {
        return idLastDownload;
    }

    void setIdLastDownload(final BigInteger idLastDownload) {
        this.idLastDownload = idLastDownload;
    }

}
