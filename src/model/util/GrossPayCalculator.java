package model.util;

import java.math.BigDecimal;

/**
 * Single source of truth for gross pay (pre-tax) business rules.
 * <p>
 * Both the Employee subclasses ({@link FullTimeEmployee}, {@link PartTimeEmployee},
 * {@link ContractorEmployee}, {@link InternEmployee}) and the SalaryCalculator
 * implementations in the {@code calculator} package delegate here so the rules
 * are maintained in exactly one place. The {@code model} package never depends
 * on the {@code calculator} package.
 * <p>
 * All monetary values use BigDecimal to avoid floating-point errors.
 */
public final class GrossPayCalculator {

    private static final BigDecimal REGULAR_HOURS_THRESHOLD = new BigDecimal("40.00");
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.50");

    private GrossPayCalculator() {
        // Utility class - no instantiation
    }

    /**
     * Full-time salaried: base salary + bonus.
     */
    public static BigDecimal fullTime(BigDecimal baseSalary, BigDecimal bonus) {
        return baseSalary.add(bonus);
    }

    /**
     * Part-time hourly: hourly rate * hours worked,
     * with overtime (hours &gt; 40) paid at 1.5x.
     */
    public static BigDecimal partTime(BigDecimal hourlyRate, BigDecimal hoursWorked) {
        if (hoursWorked.compareTo(REGULAR_HOURS_THRESHOLD) <= 0) {
            return hourlyRate.multiply(hoursWorked);
        }
        BigDecimal regularPay = hourlyRate.multiply(REGULAR_HOURS_THRESHOLD);
        BigDecimal overtimeHours = hoursWorked.subtract(REGULAR_HOURS_THRESHOLD);
        BigDecimal overtimePay = overtimeHours.multiply(hourlyRate).multiply(OVERTIME_MULTIPLIER);
        return regularPay.add(overtimePay);
    }

    /**
     * Contractor: fixed contract amount when present (positive),
     * otherwise hourly rate * hours worked.
     */
    public static BigDecimal contractor(BigDecimal fixedContractAmount, BigDecimal hourlyRate, BigDecimal hoursWorked) {
        if (fixedContractAmount != null && fixedContractAmount.signum() > 0) {
            return fixedContractAmount;
        }
        return hourlyRate.multiply(hoursWorked);
    }

    /**
     * Intern: monthly stipend.
     */
    public static BigDecimal intern(BigDecimal stipend) {
        return stipend;
    }
}
