package model;

import tax.DeductionPolicy;
import tax.TaxPolicy;
import tax.TaxRegime;

import java.math.BigDecimal;

/**
 * Concrete Employee implementation for Independent Contractors.
 * All monetary values use BigDecimal to avoid floating-point errors.
 * <p>
 * Tax and deduction rules are delegated to {@link tax.ContractorTaxPolicy}
 * and {@link tax.ContractorDeductionPolicy} for reuse across calculation paths.
 */
public class ContractorEmployee extends Employee {

    private BigDecimal hourlyRate;
    private BigDecimal hoursWorked;

    public ContractorEmployee(int id, String name, String email, String department, String bankAccountNumber,
                              BigDecimal hourlyRate, BigDecimal hoursWorked) {
        this(id, name, email, department, bankAccountNumber, hourlyRate, hoursWorked, TaxRegime.NEW);
    }

    public ContractorEmployee(int id, String name, String email, String department, String bankAccountNumber,
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
        return hoursWorked.multiply(hourlyRate);
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
        return "Independent Contractor";
    }

    @Override
    public int getTypeCode() {
        return 3;
    }

    @Override
    public String getPayDetailsSummary() {
        return String.format("$%.2f/hr x %.1f hrs", hourlyRate, hoursWorked);
    }
}
