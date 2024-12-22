package cz.geek.fio;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Data
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
}
