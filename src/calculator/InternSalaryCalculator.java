package calculator;

import model.EmployeeType;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Intern employees: Monthly stipend.
 */
public class InternSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        return components.getStipend().signum() > 0 ? components.getStipend() : components.getBasic();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.INTERN;
    }
}
