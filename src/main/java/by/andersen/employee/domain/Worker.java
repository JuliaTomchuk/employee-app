package by.andersen.employee.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "workers")
public class Worker extends Employee {

}
