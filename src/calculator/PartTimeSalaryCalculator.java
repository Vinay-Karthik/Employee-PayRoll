package calculator;

import model.EmployeeType;
import model.GrossPayCalculator;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Part-time hourly employees: Hourly rate * Hours worked with standard overtime rules
 * (hours > 40 paid at 1.5x overtime multiplier).
 * Delegates to {@link GrossPayCalculator} — the single source of truth shared
 * with {@link model.PartTimeEmployee#calculateGrossPay()}.
 */
public class PartTimeSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        BigDecimal hourlyRate = components.getHourlyRate().signum() > 0 ? components.getHourlyRate() : components.getBasic();
        return GrossPayCalculator.partTime(hourlyRate, components.getHoursWorked());
    }
}
