package creational.factorymethod;

/**
 * Executive employees: Fixed salary + Bonus + Benefits.
 */
public class ExecutiveSalaryCalculator implements SalaryCalculator {

    @Override
    public double calculateGross(SalaryComponents components) {
        return components.getFixedSalary() + components.getBonus() + components.getBenefits();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.EXECUTIVE;
    }
}
