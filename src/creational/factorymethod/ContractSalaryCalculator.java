package creational.factorymethod;

import java.math.BigDecimal;

/**
 * Contract employees: Fixed contract amount.
 */
public class ContractSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        return components.getFixedContractAmount();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.CONTRACT;
    }
}
