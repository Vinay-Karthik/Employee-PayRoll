package calculator;

public class FullTimeSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new FullTimeSalaryCalculator();
    }
}
