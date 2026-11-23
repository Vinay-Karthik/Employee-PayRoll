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
 */
public class PartTimeEmployee extends Employee {
    private static final BigDecimal REGULAR_HOURS_THRESHOLD = new BigDecimal("40.00");
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");

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
    public int getTypeCode() {
        return 2;
    }

    @Override
    public String getPayDetailsSummary() {
        return String.format("$%.2f/hr x %.1f hrs", hourlyRate, hoursWorked);
    }
}
