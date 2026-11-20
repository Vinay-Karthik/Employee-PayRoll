package decorator;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Concrete Decorator for Insurance Deduction.
 *
 * Single Responsibility: Subtracts insurance premium deduction from the salary.
 */
public class InsuranceDeductionDecorator extends SalaryDecorator {
    private static final BigDecimal DEFAULT_INSURANCE_DEDUCTION = new BigDecimal("150.00");
    private final BigDecimal insuranceDeduction;

    /**
     * Constructs Insurance Deduction decorator using the default deduction ($150.00).
     */
    public InsuranceDeductionDecorator(SalaryCalculator decoratedCalculator) {
        this(decoratedCalculator, DEFAULT_INSURANCE_DEDUCTION);
    }

    /**
     * Constructs Insurance Deduction decorator with an explicit monetary deduction.
     *
     * @param decoratedCalculator the calculator to decorate
     * @param insuranceDeduction explicit non-null, non-negative deduction amount
     */
    public InsuranceDeductionDecorator(SalaryCalculator decoratedCalculator, BigDecimal insuranceDeduction) {
        super(decoratedCalculator);
        if (insuranceDeduction == null) {
            throw new IllegalArgumentException("Insurance deduction cannot be null");
        }
        if (insuranceDeduction.signum() < 0) {
            throw new IllegalArgumentException("Insurance deduction cannot be negative: " + insuranceDeduction);
        }
        this.insuranceDeduction = insuranceDeduction;
    }

    /**
     * Subtracts the insurance deduction from the accumulated salary.
     */
    @Override
    public BigDecimal calculateSalary() {
        return super.calculateSalary().subtract(insuranceDeduction);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " - Insurance Deduction (" + insuranceDeduction + ")";
    }

    public BigDecimal getInsuranceDeduction() {
        return insuranceDeduction;
    }
}
