package model.value;

import java.math.BigDecimal;

/**
 * Value Object holding salary components for an employee.
 * Used across the unified employee classifications (Full-Time, Part-Time, Contractor, Intern).
 *
 * All monetary values use BigDecimal to avoid floating-point errors.
 * Includes copy constructor for defensive copying (Builder pattern).
 * Note: HRA and Allowances are handled by the Decorator pattern (SalaryEnhancementPipeline)
 * rather than being part of the base salary components.
 */
public class SalaryComponents {
    private BigDecimal basic = BigDecimal.ZERO;
    private BigDecimal bonus = BigDecimal.ZERO;
    private BigDecimal hourlyRate = BigDecimal.ZERO;
    private BigDecimal hoursWorked = BigDecimal.ZERO;
    private BigDecimal fixedContractAmount = BigDecimal.ZERO;
    private BigDecimal stipend = BigDecimal.ZERO;

    public SalaryComponents() {
    }

    /**
     * Copy constructor used for defensive copying.
     */
    public SalaryComponents(SalaryComponents other) {
        this.basic = other.basic;
        this.bonus = other.bonus;
        this.hourlyRate = other.hourlyRate;
        this.hoursWorked = other.hoursWorked;
        this.fixedContractAmount = other.fixedContractAmount;
        this.stipend = other.stipend;
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

    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = requireNonNegative("Bonus", bonus); }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = requireNonNegative("Hourly rate", hourlyRate); }

    public BigDecimal getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(BigDecimal hoursWorked) { this.hoursWorked = requireNonNegative("Hours worked", hoursWorked); }

    public BigDecimal getFixedContractAmount() { return fixedContractAmount; }
    public void setFixedContractAmount(BigDecimal fixedContractAmount) { this.fixedContractAmount = requireNonNegative("Fixed contract amount", fixedContractAmount); }

    public BigDecimal getStipend() { return stipend; }
    public void setStipend(BigDecimal stipend) { this.stipend = requireNonNegative("Stipend", stipend); }

    @Override
    public String toString() {
        return String.format(
                "SalaryComponents{basic=%s, bonus=%s, hourlyRate=%s, hoursWorked=%s, fixedContract=%s, stipend=%s}",
                basic, bonus, hourlyRate, hoursWorked, fixedContractAmount, stipend);
    }
}
