package calculator;

import model.EmployeeType;

/**
 * Concrete Creator for Permanent employees (Factory Method Pattern).
 */
public class PermanentSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new PermanentSalaryCalculator();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.PERMANENT;
    }
}
