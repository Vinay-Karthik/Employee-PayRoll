package model;

import tax.DeductionPolicy;
import tax.TaxPolicy;

import java.math.BigDecimal;

/**
 * Concrete Employee implementation for Full-Time Salaried workers.
 * All monetary values use BigDecimal to avoid floating-point errors.
 * <p>
 * Tax and deduction rules are delegated to {@link tax.FullTimeTaxPolicy}
 * and {@link tax.FullTimeDeductionPolicy} for reuse across calculation paths.
 */
public class FullTimeEmployee extends Employee {

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
        return TaxPolicy.forEmployee(this).calculateTax(calculateGrossPay());
    }

    @Override
    public BigDecimal calculateDeductions() {
        return DeductionPolicy.forEmployee(this).calculateDeductions(calculateGrossPay());
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
