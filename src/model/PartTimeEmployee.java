package model;

import java.math.BigDecimal;

/**
 * Concrete Employee implementation for Part-Time Hourly workers.
 * All monetary values use BigDecimal to avoid floating-point errors.
 */
public class PartTimeEmployee extends Employee {
    private static final BigDecimal REGULAR_HOURS_THRESHOLD = new BigDecimal("40.00");
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.12");

    private BigDecimal hourlyRate;
    private BigDecimal hoursWorked;

    public PartTimeEmployee(int id, String name, String email, String department, String bankAccountNumber,
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
        if (hoursWorked.compareTo(REGULAR_HOURS_THRESHOLD) > 0) {
            BigDecimal regularPay = REGULAR_HOURS_THRESHOLD.multiply(hourlyRate);
            BigDecimal overtimePay = hoursWorked.subtract(REGULAR_HOURS_THRESHOLD)
                    .multiply(hourlyRate.multiply(OVERTIME_MULTIPLIER));
            return regularPay.add(overtimePay);
        }
        return hoursWorked.multiply(hourlyRate);
    }

    @Override
    public BigDecimal calculateTax() {
        return calculateGrossPay().multiply(TAX_RATE);
    }

    @Override
    public BigDecimal calculateDeductions() {
        return BigDecimal.ZERO;
    }

    @Override
    public String getRoleCategory() {
        return "Part-Time Hourly";
    }

    @Override
    public int getTypeCode() {
        return 2;
    }

    @Override
    public String getPayDetailsSummary() {
        return String.format("$%.2f/hr x %.1f hrs", hourlyRate, hoursWorked);
    }
}
