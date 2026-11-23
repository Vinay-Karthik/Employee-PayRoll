package calculator;

public class InternSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new InternSalaryCalculator();
    }
}
