package calculator;

import model.EmployeeType;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Executive employees: Fixed salary + Bonus + Benefits.
 */
public class ExecutiveSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        return components.getFixedSalary()
                .add(components.getBonus())
                .add(components.getBenefits());
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.EXECUTIVE;
    }
}
