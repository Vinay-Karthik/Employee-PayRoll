package calculator;

import model.EmployeeType;

public class FullTimeSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new FullTimeSalaryCalculator();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.FULL_TIME;
    }
}
