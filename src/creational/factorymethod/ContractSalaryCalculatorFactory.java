package creational.factorymethod;

/**
 * Concrete Creator for Contract employees.
 *
 * Implements the GoF Factory Method {@link #createCalculator()}
 * to instantiate a {@link ContractSalaryCalculator} product.
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
