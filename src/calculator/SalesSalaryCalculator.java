package calculator;

import model.EmployeeType;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Sales employees: Fixed salary + Commission.
 */
public class SalesSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        return components.getFixedSalary().add(components.getCommission());
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.SALES;
    }
}
