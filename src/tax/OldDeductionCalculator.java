package tax;

import java.math.BigDecimal;

/**
 * Old tax regime deduction calculator: Provident Fund at 12% of gross.
 */
public class OldDeductionCalculator implements DeductionCalculator {
    private static final BigDecimal PF_RATE = new BigDecimal("0.12");

    @Override
    public BigDecimal calculateDeductions(BigDecimal grossPay) {
        return grossPay.multiply(PF_RATE);
    }

    @Override
    public TaxRegime getRegime() {
        return TaxRegime.OLD;
    }
}
