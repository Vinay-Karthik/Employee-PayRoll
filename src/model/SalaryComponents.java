package model;

import java.math.BigDecimal;

/**
 * Value Object holding salary components for an employee.
 * Not all fields are used by every calculator - each calculator
 * reads only the components relevant to its salary structure.
 *
 * All monetary values use BigDecimal to avoid floating-point errors.
 * Includes copy constructor for deep copying (Prototype pattern).
 */
public class SalaryComponents {
    private BigDecimal basic = BigDecimal.ZERO;
    private BigDecimal hra = BigDecimal.ZERO;
    private BigDecimal allowances = BigDecimal.ZERO;
    private BigDecimal fixedContractAmount = BigDecimal.ZERO;
    private BigDecimal fixedSalary = BigDecimal.ZERO;
    private BigDecimal commission = BigDecimal.ZERO;
    private BigDecimal bonus = BigDecimal.ZERO;
    private BigDecimal benefits = BigDecimal.ZERO;

    public SalaryComponents() {
    }

    /**
     * Copy constructor used for deep copying in the Prototype pattern.
     */
    public SalaryComponents(SalaryComponents other) {
        this.basic = other.basic;
        this.hra = other.hra;
        this.allowances = other.allowances;
        this.fixedContractAmount = other.fixedContractAmount;
        this.fixedSalary = other.fixedSalary;
        this.commission = other.commission;
        this.bonus = other.bonus;
        this.benefits = other.benefits;
    }

    private static BigDecimal requireNonNegative(String field, BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException(field + " must not be null");
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException(field + " cannot be negative: " + value);
        }
        return value;
    }

    public BigDecimal getBasic() { return basic; }
    public void setBasic(BigDecimal basic) { this.basic = requireNonNegative("Basic", basic); }

    public BigDecimal getHra() { return hra; }
    public void setHra(BigDecimal hra) { this.hra = requireNonNegative("HRA", hra); }

    public BigDecimal getAllowances() { return allowances; }
    public void setAllowances(BigDecimal allowances) { this.allowances = requireNonNegative("Allowances", allowances); }

    public BigDecimal getFixedContractAmount() { return fixedContractAmount; }
    public void setFixedContractAmount(BigDecimal fixedContractAmount) { this.fixedContractAmount = requireNonNegative("Fixed contract amount", fixedContractAmount); }

    public BigDecimal getFixedSalary() { return fixedSalary; }
    public void setFixedSalary(BigDecimal fixedSalary) { this.fixedSalary = requireNonNegative("Fixed salary", fixedSalary); }

    public BigDecimal getCommission() { return commission; }
    public void setCommission(BigDecimal commission) { this.commission = requireNonNegative("Commission", commission); }

    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = requireNonNegative("Bonus", bonus); }

    public BigDecimal getBenefits() { return benefits; }
    public void setBenefits(BigDecimal benefits) { this.benefits = requireNonNegative("Benefits", benefits); }

    @Override
    public String toString() {
        return String.format(
                "SalaryComponents{basic=%s, hra=%s, allowances=%s, fixedContract=%s, fixedSalary=%s, commission=%s, bonus=%s, benefits=%s}",
                basic, hra, allowances, fixedContractAmount, fixedSalary, commission, bonus, benefits);
    }
}
