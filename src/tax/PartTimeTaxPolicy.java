package tax;

import java.math.BigDecimal;

/**
 * Tax policy for Part-Time employees (type code 2).
 * <p>
 * Supports both OLD and NEW tax regimes:
 * - NEW regime (default): Flat 12%
 * - OLD regime: Flat 10%
 */
public class PartTimeTaxPolicy implements TaxPolicy {

    public static final PartTimeTaxPolicy INSTANCE = new PartTimeTaxPolicy();

    private static final BigDecimal TAX_RATE = new BigDecimal("0.12");
    private static final BigDecimal OLD_TAX_RATE = new BigDecimal("0.10");

    private PartTimeTaxPolicy() {}

    @Override
    public BigDecimal calculateTax(BigDecimal gross) {
        return calculateTax(gross, TaxRegime.NEW);
    }

    @Override
    public BigDecimal calculateTax(BigDecimal gross, TaxRegime regime) {
        if (regime == TaxRegime.OLD) {
            return gross.multiply(OLD_TAX_RATE);
        }
        return gross.multiply(TAX_RATE);
    }

    @Override
    public int getTypeCode() {
        return 2; // Part-Time
    }
}
