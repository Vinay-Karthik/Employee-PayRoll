package structural.adapter;

import model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Target Interface (GoF Adapter Pattern).
 *
 * Defines the standard domain contract expected by the Payroll Service
 * for both employee retrieval (reads) and employee management (writes),
 * guaranteeing read/write consistency regardless of the underlying data source
 * (e.g. ExternalHRAdapter vs RepositoryEmployeeDirectory).
 */
public interface EmployeeDirectory {

    /**
     * Finds an employee by their internal integer ID.
     *
     * @param id internal employee ID
     * @return Optional containing the Employee if found and active, empty otherwise
     * @throws EmployeeDirectoryException if external directory communication fails
     */
    Optional<Employee> findEmployeeById(int id);

    /**
     * Retrieves all active employees available in the directory.
     *
     * @return list of Employee domain objects
     * @throws EmployeeDirectoryException if external directory communication fails
     */
    List<Employee> findAllEmployees();

    /**
     * Persists or updates an employee in the directory.
     *
     * @param employee the employee to save
     * @throws EmployeeDirectoryException if persistence fails
     */
    void saveEmployee(Employee employee);

    /**
     * Deletes an employee from the directory by ID.
     *
     * @param id internal employee ID
     * @return true if the employee was found and deleted, false otherwise
     * @throws EmployeeDirectoryException if deletion fails
     */
    boolean deleteEmployee(int id);

    /**
     * Checks if an employee exists and is active.
     *
     * @param id internal employee ID
     * @return true if employee exists and is active
     */
    default boolean containsEmployee(int id) {
        return findEmployeeById(id).isPresent();
    }
}
