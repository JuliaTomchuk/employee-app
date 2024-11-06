--liquibase formatted sql
--changeset Julia_Tomchuk:add-tables


-- Create the employees table with information about employees
CREATE TABLE employees (
                           birth_date TIMESTAMP WITH TIME ZONE,  -- Employee's birth date
                           hire_date TIMESTAMP WITH TIME ZONE,   -- Employee's hire date
                           id BIGSERIAL NOT NULL,                      -- Unique employee ID
                           manager_id BIGINT,                       -- ID of the employee's manager
                           email VARCHAR(255) NOT NULL UNIQUE,      -- Employee's email, must be unique
                           first_name VARCHAR(255),                 -- Employee's first name
                           last_name VARCHAR(255),                  -- Employee's last name
                           patronymic VARCHAR(255),                 -- Employee's patronymic
                           PRIMARY KEY (id)                         -- Primary key constraint on employee ID
);

-- Create the managers table with information about managers
CREATE TABLE managers (
                          id BIGSERIAL NOT NULL,                      -- Unique manager ID
                          PRIMARY KEY (id)                         -- Primary key constraint on manager ID
);

-- Create the other_workers table for other types of employees (e.g., supervisors, secretaries)
CREATE TABLE other_workers (
                               id BIGSERIAL NOT NULL,                      -- Unique ID for other workers
                               description VARCHAR(255),                -- Description of the worker's role or position
                               PRIMARY KEY (id)                         -- Primary key constraint on ID
);

-- Create the workers table for standard employees
CREATE TABLE workers (
                         id BIGSERIAL NOT NULL,                      -- Unique worker ID
                         PRIMARY KEY (id)                         -- Primary key constraint on worker ID
);
alter table if exists employees add constraint employees_id_fk foreign key (manager_id) references managers;
alter table if exists managers add constraint manager_id_fk foreign key (id) references employees;
alter table if exists other_workers add constraint other_workers_id_fk foreign key (id) references employees;
alter table if exists workers add constraint workers_id_fk foreign key (id) references employees;