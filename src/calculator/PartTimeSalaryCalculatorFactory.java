package calculator;

public class PartTimeSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new PartTimeSalaryCalculator();
    }
}
