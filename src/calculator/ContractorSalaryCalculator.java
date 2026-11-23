package calculator;

import model.EmployeeType;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Contractor employees: Fixed contract amount or Billed hours * Contractor rate.
 */
public class ContractorSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        if (components.getFixedContractAmount().signum() > 0) {
            return components.getFixedContractAmount();
        }
        BigDecimal rate = components.getHourlyRate().signum() > 0 ? components.getHourlyRate() : components.getBasic();
        return rate.multiply(components.getHoursWorked());
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.CONTRACTOR;
    }
}
