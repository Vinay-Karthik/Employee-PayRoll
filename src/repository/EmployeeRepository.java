package repository;

import model.entity.Employee;
import java.util.List;
import java.util.Optional;

/**
 * Interface Segregation & Dependency Inversion:
 * Abstracts all persistence operations away from high-level domain services.
 */
public interface EmployeeRepository {
    List<Employee> findAll();
    Optional<Employee> findById(int id);
    void save(Employee employee);
    boolean deleteById(int id);
}
