package cz.geek.fio;

import lombok.Data;

import java.util.List;

import static org.apache.commons.lang3.Validate.noNullElements;
import static org.apache.commons.lang3.Validate.notNull;

@Data
public class FioAccountStatement {

    private final FioAccountInfo info;
    private final List<FioTransaction> transactions;

    public FioAccountStatement(final FioAccountInfo info, final List<FioTransaction> transactions) {
        this.info = notNull(info);
        this.transactions = noNullElements(transactions);
    }

}
