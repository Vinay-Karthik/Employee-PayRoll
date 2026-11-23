package calculator.core;

import model.value.SalaryComponents;

import java.math.BigDecimal;

/**
 * Product interface for salary calculation engines.
 * Each concrete calculator encapsulates salary calculation logic for one employment structure.
 */
public interface SalaryCalculator {

    /**
     * Computes the gross salary for the given salary components.
     */
    BigDecimal calculateGross(SalaryComponents components);
}
