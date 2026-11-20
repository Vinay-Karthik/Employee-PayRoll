package creational.abstractfactory;

/**
 * New tax regime deduction calculator: flat standard deduction,
 * since most exemptions are unavailable under the new regime.
 */
public class NewDeductionCalculator implements DeductionCalculator {
    private static final double STANDARD_DEDUCTION = 50000.0;

    @Override
    public double calculateDeductions(double grossPay) {
        return Math.min(STANDARD_DEDUCTION, grossPay);
    }

    @Override
    public TaxRegime getRegime() {
        return TaxRegime.NEW;
    }
}
