package calculator;

import model.EmployeeType;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Permanent employees: Basic + HRA + Allowances.
 */
public class PermanentSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        return components.getBasic().add(components.getHra()).add(components.getAllowances());
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.PERMANENT;
    }
}
