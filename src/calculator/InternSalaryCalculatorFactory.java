package calculator;

import model.EmployeeType;

public class InternSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new InternSalaryCalculator();
    }

    @Override
    public EmployeeType getSupportedType() {
        return EmployeeType.INTERN;
    }
}
