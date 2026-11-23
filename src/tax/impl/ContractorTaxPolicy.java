package tax.impl;

import tax.policy.TaxPolicy;
import tax.regime.TaxRegime;

import java.math.BigDecimal;

/**
 * Tax policy for Contractor employees (type code 3).
 * <p>
 * No tax withheld across regimes (contractors handle their own taxes).
 */
public class ContractorTaxPolicy implements TaxPolicy {

    public static final ContractorTaxPolicy INSTANCE = new ContractorTaxPolicy();

    private ContractorTaxPolicy() {}

    @Override
    public BigDecimal calculateTax(BigDecimal gross) {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTax(BigDecimal gross, TaxRegime regime) {
        return BigDecimal.ZERO;
    }
}
