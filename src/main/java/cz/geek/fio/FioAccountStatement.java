package cz.geek.fio;

import java.util.List;

import static org.apache.commons.lang3.Validate.noNullElements;
import static org.apache.commons.lang3.Validate.notNull;

public class FioAccountStatement {

    private final FioAccountInfo info;
    private final List<FioTransaction> transactions;

    public FioAccountStatement(final FioAccountInfo info, final List<FioTransaction> transactions) {
        this.info = notNull(info);
        this.transactions = noNullElements(transactions);
    }

    public FioAccountInfo getInfo() {
        return info;
    }

    public List<FioTransaction> getTransactions() {
        return transactions;
    }
}
