package calculator.factory;

import calculator.core.SalaryCalculator;
import calculator.impl.FullTimeSalaryCalculator;

public class FullTimeSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new FullTimeSalaryCalculator();
    }
}
