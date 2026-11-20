package creational.abstractfactory;

/**
 * Product interface for tax calculation components.
 * Implementations belong to a specific tax regime family.
 */
public interface TaxCalculator {

    /**
     * Computes tax withholding for the given gross pay.
     */
    double calculateTax(double grossPay);

    /**
     * Returns the regime this calculator belongs to.
     */
    TaxRegime getRegime();
}
