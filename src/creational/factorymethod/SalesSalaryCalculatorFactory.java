package creational.factorymethod;

/**
 * Concrete Creator for Sales employees.
 *
 * Implements the GoF Factory Method {@link #createCalculator()}
 * to instantiate a {@link SalesSalaryCalculator} product.
 */
public class SalesSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new SalesSalaryCalculator();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.SALES;
    }
}
