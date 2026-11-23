package calculator.factory;

import calculator.core.SalaryCalculator;
import calculator.impl.PartTimeSalaryCalculator;

public class PartTimeSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new PartTimeSalaryCalculator();
    }
}
