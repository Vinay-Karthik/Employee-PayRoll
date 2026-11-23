package integration;

import model.*;
import repository.EmployeeRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adapter Pattern: Adapts third-party {@link ExternalHRService} (Adaptee)
 * to the domain {@link EmployeeRepository} (Target interface).
 * <p>
 * Maintained as a clean Adapter demonstration showing integration
 * with an incompatible external system while keeping it separated
 * from the main payroll workflow.
 */
public class ExternalHRAdapter implements EmployeeRepository {
    private final ExternalHRService hrService;

    public ExternalHRAdapter(ExternalHRService hrService) {
        this.hrService = Objects.requireNonNull(hrService, "ExternalHRService must not be null");
    }

    @Override
    public Optional<Employee> findById(int id) {
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
            throw new RuntimeException("External HR service failure while retrieving employee ID " + id, e);
        }
    }

    @Override
    public List<Employee> findAll() {
        List<HRRecord> rawRecords;
        try {
            rawRecords = hrService.getAllEmployees();
        } catch (ExternalServiceException e) {
            throw new RuntimeException("External HR service failure while fetching employee listing", e);
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
    public void save(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        HRRecord record = mapToHRRecord(employee);
        hrService.saveExternalRecord(record);
    }

    @Override
    public boolean deleteById(int id) {
        if (id <= 0) {
            return false;
        }
        return hrService.deleteExternalRecord("HR-" + id);
    }

    // Convenience aliases for compatibility
    public Optional<Employee> findEmployeeById(int id) {
        return findById(id);
    }

    public List<Employee> findAllEmployees() {
        return findAll();
    }

    public void saveEmployee(Employee employee) {
        save(employee);
    }

    public boolean deleteEmployee(int id) {
        return deleteById(id);
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

    /**
     * Standalone demonstration of the Adapter Pattern:
     * Verifies that ExternalHRAdapter adapts third-party ExternalHRService
     * to the standard EmployeeRepository contract.
     */
    public static void main(String[] args) {
        ExternalHRService externalHRService = new ExternalHRService();
        EmployeeRepository adapter = new ExternalHRAdapter(externalHRService);

        System.out.println("=== External HR Adapter Demonstration ===");
        for (Employee emp : adapter.findAll()) {
            System.out.printf("  [%d] %s | %s | %s | %s%n",
                    emp.getId(), emp.getName(), emp.getDepartment(),
                    emp.getRoleCategory(), emp.getPayDetailsSummary());
        }
    }
}
