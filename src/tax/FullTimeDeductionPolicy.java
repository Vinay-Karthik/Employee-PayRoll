package tax;

import java.math.BigDecimal;

/**
 * Deduction policy for Full-Time employees (type code 1).
 * <p>
 * Health insurance: $150.00 fixed
 * Retirement fund: 5% of gross
 */
public class FullTimeDeductionPolicy implements DeductionPolicy {

    public static final FullTimeDeductionPolicy INSTANCE = new FullTimeDeductionPolicy();

    private static final BigDecimal HEALTH_INSURANCE_DEDUCTION = new BigDecimal("150.00");
    private static final BigDecimal RETIREMENT_FUND_RATE = new BigDecimal("0.05");

    private FullTimeDeductionPolicy() {}

    @Override
    public BigDecimal calculateDeductions(BigDecimal gross) {
        return HEALTH_INSURANCE_DEDUCTION.add(gross.multiply(RETIREMENT_FUND_RATE));
    }
}