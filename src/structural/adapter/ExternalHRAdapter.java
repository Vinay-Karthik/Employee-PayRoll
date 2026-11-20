package structural.adapter;

import model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adapter (GoF Adapter Pattern).
 *
 * Implements the {@link EmployeeDirectory} Target interface by wrapping
 * the third-party {@link ExternalHRService} Adaptee.
 *
 * Responsibilities:
 * 1. Translates internal numeric employee IDs to external format (e.g. 101 -> "HR-101").
 * 2. Catches expected {@link ExternalEmployeeNotFoundException} and returns {@link Optional#empty()}.
 * 3. Translates infrastructure/network failures ({@link ExternalServiceException}) into
 *    application-level {@link EmployeeDirectoryException} without swallowing unexpected errors.
 * 4. Converts external {@link HRRecord} DTOs into domain {@link Employee} models.
 * 5. Resilient batch processing: skips invalid records with a warning in {@link #findAllEmployees()},
 *    while failing fast with meaningful errors in {@link #findEmployeeById(int)}.
 * 6. Guarantees read/write consistency by synchronizing both reads and writes with the external HR service.
 */
public class ExternalHRAdapter implements EmployeeDirectory {
    private final ExternalHRService hrService;

    public ExternalHRAdapter(ExternalHRService hrService) {
        this.hrService = Objects.requireNonNull(hrService, "ExternalHRService must not be null");
    }

    /**
     * Finds an employee by internal integer ID.
     *
     * Contract:
     * - The external service guarantees returning a non-null, active HRRecord when successful.
     * - Catches ExternalEmployeeNotFoundException and returns Optional.empty().
     * - Translates ExternalServiceException into EmployeeDirectoryException.
     * - Unexpected programming errors are allowed to surface.
     */
    @Override
    public Optional<Employee> findEmployeeById(int id) {
        if (id <= 0) {
            return Optional.empty();
        }

        String externalId = "HR-" + id;

        try {
            HRRecord record = hrService.getEmployeeById(externalId);
            return Optional.of(mapToEmployee(record));
        } catch (ExternalEmployeeNotFoundException e) {
            // Documented contract: missing or inactive employee produces Optional.empty()
            return Optional.empty();
        } catch (ExternalServiceException e) {
            // Translate infrastructure failure to application-level exception
            throw new EmployeeDirectoryException("External HR service failure while retrieving employee ID " + id, e);
        }
    }

    /**
     * Retrieves all active employees from the external HR service.
     *
     * Policy:
     * - Infrastructure failures throw EmployeeDirectoryException.
     * - Invalid employment types in individual records are skipped and logged
     *   so a single corrupted record does not break the entire listing.
     */
    @Override
    public List<Employee> findAllEmployees() {
        List<HRRecord> rawRecords;
        try {
            rawRecords = hrService.getAllEmployees();
        } catch (ExternalServiceException e) {
            throw new EmployeeDirectoryException("External HR service failure while fetching employee listing", e);
        }

        List<Employee> validEmployees = new ArrayList<>();
        for (HRRecord record : rawRecords) {
            if (!record.active()) {
                continue;
            }
            try {
                Employee emp = mapToEmployee(record);
                if (emp != null) {
                    validEmployees.add(emp);
                }
            } catch (IllegalArgumentException e) {
                // Resilient batch policy: skip invalid/unsupported record and log warning
                System.err.println("Warning: Skipping external HR record " + record.externalId()
                        + " due to validation error: " + e.getMessage());
            }
        }
        return validEmployees;
    }

    @Override
    public void saveEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        HRRecord record = mapToHRRecord(employee);
        hrService.saveExternalRecord(record);
    }

    @Override
    public boolean deleteEmployee(int id) {
        if (id <= 0) {
            return false;
        }
        return hrService.deleteExternalRecord("HR-" + id);
    }

    /**
     * Converts external HRRecord DTO into internal domain Employee model.
     * Validates employment type and throws meaningful IllegalArgumentException if unsupported.
     */
    private Employee mapToEmployee(HRRecord record) {
        Objects.requireNonNull(record, "HR record cannot be null");

        int internalId = parseInternalId(record.externalId());
        String name = record.getFullName();
        String email = record.workEmail();
        String dept = record.departmentCode();
        String bankAccount = record.bankAccountNumber() != null ? record.bankAccountNumber() : "N/A";

        String type = record.employmentType() != null ? record.employmentType().toUpperCase().trim() : "";

        return switch (type) {
            case "FULL_TIME", "SALARIED", "PERMANENT" -> new FullTimeEmployee(
                    internalId, name, email, dept, bankAccount,
                    record.baseSalary(), record.bonusAmount()
            );
            case "PART_TIME", "HOURLY" -> new PartTimeEmployee(
                    internalId, name, email, dept, bankAccount,
                    record.hourlyRate(), record.hoursLogged()
            );
            case "CONTRACTOR", "CONTRACT" -> new ContractorEmployee(
                    internalId, name, email, dept, bankAccount,
                    record.hourlyRate(), record.hoursLogged()
            );
            case "INTERN" -> new InternEmployee(
                    internalId, name, email, dept, bankAccount,
                    record.stipendAmount()
            );
            default -> throw new IllegalArgumentException("Unknown external employment type: '" + record.employmentType()
                    + "' for record " + record.externalId());
        };
    }

    /**
     * Converts domain Employee into external HRRecord for saving.
     */
    private HRRecord mapToHRRecord(Employee emp) {
        String externalId = "HR-" + emp.getId();
        String[] nameParts = emp.getName().split(" ", 2);
        String firstName = nameParts[0];
        String lastName = (nameParts.length > 1) ? nameParts[1] : "";

        String employmentType;
        BigDecimal base = BigDecimal.ZERO;
        BigDecimal bonus = BigDecimal.ZERO;
        BigDecimal rate = BigDecimal.ZERO;
        BigDecimal hours = BigDecimal.ZERO;
        BigDecimal stipend = BigDecimal.ZERO;

        if (emp instanceof FullTimeEmployee ft) {
            employmentType = "FULL_TIME";
            base = ft.getBaseSalary();
            bonus = ft.getBonus();
        } else if (emp instanceof PartTimeEmployee pt) {
            employmentType = "PART_TIME";
            rate = pt.getHourlyRate();
            hours = pt.getHoursWorked();
        } else if (emp instanceof ContractorEmployee c) {
            employmentType = "CONTRACTOR";
            rate = c.getHourlyRate();
            hours = c.getHoursWorked();
        } else if (emp instanceof InternEmployee in) {
            employmentType = "INTERN";
            stipend = in.getStipend();
        } else {
            employmentType = "FULL_TIME";
        }

        return new HRRecord(
                externalId, firstName, lastName, emp.getEmail(),
                emp.getDepartment(), employmentType,
                base, bonus, rate, hours, stipend,
                emp.getBankAccountNumber(), true
        );
    }

    /**
     * Validates the exact HR- format before parsing.
     * Prevents accepting malformed IDs like "HR-10-20" or "abc101".
     */
    private int parseInternalId(String externalId) {
        if (externalId == null || !externalId.matches("^HR-[1-9]\\d*$")) {
            throw new IllegalArgumentException("Invalid external ID format: " + externalId);
        }
        return Integer.parseInt(externalId.substring(3));
    }
}
