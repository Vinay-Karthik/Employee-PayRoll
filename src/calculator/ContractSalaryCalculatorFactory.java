package calculator;

import model.EmployeeType;

/**
 * Concrete Creator for Contract employees (Factory Method Pattern).
 */
public class ContractSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new ContractSalaryCalculator();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.CONTRACT;
    }
}
