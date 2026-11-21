package tax;

import java.math.BigDecimal;

/**
 * Tax policy for Intern employees (type code 4).
 * <p>
 * Tax exempt up to $2,000 stipend.
 * Surtax: 5% on amount above $2,000.
 */
public class InternTaxPolicy implements TaxPolicy {

    public static final InternTaxPolicy INSTANCE = new InternTaxPolicy();

    private static final BigDecimal TAX_EXEMPT_STIPEND_LIMIT = new BigDecimal("2000.00");
    private static final BigDecimal STIPEND_SURTAX_RATE = new BigDecimal("0.05");

    private InternTaxPolicy() {}

    @Override
    public BigDecimal calculateTax(BigDecimal gross) {
        if (gross.compareTo(TAX_EXEMPT_STIPEND_LIMIT) > 0) {
            return gross.subtract(TAX_EXEMPT_STIPEND_LIMIT).multiply(STIPEND_SURTAX_RATE);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public int getTypeCode() {
        return 4; // Intern
    }
}