package repository;

import model.*;

import java.util.*;

/**
 * In-memory implementation of EmployeeRepository.
 * Employees are hardcoded directly in the repository.
 */
public class InMemoryEmployeeRepository implements EmployeeRepository {
    private final Map<Integer, Employee> employees = new LinkedHashMap<>();

    public InMemoryEmployeeRepository() {
        seedEmployees();
    }

    private void seedEmployees() {
        save(new FullTimeEmployee(101, "Alice Johnson", "alice@company.com", "Engineering", "US-112233", 6500.0, 800.0));
        save(new PartTimeEmployee(102, "Bob Smith", "bob@company.com", "Operations", "US-445566", 35.0, 48.0));
        save(new ContractorEmployee(103, "Charlie Davis", "charlie@company.com", "IT Consulting", "US-778899", 75.0, 60.0));
        save(new InternEmployee(104, "Diana Prince", "diana@company.com", "Marketing", "US-990011", 2500.0));
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
