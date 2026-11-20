package decorator;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Domain Service / Pipeline for dynamic salary enhancements (Decorator Pattern).
 *
 * Encapsulates all business rules related to salary decoration:
 * 1. Single-use component rule: Ensures HRA, allowances, bonuses, and deductions
 *    cannot be duplicated in the same calculation pipeline.
 * 2. Deduction validation: Ensures deductions never exceed accumulated salary.
 * 3. Assembly orchestration: Instantiates and chains appropriate decorators while
 *    keeping UI layers free of business logic.
 */
public class SalaryEnhancementPipeline {

    private SalaryCalculator currentCalculator;
    private final Set<DecoratorType> appliedTypes = EnumSet.noneOf(DecoratorType.class);

    /**
     * Initializes the pipeline with a base salary component.
     *
     * @param baseSalary the initial non-null, non-negative base salary
     */
    public SalaryEnhancementPipeline(BigDecimal baseSalary) {
        this.currentCalculator = new BaseSalaryCalculator(baseSalary);
    }

    /**
     * Initializes the pipeline from an existing SalaryCalculator component.
     *
     * @param baseCalculator existing base calculator component
     */
    public SalaryEnhancementPipeline(SalaryCalculator baseCalculator) {
        this.currentCalculator = Objects.requireNonNull(baseCalculator, "Base calculator cannot be null");
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
     * @throws IllegalStateException if the component is already applied or deduction exceeds salary
     */
    public void applyDecorator(DecoratorType type) {
        applyDecorator(type, null);
    }

    /**
     * Applies a decorator with an optional custom monetary amount.
     *
     * @param type the decorator type to apply
     * @param customAmount explicit custom amount (or null to use default)
     * @throws IllegalStateException if the component is already applied or deduction exceeds salary
     * @throws IllegalArgumentException if custom amount is invalid
     */
    public void applyDecorator(DecoratorType type, BigDecimal customAmount) {
        Objects.requireNonNull(type, "DecoratorType must not be null");

        // Business rule 1: Single-use constraint
        if (appliedTypes.contains(type)) {
            throw new IllegalStateException(
                    type.getDisplayName() + " decorator is already applied! Cannot apply duplicate single-use component."
            );
        }

        // Business rule 2: Construct decorator and validate constraints
        SalaryCalculator enhanced = switch (type) {
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
                SalaryCalculator next = customAmount == null
                        ? new InsuranceDeductionDecorator(currentCalculator)
                        : new InsuranceDeductionDecorator(currentCalculator, customAmount);
                // Validate business rule 3: deduction cannot exceed accumulated salary
                next.calculateSalary();
                yield next;
            }
        };

        this.currentCalculator = enhanced;
        this.appliedTypes.add(type);
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

    /**
     * Returns the current SalaryCalculator component.
     */
    public SalaryCalculator getCalculator() {
        return currentCalculator;
    }
}
