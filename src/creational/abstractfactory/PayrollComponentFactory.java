package creational.abstractfactory;

/**
 * Task 3: Abstract Factory Pattern - Abstract Factory.
 *
 * Declares the interface for creating families of related payroll
 * components (tax + deduction calculators) that are compatible
 * with a single tax regime.
 */
public abstract class PayrollComponentFactory {

    /**
     * Creates a tax calculator compatible with this factory's regime.
     */
    public abstract TaxCalculator createTaxCalculator();

    /**
     * Creates a deduction calculator compatible with this factory's regime.
     */
    public abstract DeductionCalculator createDeductionCalculator();

    /**
     * Returns the tax regime this factory produces components for.
     */
    public abstract TaxRegime getRegime();
}
