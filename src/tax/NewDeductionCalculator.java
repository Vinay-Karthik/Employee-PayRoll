package tax;

import java.math.BigDecimal;

/**
 * New tax regime deduction calculator: flat standard deduction,
 * since most exemptions are unavailable under the new regime.
 */
public class NewDeductionCalculator implements DeductionCalculator {
    private static final BigDecimal STANDARD_DEDUCTION = new BigDecimal("50000.00");

    @Override
    public BigDecimal calculateDeductions(BigDecimal grossPay) {
        return STANDARD_DEDUCTION.min(grossPay);
    }

    @Override
    public TaxRegime getRegime() {
        return TaxRegime.NEW;
    }
}
