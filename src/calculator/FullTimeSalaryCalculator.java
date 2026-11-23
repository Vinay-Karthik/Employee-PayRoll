package calculator;

import model.GrossPayCalculator;
import model.SalaryComponents;

import java.math.BigDecimal;

/**
 * Full-time salaried employees: Basic + Bonus.
 * Delegates to {@link GrossPayCalculator} — the single source of truth shared
 * with {@link model.FullTimeEmployee#calculateGrossPay()}.
 */
public class FullTimeSalaryCalculator implements SalaryCalculator {

    @Override
    public BigDecimal calculateGross(SalaryComponents components) {
        return GrossPayCalculator.fullTime(components.getBasic(), components.getBonus());
    }
}
