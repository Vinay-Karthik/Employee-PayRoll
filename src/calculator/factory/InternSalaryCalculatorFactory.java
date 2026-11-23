package calculator.factory;

import calculator.core.SalaryCalculator;
import calculator.impl.InternSalaryCalculator;

public class InternSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new InternSalaryCalculator();
    }
}
