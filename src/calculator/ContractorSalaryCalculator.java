package calculator;

import model.EmployeeType;
import model.GrossPayCalculator;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Contractor employees: Fixed contract amount or Billed hours * Contractor rate.
 * Delegates to {@link GrossPayCalculator} — the single source of truth shared
 * with {@link model.ContractorEmployee#calculateGrossPay()}.
 */
public class ContractorSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        BigDecimal rate = components.getHourlyRate().signum() > 0 ? components.getHourlyRate() : components.getBasic();
        return GrossPayCalculator.contractor(components.getFixedContractAmount(), rate, components.getHoursWorked());
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.CONTRACTOR;
    }
}
