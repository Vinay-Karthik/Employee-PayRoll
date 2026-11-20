package creational.abstractfactory;

import java.math.BigDecimal;

/**
 * Old tax regime tax calculator: flat 20% rate with 4% surcharge
 * on income above the exemption threshold.
 */
public class OldTaxCalculator implements TaxCalculator {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.20");
    private static final BigDecimal SURCHARGE_THRESHOLD = new BigDecimal("50000.00");
    private static final BigDecimal SURCHARGE_RATE = new BigDecimal("0.04");

    @Override
    public BigDecimal calculateTax(BigDecimal grossPay) {
        BigDecimal tax = grossPay.multiply(TAX_RATE);
        if (grossPay.compareTo(SURCHARGE_THRESHOLD) > 0) {
            tax = tax.add(grossPay.subtract(SURCHARGE_THRESHOLD).multiply(SURCHARGE_RATE));
        }
        return tax;
    }

    @Override
    public TaxRegime getRegime() {
        return TaxRegime.OLD;
    }
}
