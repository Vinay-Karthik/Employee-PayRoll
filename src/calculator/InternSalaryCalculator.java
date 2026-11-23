package calculator;

import model.EmployeeType;
import model.GrossPayCalculator;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Intern employees: Monthly stipend.
 * Delegates to {@link GrossPayCalculator} — the single source of truth shared
 * with {@link model.InternEmployee#calculateGrossPay()}.
 */
public class InternSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        return GrossPayCalculator.intern(
                components.getStipend().signum() > 0 ? components.getStipend() : components.getBasic());
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.INTERN;
    }
}
