package tax;

import java.math.BigDecimal;

/**
 * Tax policy for Full-Time employees (type code 1).
 * <p>
 * Standard rate: 22%
 * Surtax: 5% on earnings above $7,000
 */
public class FullTimeTaxPolicy implements TaxPolicy {

    public static final FullTimeTaxPolicy INSTANCE = new FullTimeTaxPolicy();

    private static final BigDecimal STANDARD_TAX_RATE = new BigDecimal("0.22");
    private static final BigDecimal HIGH_EARNER_THRESHOLD = new BigDecimal("7000.00");
    private static final BigDecimal SURTAX_RATE = new BigDecimal("0.05");

    private FullTimeTaxPolicy() {}

    @Override
    public BigDecimal calculateTax(BigDecimal gross) {
        BigDecimal tax = gross.multiply(STANDARD_TAX_RATE);
        if (gross.compareTo(HIGH_EARNER_THRESHOLD) > 0) {
            tax = tax.add(gross.subtract(HIGH_EARNER_THRESHOLD).multiply(SURTAX_RATE));
        }
        return tax;
    }

    @Override
    public int getTypeCode() {
        return 1; // Full-Time
    }
}