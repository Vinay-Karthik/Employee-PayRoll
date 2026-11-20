package calculator;

import model.EmployeeType;

/**
 * Concrete Creator for Sales employees (Factory Method Pattern).
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
