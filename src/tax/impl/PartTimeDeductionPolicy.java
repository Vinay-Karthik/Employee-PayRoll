package tax.impl;

import tax.policy.DeductionPolicy;

import java.math.BigDecimal;

/**
 * Deduction policy for Part-Time employees (type code 2).
 * <p>
 * No standard deductions.
 */
public class PartTimeDeductionPolicy implements DeductionPolicy {

    public static final PartTimeDeductionPolicy INSTANCE = new PartTimeDeductionPolicy();

    private PartTimeDeductionPolicy() {}

    @Override
    public BigDecimal calculateDeductions(BigDecimal gross) {
        return BigDecimal.ZERO;
    }
}