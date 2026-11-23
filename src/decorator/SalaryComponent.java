package decorator;

import java.math.BigDecimal;

/**
 * Component Interface (GoF Decorator Pattern).
 *
 * Defines the contract for salary calculation components that can be
 * dynamically enhanced with allowances, bonuses, and deductions.
 */
public interface SalaryComponent {

    /**
     * Calculates the total net/gross salary including all applied enhancements.
     *
     * @return calculated total salary as BigDecimal
     */
    BigDecimal calculateSalary();

    /**
     * Returns the base salary of the underlying component.
     * Used by percentage-based decorators to compute additions against the base figure.
     *
     * @return base salary as BigDecimal
     */
    BigDecimal getBaseSalary();

    /**
     * Returns an itemized description of the calculation pipeline.
     *
     * @return descriptive breakdown string
     */
    String getDescription();
}
