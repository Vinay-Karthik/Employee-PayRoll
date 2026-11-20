package creational.factorymethod;

/**
 * Concrete Creator for Executive employees.
 *
 * Implements the GoF Factory Method {@link #createCalculator()}
 * to instantiate an {@link ExecutiveSalaryCalculator} product.
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
