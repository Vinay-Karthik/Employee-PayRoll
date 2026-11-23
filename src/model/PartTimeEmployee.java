package model;

import tax.DeductionPolicy;
import tax.TaxPolicy;
import tax.TaxRegime;

import java.math.BigDecimal;

/**
 * Concrete Employee implementation for Part-Time Hourly workers.
 * All monetary values use BigDecimal to avoid floating-point errors.
 * <p>
 * Tax and deduction rules are delegated to {@link tax.PartTimeTaxPolicy}
 * and {@link tax.PartTimeDeductionPolicy} for reuse across calculation paths.
 * <p>
 * Gross salary calculation: hourly rate * hours worked with overtime (hours > 40 at 1.5x)
 * (matches {@link calculator.PartTimeSalaryCalculator}).
 */
public class PartTimeEmployee extends Employee {

    private BigDecimal hourlyRate;
    private BigDecimal hoursWorked;

    public PartTimeEmployee(int id, String name, String email, String department, String bankAccountNumber,
                            BigDecimal hourlyRate, BigDecimal hoursWorked) {
        this(id, name, email, department, bankAccountNumber, hourlyRate, hoursWorked, TaxRegime.NEW);
    }

    public PartTimeEmployee(int id, String name, String email, String department, String bankAccountNumber,
                            BigDecimal hourlyRate, BigDecimal hoursWorked, TaxRegime taxRegime) {
        super(id, name, email, department, bankAccountNumber, taxRegime);
        if (hourlyRate == null || hoursWorked == null) {
            throw new IllegalArgumentException("Hourly rate and hours worked must not be null");
        }
        if (hourlyRate.signum() < 0 || hoursWorked.signum() < 0) {
            throw new IllegalArgumentException("Hourly rate and hours worked must be non-negative");
        }
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        if (hourlyRate == null || hourlyRate.signum() < 0) {
            throw new IllegalArgumentException("Hourly rate must be non-negative");
        }
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(BigDecimal hoursWorked) {
        if (hoursWorked == null || hoursWorked.signum() < 0) {
            throw new IllegalArgumentException("Hours worked must be non-negative");
        }
        this.hoursWorked = hoursWorked;
    }

    @Override
    public BigDecimal calculateGrossPay() {
        return GrossPayCalculator.partTime(hourlyRate, hoursWorked);
    }

    @Override
    public BigDecimal calculateTax() {
        return TaxPolicy.forEmployee(this).calculateTax(calculateGrossPay(), getTaxRegime());
    }

    @Override
    public BigDecimal calculateDeductions() {
        return DeductionPolicy.forEmployee(this).calculateDeductions(calculateGrossPay());
    }

    @Override
    public String getRoleCategory() {
        return "Part-Time Hourly";
    }

    @Override
    public EmployeeType getEmployeeType() {
        return EmployeeType.PART_TIME;
    }

    @Override
    public String getPayDetailsSummary() {
        return String.format("$%.2f/hr x %.1f hrs", hourlyRate, hoursWorked);
    }
}
