package by.andersen.employee.enums;

import lombok.Getter;

@Getter
public enum EmployeeType {
    WORKER("worker"),
    OTHER_WORKER("otherWorker"),
    MANAGER("manager");

    private final String value;

    EmployeeType(String value) {
        this.value = value;
    }
}
