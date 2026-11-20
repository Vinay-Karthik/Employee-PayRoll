package tax;

import java.math.BigDecimal;

/**
 * Product interface for deduction calculation components (Abstract Factory Pattern).
 * Implementations belong to a specific tax regime family.
 */
public interface DeductionCalculator {

    /**
     * Computes total deductions for the given gross pay.
     */
    BigDecimal calculateDeductions(BigDecimal grossPay);

    /**
     * Returns the regime this calculator belongs to.
     */
    TaxRegime getRegime();
}
