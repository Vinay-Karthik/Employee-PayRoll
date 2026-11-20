package creational.factorymethod;

/**
 * Permanent employees: Basic + HRA + Allowances.
 */
public class PermanentSalaryCalculator implements SalaryCalculator {

    @Override
    public double calculateGross(SalaryComponents components) {
        return components.getBasic() + components.getHra() + components.getAllowances();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.PERMANENT;
    }
}
