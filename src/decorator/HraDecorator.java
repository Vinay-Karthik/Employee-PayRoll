package decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete Decorator for House Rent Allowance (HRA).
 *
 * Single Responsibility: Adds HRA allowance to the salary.
 * Enforces a consistent scale of 2 with RoundingMode.HALF_UP for all monetary amounts.
 */
public class HraDecorator extends SalaryDecorator {
    private static final BigDecimal DEFAULT_HRA_PERCENTAGE = new BigDecimal("0.20"); // 20% of base
    private final BigDecimal hraAmount;

    /**
     * Constructs HRA decorator using default 20% of base salary.
     */
    public HraDecorator(SalaryComponent decoratedCalculator) {
        super(decoratedCalculator);
        this.hraAmount = decoratedCalculator.getBaseSalary()
                .multiply(DEFAULT_HRA_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Constructs HRA decorator with an explicit fixed monetary amount.
     * Enforces consistent monetary scale (2 decimal places) and HALF_UP rounding.
     *
     * @param decoratedCalculator the calculator to decorate
     * @param hraAmount explicit non-null, non-negative HRA amount
     */
    public HraDecorator(SalaryComponent decoratedCalculator, BigDecimal hraAmount) {
        super(decoratedCalculator);
        if (hraAmount == null) {
            throw new IllegalArgumentException("HRA amount cannot be null");
        }
        if (hraAmount.signum() < 0) {
            throw new IllegalArgumentException("HRA amount cannot be negative: " + hraAmount);
        }
        this.hraAmount = hraAmount.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public DecoratorType getDecoratorType() {
        return DecoratorType.HRA;
    }

    @Override
    public BigDecimal calculateSalary() {
        return super.calculateSalary().add(hraAmount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + HRA (" + hraAmount + ")";
    }

    public BigDecimal getHraAmount() {
        return hraAmount;
    }
}
