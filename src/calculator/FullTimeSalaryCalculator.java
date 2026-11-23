package calculator;

import model.EmployeeType;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Full-time salaried employees: Basic + Bonus + Allowances + HRA.
 */
public class FullTimeSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        return components.getBasic()
                .add(components.getBonus())
                .add(components.getAllowances())
                .add(components.getHra());
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.FULL_TIME;
    }
}
