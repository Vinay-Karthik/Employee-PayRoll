package creational.abstractfactory;

import java.math.BigDecimal;

/**
 * New tax regime tax calculator: simplified progressive slabs.
 * 10% up to 5,00,000; 20% above.
 */
public class NewTaxCalculator implements TaxCalculator {
    private static final BigDecimal SLAB_LIMIT = new BigDecimal("500000.00");
    private static final BigDecimal LOWER_RATE = new BigDecimal("0.10");
    private static final BigDecimal UPPER_RATE = new BigDecimal("0.20");

    @Override
    public BigDecimal calculateTax(BigDecimal grossPay) {
        if (grossPay.compareTo(SLAB_LIMIT) <= 0) {
            return grossPay.multiply(LOWER_RATE);
        }
        return SLAB_LIMIT.multiply(LOWER_RATE)
                .add(grossPay.subtract(SLAB_LIMIT).multiply(UPPER_RATE));
    }

    @Override
    public TaxRegime getRegime() {
        return TaxRegime.NEW;
    }
}
