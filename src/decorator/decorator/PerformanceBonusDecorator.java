package decorator.decorator;

import decorator.component.SalaryComponent;
import decorator.pipeline.DecoratorType;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete Decorator for Performance Bonus.
 *
 * Single Responsibility: Adds performance bonus to the salary.
 * Enforces a consistent scale of 2 with RoundingMode.HALF_UP for all monetary amounts.
 */
public class PerformanceBonusDecorator extends SalaryDecorator {
    private static final BigDecimal DEFAULT_BONUS_PERCENTAGE = new BigDecimal("0.10"); // 10% of base
    private final BigDecimal bonusAmount;

    /**
     * Constructs Performance Bonus decorator using default 10% of base salary.
     */
    public PerformanceBonusDecorator(SalaryComponent decoratedCalculator) {
        super(decoratedCalculator);
        this.bonusAmount = decoratedCalculator.getBaseSalary()
                .multiply(DEFAULT_BONUS_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Constructs Performance Bonus decorator with an explicit fixed monetary amount.
     * Enforces consistent monetary scale (2 decimal places) and HALF_UP rounding.
     *
     * @param decoratedCalculator the calculator to decorate
     * @param bonusAmount explicit non-null, non-negative bonus amount
     */
    public PerformanceBonusDecorator(SalaryComponent decoratedCalculator, BigDecimal bonusAmount) {
        super(decoratedCalculator);
        if (bonusAmount == null) {
            throw new IllegalArgumentException("Performance bonus cannot be null");
        }
        if (bonusAmount.signum() < 0) {
            throw new IllegalArgumentException("Performance bonus cannot be negative: " + bonusAmount);
        }
        this.bonusAmount = bonusAmount.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public DecoratorType getDecoratorType() {
        return DecoratorType.PERFORMANCE_BONUS;
    }

    @Override
    public BigDecimal calculateSalary() {
        return super.calculateSalary().add(bonusAmount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Performance Bonus (" + bonusAmount + ")";
    }

    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }
}
