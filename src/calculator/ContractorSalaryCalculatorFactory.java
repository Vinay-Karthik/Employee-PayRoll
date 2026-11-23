package calculator;

public class ContractorSalaryCalculatorFactory extends SalaryCalculatorFactory {

    @Override
    public SalaryCalculator createCalculator() {
        return new ContractorSalaryCalculator();
    }
}
