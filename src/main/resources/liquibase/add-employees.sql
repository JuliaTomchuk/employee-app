--liquibase formatted sql
--changeset Julia_Tomchuk:insert-employees

INSERT INTO employees (birth_date, hire_date, email, first_name, last_name, patronymic, version, created_by, modified_by, created_date, modified_date)
VALUES
    ('1985-01-15', '2020-02-20', 'john.smith@example.com', 'John', 'Smith', 'Michaelovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1990-03-22', '2019-04-01', 'emma.jones@example.com', 'Emma', 'Jones', 'Petrovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1995-05-15', '2021-06-10', 'michael.brown@example.com', 'Michael', 'Brown', 'Alexeevich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1987-09-20', '2018-01-15', 'olivia.martin@example.com', 'Olivia', 'Martin', 'Sergeevna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1992-11-12', '2022-01-25', 'william.miller@example.com', 'William', 'Miller', 'Vladislavovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1993-02-18', '2017-08-12', 'sophia.davis@example.com', 'Sophia', 'Davis', 'Ivanovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1991-03-25', '2016-05-09', 'liam.jackson@example.com', 'Liam', 'Jackson', 'Nikolaevich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1996-12-12', '2020-10-15', 'ava.wilson@example.com', 'Ava', 'Wilson', 'Alexandrovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1989-07-23', '2015-03-21', 'lucas.moore@example.com', 'Lucas', 'Moore', 'Pavlovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1994-09-14', '2019-12-05', 'mia.taylor@example.com', 'Mia', 'Taylor', 'Andreevna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1998-05-30', '2021-09-01', 'noah.thomas@example.com', 'Noah', 'Thomas', 'Romanovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1997-11-22', '2017-01-10', 'isabella.harris@example.com', 'Isabella', 'Harris', 'Gennadievna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1992-06-18', '2014-11-23', 'logan.clark@example.com', 'Logan', 'Clark', 'Stanislavovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1999-04-05', '2023-02-16', 'amelia.lewis@example.com', 'Amelia', 'Lewis', 'Viktorovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1984-10-28', '2009-07-19', 'mason.robinson@example.com', 'Mason', 'Robinson', 'Leonidovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1990-08-17', '2018-08-30', 'ella.walker@example.com', 'Ella', 'Walker', 'Yurievna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1995-03-13', '2021-04-25', 'james.young@example.com', 'James', 'Young', 'Antonovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1991-09-09', '2016-03-20', 'scarlett.hall@example.com', 'Scarlett', 'Hall', 'Vitalievna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1986-12-01', '2010-06-12', 'benjamin.allen@example.com', 'Benjamin', 'Allen', 'Olegovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1993-10-25', '2019-05-14', 'grace.king@example.com', 'Grace', 'King', 'Maximovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1992-02-15', '2018-07-27', 'henry.scott@example.com', 'Henry', 'Scott', 'Igorevich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1988-07-05', '2015-09-10', 'sofia.green@example.com', 'Sofia', 'Green', 'Dmitrievna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1994-03-03', '2020-02-11', 'jack.evans@example.com', 'Jack', 'Evans', 'Artemovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1996-01-28', '2017-06-17', 'madison.baker@example.com', 'Madison', 'Baker', 'Valerievna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1987-11-15', '2009-10-10', 'alexander.carter@example.com', 'Alexander', 'Carter', 'Fedorovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1999-08-12', '2022-11-08', 'victoria.rivera@example.com', 'Victoria', 'Rivera', 'Semenovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1998-03-21', '2020-05-01', 'daniel.ortiz@example.com', 'Daniel', 'Ortiz', 'Konstantinovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1997-07-30', '2021-03-03', 'zoe.diaz@example.com', 'Zoe', 'Diaz', 'Sergeevna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1985-09-19', '2012-02-06', 'leo.hill@example.com', 'Leo', 'Hill', 'Nikitovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1993-06-24', '2016-09-20', 'natalie.reed@example.com', 'Natalie', 'Reed', 'Vladislavovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1994-12-09', '2021-08-14', 'lucy.rogers@example.com', 'Lucy', 'Rogers', 'Alexeevna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1991-05-27', '2017-12-11', 'theo.cook@example.com', 'Theo', 'Cook', 'Timurovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1996-02-04', '2023-01-30', 'hannah.morris@example.com', 'Hannah', 'Morris', 'Petrovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1989-04-17', '2015-04-29', 'julian.flores@example.com', 'Julian', 'Flores', 'Borisovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1990-10-31', '2016-06-10', 'lily.bennett@example.com', 'Lily', 'Bennett', 'Ivanovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1993-08-03', '2020-04-18', 'nathan.collins@example.com', 'Nathan', 'Collins', 'Olegovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1997-11-14', '2021-12-21', 'amelie.mitchell@example.com', 'Amelie', 'Mitchell', 'Andreevna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1999-07-18', '2023-03-12', 'isaac.perry@example.com', 'Isaac', 'Perry', 'Alexandrovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1988-10-07', '2014-05-09', 'lila.campbell@example.com', 'Lila', 'Campbell', 'Sergeevna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1992-01-11', '2018-03-06', 'joshua.kelly@example.com', 'Joshua', 'Kelly', 'Viktorovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1995-09-22', '2019-09-15', 'eva.cooper@example.com', 'Eva', 'Cooper', 'Yurievna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1996-10-20', '2020-02-01', 'andrew.kingston@example.com', 'Andrew', 'Kingston', 'Vladimirovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1989-02-16', '2017-07-10', 'elena.wells@example.com', 'Elena', 'Wells', 'Mikhailovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1990-12-05', '2015-10-27', 'samuel.harrison@example.com', 'Samuel', 'Harrison', 'Evgenievich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1983-04-14', '2008-03-19', 'charlotte.morris@example.com', 'Charlotte', 'Morris', 'Igorevna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1991-08-30', '2019-07-18', 'alexander.rogers@example.com', 'Alexander', 'Rogers', 'Olegovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1990-06-07', '2017-09-14', 'peter.walker@example.com', 'Peter', 'Walker', 'Dmitrievich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1995-11-21', '2020-12-10', 'grace.smith@example.com', 'Grace', 'Smith', 'Vladislavovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1997-04-13', '2022-03-05', 'lucas.davis@example.com', 'Lucas', 'Davis', 'Maksimovich', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1986-12-05', '2015-05-20', 'maria.white@example.com', 'Maria', 'White', 'Petrovna', 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO managers (id)
VALUES (1), (2), (3), (4), (5), (6), (7), (8), (9), (10);

INSERT INTO workers (id)
VALUES (11), (12), (13), (14), (15),(16), (17), (18), (19), (20), (21), (22), (23), (24), (25)
,(26), (27), (28), (29), (30),(31), (32), (33), (34), (35), (36), (37), (38), (39), (40);

INSERT INTO other_workers (id, description)
VALUES (41, 'Receptionist'), (42, 'QA Specialist'), (43, 'Marketing Coordinator'), (44, 'Business Analyst'),
       (45, 'Technical Writer'), (46, 'Consultant'), (47, 'Training Coordinator'), (48, 'Operations Supervisor'),
       (49, 'Project Manager'), (50, 'Team Lead');

UPDATE employees SET manager_id = 1 WHERE id = 2;
UPDATE employees SET manager_id = 1 WHERE id = 3;
UPDATE employees SET manager_id = 1 WHERE id = 4;
UPDATE employees SET manager_id = 1 WHERE id = 5;
UPDATE employees SET manager_id = 1 WHERE id = 6;
UPDATE employees SET manager_id = 1 WHERE id = 7;
UPDATE employees SET manager_id = 1 WHERE id = 8;
UPDATE employees SET manager_id = 1 WHERE id = 9;
UPDATE employees SET manager_id = 1 WHERE id = 10;

UPDATE employees SET manager_id = 2 WHERE id = 11;
UPDATE employees SET manager_id = 2 WHERE id = 12;
UPDATE employees SET manager_id = 2 WHERE id = 13;
UPDATE employees SET manager_id = 2 WHERE id = 14;
UPDATE employees SET manager_id = 2 WHERE id = 15;

UPDATE employees SET manager_id = 3 WHERE id = 16;
UPDATE employees SET manager_id = 3 WHERE id = 17;
UPDATE employees SET manager_id = 3 WHERE id = 18;
UPDATE employees SET manager_id = 3 WHERE id = 19;
UPDATE employees SET manager_id = 3 WHERE id = 20;

UPDATE employees SET manager_id = 4 WHERE id = 21;
UPDATE employees SET manager_id = 4 WHERE id = 22;
UPDATE employees SET manager_id = 4 WHERE id = 23;
UPDATE employees SET manager_id = 4 WHERE id = 24;

UPDATE employees SET manager_id = 5 WHERE id = 25;
UPDATE employees SET manager_id = 5 WHERE id = 26;

UPDATE employees SET manager_id = 6 WHERE id = 27;
UPDATE employees SET manager_id = 6 WHERE id = 28;
UPDATE employees SET manager_id = 6 WHERE id = 29;
UPDATE employees SET manager_id = 6 WHERE id = 30;
UPDATE employees SET manager_id = 6 WHERE id = 31;

UPDATE employees SET manager_id = 7 WHERE id = 32;
UPDATE employees SET manager_id = 7 WHERE id = 33;
UPDATE employees SET manager_id = 7 WHERE id = 34;
UPDATE employees SET manager_id = 7 WHERE id = 35;
UPDATE employees SET manager_id = 7 WHERE id = 36;

UPDATE employees SET manager_id = 8 WHERE id = 37;
UPDATE employees SET manager_id = 8 WHERE id = 38;
UPDATE employees SET manager_id = 8 WHERE id = 39;
UPDATE employees SET manager_id = 8 WHERE id = 40;
UPDATE employees SET manager_id = 8 WHERE id = 41;
UPDATE employees SET manager_id = 8 WHERE id = 42;

UPDATE employees SET manager_id = 9 WHERE id = 43;
UPDATE employees SET manager_id = 9 WHERE id = 44;
UPDATE employees SET manager_id = 9 WHERE id = 45;
UPDATE employees SET manager_id = 9 WHERE id = 46;

UPDATE employees SET manager_id = 10 WHERE id = 47;
UPDATE employees SET manager_id = 10 WHERE id = 48;
UPDATE employees SET manager_id = 10 WHERE id = 49;
UPDATE employees SET manager_id = 10 WHERE id = 50;