package by.andersen.employee.controller;

import by.andersen.employee.TestDataGenerator;
import by.andersen.employee.domain.Employee;
import by.andersen.employee.domain.Manager;
import by.andersen.employee.domain.OtherWorker;
import by.andersen.employee.domain.Worker;
import by.andersen.employee.dto.employee.EmployeeFilterDto;
import by.andersen.employee.enums.EmployeeType;
import by.andersen.employee.exception.ErrorMessage;
import by.andersen.employee.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static by.andersen.employee.TestDataGenerator.ROLE_ADMIN;
import static by.andersen.employee.enums.EmployeeType.OTHER_WORKER;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class EmployeeControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String BASE_PATH = "/api/v1/employees";
    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PATRONYMIC = "patronymic";
    public static final String MANAGER_EMAIL = "managerEmail";
    public static final String BIRTH_DATE_FROM = "birthDateFrom";
    public static final String BIRTH_DATE_TO = "birthDateTo";
    public static final String HIRE_DATE_FROM = "hireDateFrom";
    public static final String HIRE_DATE_TO = "hireDateTo";
    public static final String PAGE = "page";
    public static final String SIZE = "size";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDataGenerator testDataGenerator;


    @BeforeAll
    static void beforeAll() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    }

    @BeforeEach
    void saveData() {
        employeeRepository.saveAll(testDataGenerator.createEmployees());
    }


    @AfterEach
    void cleanDataBase() {
        employeeRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE employee_sequence RESTART WITH 1");
    }

    @ParameterizedTest
    @ValueSource(strings = {"john.black@gmail.com", "julia.tomchuk@gmail.com", "alex.jonathon@gmail.com",
            "jan.doe@gmail.com"})
    void get_ValidEmail_Employee(String email) throws Exception {
        Employee employee = employeeRepository.findByEmail(email).get();

        mockMvc.perform(get(BASE_PATH + "/" + email)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employee.getId()))
                .andExpect(jsonPath("$.firstName").value(employee.getFirstName()))
                .andExpect(jsonPath("$.patronymic").value(employee.getPatronymic()))
                .andExpect(jsonPath("$.lastName").value(employee.getLastName()))
                .andExpect(jsonPath("$.birthDate").value(employee.getBirthDate().toString()))
                .andExpect(jsonPath("$.hireDate").value(employee.getHireDate().toString()))
                .andExpect(jsonPath("$.email").value(employee.getEmail()))
                .andExpect(jsonPath("$.manager").value(employee.getManager()))
                .andExpect(jsonPath("$.createdBy").value(employee.getCreatedBy()))
                .andExpect(jsonPath("$.modifiedBy").value(employee.getModifiedBy()))
                .andExpect(jsonPath("$.createdDate").value(employee.getCreatedDate().toString()))
                .andExpect(jsonPath("$.modifiedDate").value(employee.getModifiedDate().toString()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.black@gmail.com", "julia.test@gmail.com", "alex.test@gmail.com",
            "jan.doe@gmail"})
    void get_InvalidEmail_NotFound(String email) throws Exception {
        mockMvc.perform(get(BASE_PATH + "/" + email)
                        .with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(ErrorMessage.EMPLOYEE_NOT_FOUND.toString()));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void delete_ValidId_NoContent(Long id) throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN))))
                .andExpect(status().isNoContent());

        Optional<Employee> byId = employeeRepository.findById(id);
        assertTrue(byId.isEmpty());
    }

    @Test
    void delete_WithoutAdminRole_Forbidden() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete(BASE_PATH + "/" + id)
                        .with(jwt()))
                .andExpect(status().isForbidden());

        Optional<Employee> byId = employeeRepository.findById(id);
        assertTrue(byId.isPresent());
    }

    @ParameterizedTest
    @ValueSource(longs = {5L, 6L, 7L, 77L})
    void delete_InvalidId_NotFound(Long id) throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN))))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorMessage.EMPLOYEE_NOT_FOUND.toString()));

        long count = employeeRepository.count();
        assertEquals(4L, count);
    }

    @ParameterizedTest
    @MethodSource("getPageable")
    void getAll_Pageable_PageOfEmployees(Pageable pageable) throws Exception {
        mockMvc.perform(get(BASE_PATH)
                        .param(PAGE, String.valueOf(pageable.getPageNumber()))
                        .param(SIZE, String.valueOf(pageable.getPageSize()))
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.page." + SIZE).value(pageable.getPageSize()))
                .andExpect(jsonPath("$.page.totalElements").value(4));

    }

    @Test
    void getAll_WithoutPageable_DefaultPageOfEmployees() throws Exception {
        mockMvc.perform(get(BASE_PATH)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(4))
                .andExpect(jsonPath("$.page." + SIZE).value(10))
                .andExpect(jsonPath("$.content.length()").value(4));
    }

    @Test
    void getAll_WithoutPageableWithEmptyEmployeeTable_DefaultEmptyPage() throws Exception {
        employeeRepository.deleteAll();
        mockMvc.perform(get(BASE_PATH)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page." + SIZE).value(10))
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @ParameterizedTest
    @MethodSource("getEmployeeFilterDto")
    void search_EmployeeFilterDtoWithoutPageable_DefaultPageWithEmployee(EmployeeFilterDto employeeFilterDto) throws Exception {
        mockMvc.perform(get(BASE_PATH + "/search")
                        .with(jwt())
                        .param(EMAIL, employeeFilterDto.getEmail())
                        .param(FIRST_NAME, employeeFilterDto.getFirstName())
                        .param(LAST_NAME, employeeFilterDto.getLastName())
                        .param(PATRONYMIC, employeeFilterDto.getPatronymic())
                        .param(MANAGER_EMAIL, employeeFilterDto.getManagerEmail())
                        .param(BIRTH_DATE_FROM, employeeFilterDto.getBirthDateFrom().toString())
                        .param(BIRTH_DATE_TO, employeeFilterDto.getBirthDateTo().toString())
                        .param(HIRE_DATE_FROM, employeeFilterDto.getHireDateFrom().toString())
                        .param(HIRE_DATE_TO, employeeFilterDto.getHireDateTo().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.*." + EMAIL).value(employeeFilterDto.getEmail()))
                .andExpect(jsonPath("$.content.*." + FIRST_NAME).value(employeeFilterDto.getFirstName()))
                .andExpect(jsonPath("$.content.*." + LAST_NAME).value(employeeFilterDto.getLastName()))
                .andExpect(jsonPath("$.content.*." + PATRONYMIC).value(employeeFilterDto.getPatronymic()))
                .andExpect(jsonPath("$.content.*.manager.email").value(employeeFilterDto.getManagerEmail()))
                .andExpect(jsonPath("$.content.[0].hireDate").value(allOf(
                        greaterThanOrEqualTo(employeeFilterDto.getHireDateFrom().toString()),
                        lessThanOrEqualTo(employeeFilterDto.getHireDateTo().toString()))))
                .andExpect(jsonPath("$.content.[0].birthDate").value(allOf(
                        greaterThanOrEqualTo(employeeFilterDto.getBirthDateFrom().toString()),
                        lessThanOrEqualTo(employeeFilterDto.getBirthDateTo().toString()))));
    }

    @Test
    void search_PageableAndFirstNameStartWithJ_PageWithEmployees() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/search")
                        .with(jwt())
                        .param(FIRST_NAME, "J")
                        .param(SIZE, "3")
                        .param(PAGE, "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(3))
                .andExpect(jsonPath("$.page.size").value(3))
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content.[*]." + FIRST_NAME, everyItem(startsWith("J"))));
    }

    @Test
    void search_NonExistingEmployeeFieldsWithPageable_EmptyPage() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/search")
                        .with(jwt())
                        .param(EMAIL, "test")
                        .param(SIZE, "55")
                        .param(PAGE, "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.size").value(55))
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @ParameterizedTest
    @MethodSource("getParamChangeType")
    void changeType_ValidInput_EmployeeDetailedDto(Long id, EmployeeType type) throws Exception {
        boolean wasManager = false;
        Employee employeeBeforeChange = employeeRepository.findById(id).get();
        if (employeeBeforeChange instanceof Manager) {
            wasManager = true;
        }

        String response = mockMvc.perform(put(BASE_PATH + "/" + id + "/change-type")
                        .param("type", type.name())
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer returnedId = JsonPath.read(response, "$.id");

        Employee employee = employeeRepository.findById(returnedId.longValue()).get();

        switch (type) {
            case WORKER -> assertInstanceOf(Worker.class, employee);
            case OTHER_WORKER -> assertInstanceOf(OtherWorker.class, employee);
            case MANAGER -> assertInstanceOf(Manager.class, employee);

        }
        assertTrue(employeeRepository.findById(id).isEmpty());

        if (wasManager) {
            List<Employee> allByManagerEmail = employeeRepository.findByManagerEmail(employeeBeforeChange.getEmail());
            assertTrue(allByManagerEmail.isEmpty());
        }
    }

    @Test
    void changeType_InvalidId_NotFound() throws Exception {
        mockMvc.perform(put(BASE_PATH + "/" + 100 + "/change-type")
                        .param("type", EmployeeType.WORKER.name())
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN))))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorMessage.EMPLOYEE_NOT_FOUND.toString()));
    }

    @Test
    void changeType_EmployeeAlreadyHasRequestedType_DataConflictException() throws Exception {
        mockMvc.perform(put(BASE_PATH + "/" + 1 + "/change-type")
                        .param("type", EmployeeType.MANAGER.name())
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN))))
                .andExpect(status().isConflict())
                .andExpect(content().string(ErrorMessage.EMPLOYEE_ALREADY_IN_THIS_POSITION.toString()));
    }

    @Test
    void changeType_WithoutAdminRole_Forbidden() throws Exception {
        mockMvc.perform(put(BASE_PATH + "/" + 1 + "/change-type")
                        .param("type", EmployeeType.MANAGER.name())
                        .with(jwt()))
                .andExpect(status().isForbidden());
    }

    private static Stream<Arguments> getParamChangeType() {
        return Stream.of(Arguments.of(1L, EmployeeType.WORKER),
                Arguments.of(2L, OTHER_WORKER),
                Arguments.of(3L, EmployeeType.MANAGER));
    }

    private static List<EmployeeFilterDto> getEmployeeFilterDto() {
        EmployeeFilterDto employeeFilterDto = new EmployeeFilterDto();
        employeeFilterDto.setEmail("john.black@gmail.com");
        employeeFilterDto.setFirstName("John");
        employeeFilterDto.setLastName("Black");
        employeeFilterDto.setPatronymic("John");
        employeeFilterDto.setManagerEmail("julia.tomchuk@gmail.com");
        employeeFilterDto.setBirthDateFrom(Instant.parse("1990-11-19T00:00:00Z"));
        employeeFilterDto.setBirthDateTo(Instant.parse("1990-11-21T00:00:00Z"));
        employeeFilterDto.setHireDateFrom(Instant.parse("2010-11-19T00:00:00Z"));
        employeeFilterDto.setHireDateTo(Instant.parse("2010-11-21T00:00:00Z"));

        EmployeeFilterDto employeeFilterDto1 = new EmployeeFilterDto();
        employeeFilterDto1.setEmail("alex.jonathon@gmail.com");
        employeeFilterDto1.setFirstName("Alex");
        employeeFilterDto1.setLastName("Jonathon");
        employeeFilterDto1.setPatronymic("Aleksandrovich");
        employeeFilterDto1.setManagerEmail("john.black@gmail.com");
        employeeFilterDto1.setBirthDateFrom(Instant.parse("2001-12-27T00:00:00Z"));
        employeeFilterDto1.setBirthDateTo(Instant.parse("2001-12-27T00:00:00Z"));
        employeeFilterDto1.setHireDateFrom(Instant.parse("2021-07-12T00:00:00Z"));
        employeeFilterDto1.setHireDateTo(Instant.parse("2021-07-12T00:00:00Z"));

        EmployeeFilterDto employeeFilterDto2 = new EmployeeFilterDto();
        employeeFilterDto2.setEmail("jan.doe@gmail.com");
        employeeFilterDto2.setFirstName("Jan");
        employeeFilterDto2.setLastName("Doe");
        employeeFilterDto2.setPatronymic("Vladimirovich");
        employeeFilterDto2.setManagerEmail("john.black@gmail.com");
        employeeFilterDto2.setBirthDateFrom(Instant.parse("2000-12-27T00:00:00Z"));
        employeeFilterDto2.setBirthDateTo(Instant.parse("2021-12-27T00:00:00Z"));
        employeeFilterDto2.setHireDateFrom(Instant.parse("1990-07-12T00:00:00Z"));
        employeeFilterDto2.setHireDateTo(Instant.parse("2040-07-12T00:00:00Z"));
        return List.of(employeeFilterDto, employeeFilterDto1, employeeFilterDto2);
    }

    private static List<Pageable> getPageable() {
        return List.of(PageRequest.of(0, 4),
                PageRequest.of(1, 2),
                PageRequest.of(2, 1),
                PageRequest.of(10, 4));
    }

}