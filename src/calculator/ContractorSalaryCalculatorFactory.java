package calculator;

import model.EmployeeType;

public class ContractorSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new ContractorSalaryCalculator();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.CONTRACTOR;
    }
}
