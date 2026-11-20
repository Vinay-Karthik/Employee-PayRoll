package creational.factorymethod;

/**
 * Sales employees: Fixed salary + Commission.
 */
public class SalesSalaryCalculator implements SalaryCalculator {

    @Override
    public double calculateGross(SalaryComponents components) {
        return components.getFixedSalary() + components.getCommission();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.SALES;
    }
}
