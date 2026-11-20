package decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete Component (GoF Decorator Pattern).
 *
 * Encapsulates the core base salary calculation.
 * Remains completely unmodified when decorators are dynamically added.
 * Enforces a consistent scale of 2 with RoundingMode.HALF_UP.
 */
public class BaseSalaryCalculator implements SalaryCalculator {
    private final BigDecimal baseSalary;

    /**
     * Constructs a BaseSalaryCalculator with a validated non-null, non-negative base salary.
     * Enforces consistent monetary scale (2 decimal places) and HALF_UP rounding.
     *
     * @param baseSalary the initial base salary
     * @throws IllegalArgumentException if baseSalary is null or negative
     */
    public BaseSalaryCalculator(BigDecimal baseSalary) {
        if (baseSalary == null) {
            throw new IllegalArgumentException("Base salary cannot be null");
        }
        if (baseSalary.signum() < 0) {
            throw new IllegalArgumentException("Base salary cannot be negative: " + baseSalary);
        }
        this.baseSalary = baseSalary.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateSalary() {
        return baseSalary;
    }

    @Override
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    @Override
    public String getDescription() {
        return "Base Salary (" + baseSalary + ")";
    }
}
