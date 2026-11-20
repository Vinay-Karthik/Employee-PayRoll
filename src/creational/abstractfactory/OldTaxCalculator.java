package creational.abstractfactory;

/**
 * Old tax regime tax calculator: flat 20% rate with 4% surcharge
 * on income above the exemption threshold.
 */
public class OldTaxCalculator implements TaxCalculator {
    private static final double TAX_RATE = 0.20;
    private static final double SURCHARGE_THRESHOLD = 50000.0;
    private static final double SURCHARGE_RATE = 0.04;

    @Override
    public double calculateTax(double grossPay) {
        double tax = grossPay * TAX_RATE;
        if (grossPay > SURCHARGE_THRESHOLD) {
            tax += (grossPay - SURCHARGE_THRESHOLD) * SURCHARGE_RATE;
        }
        return tax;
    }

    @Override
    public TaxRegime getRegime() {
        return TaxRegime.OLD;
    }
}
