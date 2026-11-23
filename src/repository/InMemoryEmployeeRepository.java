package repository;

import model.entity.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * In-memory implementation of EmployeeRepository.
 * Employees are hardcoded directly in the repository.
 * All monetary values use BigDecimal.
 */
public class InMemoryEmployeeRepository implements EmployeeRepository {
    private final Map<Integer, Employee> employees = new LinkedHashMap<>();

    public InMemoryEmployeeRepository() {
        seedEmployees();
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    private void seedEmployees() {
        save(new FullTimeEmployee(101, "Alice Johnson", "alice@company.com", "Engineering", "US-112233",
                bd("6500.00"), bd("800.00")));
        save(new PartTimeEmployee(102, "Bob Smith", "bob@company.com", "Operations", "US-445566",
                bd("35.00"), bd("48.00")));
        save(new ContractorEmployee(103, "Charlie Davis", "charlie@company.com", "IT Consulting", "US-778899",
                bd("75.00"), bd("60.00")));
        save(new InternEmployee(104, "Diana Prince", "diana@company.com", "Marketing", "US-990011",
                bd("2500.00")));
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }

    @Override
    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(employees.get(id));
    }

    @Override
    public void save(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        employees.put(employee.getId(), employee);
    }

    @Override
    public boolean deleteById(int id) {
        return employees.remove(id) != null;
    }
}
