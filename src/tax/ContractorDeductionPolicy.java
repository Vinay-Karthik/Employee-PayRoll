package tax;

import java.math.BigDecimal;

/**
 * Deduction policy for Contractor employees (type code 3).
 * <p>
 * Service fee: 2% of gross
 */
public class ContractorDeductionPolicy implements DeductionPolicy {

    public static final ContractorDeductionPolicy INSTANCE = new ContractorDeductionPolicy();

    private static final BigDecimal SERVICE_FEE_RATE = new BigDecimal("0.02");

    private ContractorDeductionPolicy() {}

    @Override
    public BigDecimal calculateDeductions(BigDecimal gross) {
        return gross.multiply(SERVICE_FEE_RATE);
    }

    @Override
    public int getTypeCode() {
        return 3; // Contractor
    }
}