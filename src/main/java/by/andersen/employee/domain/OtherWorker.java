package by.andersen.employee.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "other_workers")
public class OtherWorker extends Employee{
    private String description;
}
