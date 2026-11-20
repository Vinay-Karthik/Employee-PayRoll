package integration;

import model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adapter Pattern: Adapts {@link ExternalHRService} to the domain {@link EmployeeDirectory}.
 */
public class ExternalHRAdapter implements EmployeeDirectory {
    private final ExternalHRService hrService;

    public ExternalHRAdapter(ExternalHRService hrService) {
        this.hrService = Objects.requireNonNull(hrService, "ExternalHRService must not be null");
    }

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
            return Optional.empty();
        } catch (ExternalServiceException e) {
            throw new EmployeeDirectoryException("External HR service failure while retrieving employee ID " + id, e);
        }
    }

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

    private int parseInternalId(String externalId) {
        if (externalId == null || !externalId.matches("^HR-[1-9]\\d*$")) {
            throw new IllegalArgumentException("Invalid external ID format: " + externalId);
        }
        return Integer.parseInt(externalId.substring(3));
    }
}
