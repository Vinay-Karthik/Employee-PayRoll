package calculator;

import model.EmployeeType;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Part-time hourly employees: Hourly rate * Hours worked with standard overtime rules
 * (hours > 40 paid at 1.5x overtime multiplier).
 */
public class PartTimeSalaryCalculator implements SalaryCalculator {
    private static final BigDecimal STANDARD_HOURS_LIMIT = new BigDecimal("40.00");
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.50");

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        BigDecimal hourlyRate = components.getHourlyRate().signum() > 0 ? components.getHourlyRate() : components.getBasic();
        BigDecimal hoursWorked = components.getHoursWorked();

        if (hoursWorked.compareTo(STANDARD_HOURS_LIMIT) <= 0) {
            return hourlyRate.multiply(hoursWorked);
        }
        BigDecimal regularPay = hourlyRate.multiply(STANDARD_HOURS_LIMIT);
        BigDecimal overtimeHours = hoursWorked.subtract(STANDARD_HOURS_LIMIT);
        BigDecimal overtimePay = overtimeHours.multiply(hourlyRate).multiply(OVERTIME_MULTIPLIER);
        return regularPay.add(overtimePay);
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.PART_TIME;
    }
}
