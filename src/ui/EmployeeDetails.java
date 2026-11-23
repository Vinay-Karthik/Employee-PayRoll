package ui;

import tax.TaxRegime;

/**
 * Common employee fields collected by PayrollCLI before type-specific prompting.
 */
public record EmployeeDetails(int id, String name, String email, String department,
                              String bankAccountNumber, TaxRegime taxRegime) {
}
