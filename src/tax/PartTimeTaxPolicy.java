package tax;

import java.math.BigDecimal;

/**
 * Tax policy for Part-Time employees (type code 2).
 * <p>
 * Flat rate: 12%
 */
public class PartTimeTaxPolicy implements TaxPolicy {

    public static final PartTimeTaxPolicy INSTANCE = new PartTimeTaxPolicy();

    private static final BigDecimal TAX_RATE = new BigDecimal("0.12");

    private PartTimeTaxPolicy() {}

    @Override
    public BigDecimal calculateTax(BigDecimal gross) {
        return gross.multiply(TAX_RATE);
    }

    @Override
    public int getTypeCode() {
        return 2; // Part-Time
    }
}