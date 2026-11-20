package creational.abstractfactory;

/**
 * Old tax regime deduction calculator: Provident Fund at 12% of gross.
 */
public class OldDeductionCalculator implements DeductionCalculator {
    private static final double PF_RATE = 0.12;

    @Override
    public double calculateDeductions(double grossPay) {
        return grossPay * PF_RATE;
    }

    @Override
    public TaxRegime getRegime() {
        return TaxRegime.OLD;
    }
}
