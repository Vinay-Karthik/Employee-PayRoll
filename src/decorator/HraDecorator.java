package decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete Decorator for House Rent Allowance (HRA).
 *
 * Single Responsibility: Adds HRA allowance to the salary.
 */
public class HraDecorator extends SalaryDecorator {
    private static final BigDecimal DEFAULT_HRA_PERCENTAGE = new BigDecimal("0.20"); // 20% of base
    private final BigDecimal hraAmount;

    /**
     * Constructs HRA decorator using default 20% of base salary.
     */
    public HraDecorator(SalaryCalculator decoratedCalculator) {
        super(decoratedCalculator);
        this.hraAmount = decoratedCalculator.getBaseSalary()
                .multiply(DEFAULT_HRA_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Constructs HRA decorator with an explicit fixed monetary amount.
     *
     * @param decoratedCalculator the calculator to decorate
     * @param hraAmount explicit non-null, non-negative HRA amount
     */
    public HraDecorator(SalaryCalculator decoratedCalculator, BigDecimal hraAmount) {
        super(decoratedCalculator);
        if (hraAmount == null) {
            throw new IllegalArgumentException("HRA amount cannot be null");
        }
        if (hraAmount.signum() < 0) {
            throw new IllegalArgumentException("HRA amount cannot be negative: " + hraAmount);
        }
        this.hraAmount = hraAmount;
    }

    @Override
    public BigDecimal calculateSalary() {
        return super.calculateSalary().add(hraAmount);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + HRA (" + hraAmount + ")";
    }

    public BigDecimal getHraAmount() {
        return hraAmount;
    }
}
