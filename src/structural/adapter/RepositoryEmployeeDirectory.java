package structural.adapter;

import model.Employee;
import repository.EmployeeRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Alternate implementation of the {@link EmployeeDirectory} Target interface.
 *
 * Demonstrates that {@link service.PayrollService} can use multiple different
 * directory sources (e.g. Local Database / Repository vs External HR API)
 * without any code changes to the payroll business logic.
 */
public class RepositoryEmployeeDirectory implements EmployeeDirectory {
    private final EmployeeRepository repository;

    public RepositoryEmployeeDirectory(EmployeeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "EmployeeRepository must not be null");
    }

    @Override
    public Optional<Employee> findEmployeeById(int id) {
        return repository.findById(id);
    }

    @Override
    public List<Employee> findAllEmployees() {
        return repository.findAll();
    }

    @Override
    public void saveEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        repository.save(employee);
    }

    @Override
    public boolean deleteEmployee(int id) {
        return repository.deleteById(id);
    }
}
