package tax.policy;

import model.entity.Employee;
import model.entity.EmployeeType;

import java.math.BigDecimal;
import java.util.Objects;

import tax.impl.FullTimeTaxPolicy;
import tax.impl.PartTimeTaxPolicy;
import tax.impl.ContractorTaxPolicy;
import tax.impl.InternTaxPolicy;
import tax.regime.TaxRegime;

/**
 * Strategy interface for tax calculation policies.
 * Encapsulates tax rules for each employee category and supports
 * tax regime selection (OLD vs NEW).
 */
public interface TaxPolicy {

    /**
     * Calculates tax for the given gross amount using default (NEW) regime.
     *
     * @param gross the gross salary amount to calculate tax on
     * @return calculated tax amount
     */
    BigDecimal calculateTax(BigDecimal gross);

    /**
     * Calculates tax for the given gross amount respecting the specified tax regime.
     *
     * @param gross the gross salary amount to calculate tax on
     * @param regime the tax regime (OLD or NEW)
     * @return calculated tax amount
     */
    default BigDecimal calculateTax(BigDecimal gross, TaxRegime regime) {
        return calculateTax(gross);
    }

    /**
     * Returns the tax policy for the given employee.
     *
     * @param employee the employee
     * @return the corresponding tax policy
     */
    static TaxPolicy forEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee must not be null");
        return forEmployeeType(employee.getEmployeeType());
    }

    /**
     * Returns the tax policy for the given unified employee type category.
     *
     * @param employeeType the employee type category
     * @return the corresponding tax policy
     */
    static TaxPolicy forEmployeeType(EmployeeType employeeType) {
        Objects.requireNonNull(employeeType, "Employee type must not be null");
        return switch (employeeType) {
            case FULL_TIME -> FullTimeTaxPolicy.INSTANCE;
            case PART_TIME -> PartTimeTaxPolicy.INSTANCE;
            case CONTRACTOR -> ContractorTaxPolicy.INSTANCE;
            case INTERN -> InternTaxPolicy.INSTANCE;
        };
    }
}
