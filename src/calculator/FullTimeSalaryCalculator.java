package calculator;

import model.EmployeeType;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Full-time salaried employees: Basic + Bonus.
 * Matches {@link model.FullTimeEmployee#calculateGrossPay()} for consistency.
 */
public class FullTimeSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        return components.getBasic()
                .add(components.getBonus());
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.FULL_TIME;
    }
}
