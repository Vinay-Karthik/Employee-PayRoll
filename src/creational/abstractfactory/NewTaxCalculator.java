package creational.abstractfactory;

/**
 * New tax regime tax calculator: simplified progressive slabs.
 * 10% up to 5,00,000; 20% above.
 */
public class NewTaxCalculator implements TaxCalculator {
    private static final double SLAB_LIMIT = 500000.0;
    private static final double LOWER_RATE = 0.10;
    private static final double UPPER_RATE = 0.20;

    @Override
    public double calculateTax(double grossPay) {
        if (grossPay <= SLAB_LIMIT) {
            return grossPay * LOWER_RATE;
        }
        return SLAB_LIMIT * LOWER_RATE + (grossPay - SLAB_LIMIT) * UPPER_RATE;
    }

    @Override
    public TaxRegime getRegime() {
        return TaxRegime.NEW;
    }
}
