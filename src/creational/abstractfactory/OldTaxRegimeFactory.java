package creational.abstractfactory;

/**
 * Old tax regime factory: produces the Old-regime family of
 * compatible tax and deduction components.
 */
public class OldTaxRegimeFactory extends PayrollComponentFactory {

    @Override
    public TaxCalculator createTaxCalculator() {
        return new OldTaxCalculator();
    }

    @Override
    public DeductionCalculator createDeductionCalculator() {
        return new OldDeductionCalculator();
    }

    @Override
    public TaxRegime getRegime() {
        return TaxRegime.OLD;
    }
}
