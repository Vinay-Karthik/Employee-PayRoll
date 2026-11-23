package tax;

import java.math.BigDecimal;

/**
 * Tax policy for Full-Time employees (type code 1).
 * <p>
 * Supports both OLD and NEW tax regimes:
 * - NEW regime (default): 22% standard rate with 5% surtax on earnings above $7,000.
 * - OLD regime: 20% baseline rate with 4% surcharge on earnings above $5,000.
 */
public class FullTimeTaxPolicy implements TaxPolicy {

    public static final FullTimeTaxPolicy INSTANCE = new FullTimeTaxPolicy();

    private static final BigDecimal STANDARD_TAX_RATE = new BigDecimal("0.22");
    private static final BigDecimal HIGH_EARNER_THRESHOLD = new BigDecimal("7000.00");
    private static final BigDecimal SURTAX_RATE = new BigDecimal("0.05");

    private static final BigDecimal OLD_TAX_RATE = new BigDecimal("0.20");
    private static final BigDecimal OLD_SURCHARGE_THRESHOLD = new BigDecimal("5000.00");
    private static final BigDecimal OLD_SURCHARGE_RATE = new BigDecimal("0.04");

    private FullTimeTaxPolicy() {}

    @Override
    public BigDecimal calculateTax(BigDecimal gross) {
        return calculateTax(gross, TaxRegime.NEW);
    }

    @Override
    public BigDecimal calculateTax(BigDecimal gross, TaxRegime regime) {
        if (regime == TaxRegime.OLD) {
            BigDecimal tax = gross.multiply(OLD_TAX_RATE);
            if (gross.compareTo(OLD_SURCHARGE_THRESHOLD) > 0) {
                tax = tax.add(gross.subtract(OLD_SURCHARGE_THRESHOLD).multiply(OLD_SURCHARGE_RATE));
            }
            return tax;
        }

        BigDecimal tax = gross.multiply(STANDARD_TAX_RATE);
        if (gross.compareTo(HIGH_EARNER_THRESHOLD) > 0) {
            tax = tax.add(gross.subtract(HIGH_EARNER_THRESHOLD).multiply(SURTAX_RATE));
        }
        return tax;
    }
}
