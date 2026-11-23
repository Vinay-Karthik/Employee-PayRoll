package calculator.factory;

import calculator.core.SalaryCalculator;
import calculator.impl.ContractorSalaryCalculator;

public class ContractorSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new ContractorSalaryCalculator();
    }
}
