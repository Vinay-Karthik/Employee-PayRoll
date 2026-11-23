package calculator;

import model.EmployeeType;

public class PartTimeSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new PartTimeSalaryCalculator();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.PART_TIME;
    }
}
