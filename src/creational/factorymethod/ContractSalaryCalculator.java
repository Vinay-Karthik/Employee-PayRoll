package creational.factorymethod;

/**
 * Contract employees: Fixed contract amount.
 */
public class ContractSalaryCalculator implements SalaryCalculator {

    @Override
    public double calculateGross(SalaryComponents components) {
        return components.getFixedContractAmount();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.CONTRACT;
    }
}
