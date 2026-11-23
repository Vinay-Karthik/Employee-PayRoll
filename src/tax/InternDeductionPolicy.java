package tax;

import java.math.BigDecimal;

/**
 * Deduction policy for Intern employees (type code 4).
 * <p>
 * No standard deductions.
 */
public class InternDeductionPolicy implements DeductionPolicy {

    public static final InternDeductionPolicy INSTANCE = new InternDeductionPolicy();

    private InternDeductionPolicy() {}

    @Override
    public BigDecimal calculateDeductions(BigDecimal gross) {
        return BigDecimal.ZERO;
    }
}