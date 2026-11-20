package tax;

import java.math.BigDecimal;

/**
 * Product interface for tax calculation components (Abstract Factory Pattern).
 * Implementations belong to a specific tax regime family.
 */
public interface TaxCalculator {

    /**
     * Computes tax withholding for the given gross pay.
     */
    BigDecimal calculateTax(BigDecimal grossPay);

    /**
     * Returns the regime this calculator belongs to.
     */
    TaxRegime getRegime();
}
