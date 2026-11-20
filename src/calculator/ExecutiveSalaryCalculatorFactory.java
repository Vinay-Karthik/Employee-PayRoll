package calculator;

import model.EmployeeType;

/**
 * Concrete Creator for Executive employees (Factory Method Pattern).
 */
public class ExecutiveSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new ExecutiveSalaryCalculator();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.EXECUTIVE;
    }
}
