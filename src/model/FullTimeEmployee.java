package model;

import java.math.BigDecimal;

/**
 * Concrete Employee implementation for Full-Time Salaried workers.
 * All monetary values use BigDecimal to avoid floating-point errors.
 */
public class FullTimeEmployee extends Employee {
    private static final BigDecimal STANDARD_TAX_RATE = new BigDecimal("0.22");
    private static final BigDecimal HIGH_EARNER_THRESHOLD = new BigDecimal("7000.00");
    private static final BigDecimal SURTAX_RATE = new BigDecimal("0.05");
    private static final BigDecimal HEALTH_INSURANCE_DEDUCTION = new BigDecimal("150.00");
    private static final BigDecimal RETIREMENT_FUND_RATE = new BigDecimal("0.05");

    private BigDecimal baseSalary;
    private BigDecimal bonus;

    public FullTimeEmployee(int id, String name, String email, String department, String bankAccountNumber,
                            BigDecimal baseSalary, BigDecimal bonus) {
        super(id, name, email, department, bankAccountNumber);
        if (baseSalary == null || bonus == null) {
            throw new IllegalArgumentException("Base salary and bonus must not be null");
        }
        if (baseSalary.signum() < 0 || bonus.signum() < 0) {
            throw new IllegalArgumentException("Base salary and bonus must be non-negative");
        }
        this.baseSalary = baseSalary;
        this.bonus = bonus;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        if (baseSalary == null || baseSalary.signum() < 0) {
            throw new IllegalArgumentException("Base salary must be non-negative");
        }
        this.baseSalary = baseSalary;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        if (bonus == null || bonus.signum() < 0) {
            throw new IllegalArgumentException("Bonus must be non-negative");
        }
        this.bonus = bonus;
    }

    @Override
    public BigDecimal calculateGrossPay() {
        return baseSalary.add(bonus);
    }

    @Override
    public BigDecimal calculateTax() {
        BigDecimal gross = calculateGrossPay();
        BigDecimal tax = gross.multiply(STANDARD_TAX_RATE);
        if (gross.compareTo(HIGH_EARNER_THRESHOLD) > 0) {
            tax = tax.add(gross.subtract(HIGH_EARNER_THRESHOLD).multiply(SURTAX_RATE));
        }
        return tax;
    }

    @Override
    public BigDecimal calculateDeductions() {
        return HEALTH_INSURANCE_DEDUCTION.add(calculateGrossPay().multiply(RETIREMENT_FUND_RATE));
    }

    @Override
    public String getRoleCategory() {
        return "Full-Time Salaried";
    }

    @Override
    public int getTypeCode() {
        return 1;
    }

    @Override
    public String getPayDetailsSummary() {
        return String.format("$%.2f (Bonus: $%.2f)", baseSalary, bonus);
    }
}
