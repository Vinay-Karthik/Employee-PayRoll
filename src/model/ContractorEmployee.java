package model;

import java.math.BigDecimal;

/**
 * Concrete Employee implementation for Independent Contractors.
 * All monetary values use BigDecimal to avoid floating-point errors.
 */
public class ContractorEmployee extends Employee {
    private static final BigDecimal SERVICE_FEE_RATE = new BigDecimal("0.02");

    private BigDecimal hourlyRate;
    private BigDecimal hoursWorked;

    public ContractorEmployee(int id, String name, String email, String department, String bankAccountNumber,
                              BigDecimal hourlyRate, BigDecimal hoursWorked) {
        super(id, name, email, department, bankAccountNumber);
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
        return BigDecimal.ZERO; // Taxes handled independently by contractors
    }

    @Override
    public BigDecimal calculateDeductions() {
        return calculateGrossPay().multiply(SERVICE_FEE_RATE);
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
