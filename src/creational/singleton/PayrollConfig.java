package creational.singleton;

import creational.abstractfactory.TaxRegime;

/**
 * Task 1: Singleton Pattern - Shared Payroll Configuration Provider.
 *
 * Guarantees:
 * - Only one configuration instance exists (enum singleton, JVM-wide).
 * - Immutable after initialization (all fields final, no setters).
 * - Thread-safe by construction (enum initialization is guaranteed by the JLS).
 */
public enum PayrollConfig {
    INSTANCE("USD", "FY2025-26", TaxRegime.NEW);

    private final String currency;
    private final String financialYear;
    private final TaxRegime defaultTaxRegime;

    PayrollConfig(String currency, String financialYear, TaxRegime defaultTaxRegime) {
        this.currency = currency;
        this.financialYear = financialYear;
        this.defaultTaxRegime = defaultTaxRegime;
    }

    public static PayrollConfig getInstance() {
        return INSTANCE;
    }

    public String getCurrency() {
        return currency;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public TaxRegime getDefaultTaxRegime() {
        return defaultTaxRegime;
    }

    @Override
    public String toString() {
        return String.format("PayrollConfig{currency='%s', financialYear='%s', defaultTaxRegime=%s}",
                currency, financialYear, defaultTaxRegime);
    }
}
