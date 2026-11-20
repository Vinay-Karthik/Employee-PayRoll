package tax;

/**
 * New tax regime factory: produces the New-regime family of
 * compatible tax and deduction components.
 */
public class NewTaxRegimeFactory extends PayrollComponentFactory {

    @Override
    public TaxCalculator createTaxCalculator() {
        return new NewTaxCalculator();
    }

    @Override
    public DeductionCalculator createDeductionCalculator() {
        return new NewDeductionCalculator();
    }

    @Override
    public TaxRegime getRegime() {
        return TaxRegime.NEW;
    }
}
