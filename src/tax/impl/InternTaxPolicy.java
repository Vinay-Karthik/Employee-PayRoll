package tax.impl;

import tax.policy.TaxPolicy;
import tax.regime.TaxRegime;

import java.math.BigDecimal;

/**
 * Tax policy for Intern employees (type code 4).
 * <p>
 * Supports both OLD and NEW tax regimes:
 * - NEW regime (default): Tax exempt up to $2,000 stipend. 5% surtax on amount above $2,000.
 * - OLD regime: Tax exempt up to $1,500 stipend. 5% surtax on amount above $1,500.
 */
public class InternTaxPolicy implements TaxPolicy {

    public static final InternTaxPolicy INSTANCE = new InternTaxPolicy();

    private static final BigDecimal TAX_EXEMPT_STIPEND_LIMIT = new BigDecimal("2000.00");
    private static final BigDecimal OLD_TAX_EXEMPT_STIPEND_LIMIT = new BigDecimal("1500.00");
    private static final BigDecimal STIPEND_SURTAX_RATE = new BigDecimal("0.05");

    private InternTaxPolicy() {}

    @Override
    public BigDecimal calculateTax(BigDecimal gross) {
        return calculateTax(gross, TaxRegime.NEW);
    }

    @Override
    public BigDecimal calculateTax(BigDecimal gross, TaxRegime regime) {
        BigDecimal exemptLimit = (regime == TaxRegime.OLD) ? OLD_TAX_EXEMPT_STIPEND_LIMIT : TAX_EXEMPT_STIPEND_LIMIT;
        if (gross.compareTo(exemptLimit) > 0) {
            return gross.subtract(exemptLimit).multiply(STIPEND_SURTAX_RATE);
        }
        return BigDecimal.ZERO;
    }
}
