package decorator.pipeline;

import decorator.component.BaseSalaryCalculator;
import decorator.component.SalaryComponent;
import decorator.decorator.HraDecorator;
import decorator.decorator.PerformanceBonusDecorator;
import decorator.decorator.TransportAllowanceDecorator;
import decorator.decorator.InsuranceDeductionDecorator;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Domain Service / Fluent Pipeline for dynamic salary enhancements (Decorator Pattern).
 *
 * Encapsulates all business rules related to salary decoration:
 * 1. Single-use component rule: Ensures HRA, allowances, bonuses, and deductions
 *    cannot be duplicated in the same calculation pipeline.
 * 2. Deduction validation: Ensures deductions never exceed accumulated salary.
 * 3. Assembly orchestration: Instantiates and chains appropriate decorators while
 *    keeping presentation layers free of decorator instantiation boilerplate.
 */
public class SalaryEnhancementPipeline {

    private SalaryComponent currentCalculator;
    private final Set<DecoratorType> appliedTypes = EnumSet.noneOf(DecoratorType.class);

    /**
     * Initializes the pipeline with a base salary component.
     *
     * @param baseSalary the initial non-null, non-negative base salary
     * @throws IllegalArgumentException if baseSalary is null or negative
     */
    public SalaryEnhancementPipeline(BigDecimal baseSalary) {
        Objects.requireNonNull(baseSalary, "Base salary must not be null");
        if (baseSalary.signum() < 0) {
            throw new IllegalArgumentException("Base salary must be non-negative");
        }
        this.currentCalculator = new BaseSalaryCalculator(baseSalary);
    }

    /**
     * Checks if a specific decorator type has already been applied.
     *
     * @param type decorator type to verify
     * @return true if already applied, false otherwise
     */
    public boolean isApplied(DecoratorType type) {
        return appliedTypes.contains(type);
    }

    /**
     * Returns an unmodifiable view of all applied decorator types.
     */
    public Set<DecoratorType> getAppliedTypes() {
        return Collections.unmodifiableSet(appliedTypes);
    }

    /**
     * Applies a standard decorator using its default configuration.
     *
     * @param type the decorator type to apply
     * @return this pipeline for fluent chaining
     * @throws IllegalStateException if the component is already applied or deduction exceeds salary
     */
    public SalaryEnhancementPipeline applyDecorator(DecoratorType type) {
        return applyDecorator(type, null);
    }

    /**
     * Applies a decorator with an optional custom monetary amount.
     *
     * @param type the decorator type to apply
     * @param customAmount explicit custom amount (or null to use default)
     * @return this pipeline for fluent chaining
     * @throws IllegalStateException if the component is already applied or deduction exceeds salary
     * @throws IllegalArgumentException if custom amount is invalid
     */
    public SalaryEnhancementPipeline applyDecorator(DecoratorType type, BigDecimal customAmount) {
        Objects.requireNonNull(type, "DecoratorType must not be null");

        // Business rule 1: Single-use constraint
        if (appliedTypes.contains(type)) {
            throw new IllegalStateException(
                    type.getDisplayName() + " decorator is already applied! Cannot apply duplicate single-use component."
            );
        }

        // Business rule 2: Construct decorator and chain
        SalaryComponent enhanced = switch (type) {
            case HRA -> customAmount == null
                    ? new HraDecorator(currentCalculator)
                    : new HraDecorator(currentCalculator, customAmount);
            case TRANSPORT_ALLOWANCE -> customAmount == null
                    ? new TransportAllowanceDecorator(currentCalculator)
                    : new TransportAllowanceDecorator(currentCalculator, customAmount);
            case PERFORMANCE_BONUS -> customAmount == null
                    ? new PerformanceBonusDecorator(currentCalculator)
                    : new PerformanceBonusDecorator(currentCalculator, customAmount);
            case INSURANCE_DEDUCTION -> {
                SalaryComponent next = customAmount == null
                        ? new InsuranceDeductionDecorator(currentCalculator)
                        : new InsuranceDeductionDecorator(currentCalculator, customAmount);
                // Validate deduction immediately against accumulated salary
                next.calculateSalary();
                yield next;
            }
        };

        this.currentCalculator = enhanced;
        this.appliedTypes.add(type);
        return this;
    }

    /**
     * Returns the underlying base salary.
     */
    public BigDecimal getBaseSalary() {
        return currentCalculator.getBaseSalary();
    }

    /**
     * Returns the current calculated salary after all applied enhancements.
     */
    public BigDecimal getCurrentSalary() {
        return currentCalculator.calculateSalary();
    }

    /**
     * Returns the itemized description of the current pipeline.
     */
    public String getCurrentDescription() {
        return currentCalculator.getDescription();
    }
}
