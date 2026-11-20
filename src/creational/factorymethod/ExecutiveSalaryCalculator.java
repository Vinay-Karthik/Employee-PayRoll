package creational.factorymethod;

import java.math.BigDecimal;

/**
 * Executive employees: Fixed salary + Bonus + Benefits.
 *
 * Salary calculation uses monetary BigDecimal components exclusively
 * (fixedSalary, bonus, and benefits from SalaryComponents).
 * Descriptive metadata such as bonusDetails (e.g., "Annual performance bonus")
 * is preserved on the PayrollRequest and PaySlip for reporting and audit,
 * and is never parsed or converted into numeric values.
 */
public class ExecutiveSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        return components.getFixedSalary()
                .add(components.getBonus())
                .add(components.getBenefits());
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.EXECUTIVE;
    }
}
