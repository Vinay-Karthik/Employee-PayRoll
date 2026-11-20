package creational.factorymethod;

/**
 * Concrete Creator for Permanent employees.
 *
 * Implements the GoF Factory Method {@link #createCalculator()}
 * to instantiate a {@link PermanentSalaryCalculator} product.
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
