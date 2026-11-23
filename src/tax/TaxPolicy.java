package tax;

import model.Employee;
import model.EmployeeType;

import java.math.BigDecimal;
import java.util.Objects;

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
     * Returns the employee type code this policy applies to.
     */
    int getTypeCode();

    /**
     * Returns the tax policy for the given employee type code.
     *
     * @param typeCode the employee type code (1=Full-Time, 2=Part-Time, 3=Contractor, 4=Intern)
     * @return the corresponding tax policy
     * @throws IllegalArgumentException if no policy exists for the type code
     */
    static TaxPolicy forTypeCode(int typeCode) {
        return switch (typeCode) {
            case 1 -> FullTimeTaxPolicy.INSTANCE;
            case 2 -> PartTimeTaxPolicy.INSTANCE;
            case 3 -> ContractorTaxPolicy.INSTANCE;
            case 4 -> InternTaxPolicy.INSTANCE;
            default -> throw new IllegalArgumentException("Unknown employee type code: " + typeCode);
        };
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
