package by.andersen.employee;

import by.andersen.employee.domain.Employee;
import by.andersen.employee.domain.Manager;
import by.andersen.employee.domain.OtherWorker;
import by.andersen.employee.domain.Worker;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TestDataGenerator {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PATRONYMIC = "patronymic";
    public static final String MANAGER_EMAIL = "managerEmail";
    public static final String BIRTH_DATE_FROM = "birthDateFrom";
    public static final String BIRTH_DATE_TO = "birthDateTo";
    public static final String HIRE_DATE_FROM = "hireDateFrom";
    public static final String HIRE_DATE_TO = "hireDateTo";


    public List<Employee> createEmployees() {
        Manager manager1 = new Manager();
        manager1.setFirstName("John");
        manager1.setLastName("Black");
        manager1.setPatronymic("John");
        manager1.setBirthDate(Instant.parse("1990-11-20T00:00:00Z"));
        manager1.setEmail("john.black@gmail.com");
        manager1.setHireDate(Instant.parse("2010-11-20T00:00:00Z"));
        manager1.setPatronymic("John");

        Manager manager2 = new Manager();
        manager2.setFirstName("Julia");
        manager2.setLastName("Tomchuk");
        manager2.setBirthDate(Instant.parse("1991-12-27T00:00:00Z"));
        manager2.setEmail("julia.tomchuk@gmail.com");
        manager2.setHireDate(Instant.parse("2010-11-20T00:00:00Z"));
        manager2.setPatronymic("Aleksandrovna");
        manager2.addSubordinate(manager1);

        Worker worker = new Worker();
        worker.setFirstName("Alex");
        worker.setLastName("Jonathon");
        worker.setBirthDate(Instant.parse("2001-12-27T00:00:00Z"));
        worker.setEmail("alex.jonathon@gmail.com");
        worker.setHireDate(Instant.parse("2021-07-12T00:00:00Z"));
        worker.setPatronymic("Aleksandrovich");
        manager1.addSubordinate(worker);

        OtherWorker otherWorker = new OtherWorker();
        otherWorker.setFirstName("Jan");
        otherWorker.setLastName("Doe");
        otherWorker.setBirthDate(Instant.parse("2001-12-27T00:00:00Z"));
        otherWorker.setEmail("jan.doe@gmail.com");
        otherWorker.setHireDate(Instant.parse("2021-07-12T00:00:00Z"));
        otherWorker.setPatronymic("Vladimirovich");
        otherWorker.setDescription("description");
        manager1.addSubordinate(otherWorker);
        return List.of(manager1, manager2, worker, otherWorker);
    }

    public List<Manager> createManagers() {

        Manager manager1 = new Manager();
        manager1.setFirstName("John");
        manager1.setLastName("Black");
        manager1.setPatronymic("John");
        manager1.setBirthDate(Instant.parse("1990-11-20T00:00:00Z"));
        manager1.setEmail("john.black@gmail.com");
        manager1.setHireDate(Instant.parse("2010-11-20T00:00:00Z"));
        manager1.setPatronymic("John");

        Manager manager2 = new Manager();
        manager2.setFirstName("Julia");
        manager2.setLastName("Tomchuk");
        manager2.setBirthDate(Instant.parse("1991-12-27T00:00:00Z"));
        manager2.setEmail("julia.tomchuk@gmail.com");
        manager2.setHireDate(Instant.parse("2010-11-20T00:00:00Z"));
        manager2.setPatronymic("Aleksandrovna");
        manager2.addSubordinate(manager1);

        Manager manager3 = new Manager();
        manager3.setFirstName("Matthew");
        manager3.setLastName("Bellamy");
        manager3.setBirthDate(Instant.parse("1978-06-09T00:00:00Z"));
        manager3.setEmail("matt.bellamy@gmail.com");
        manager3.setHireDate(Instant.parse("2020-11-20T00:00:00Z"));
        manager3.setPatronymic("James");

        return List.of(manager1, manager2, manager3);
    }

}
