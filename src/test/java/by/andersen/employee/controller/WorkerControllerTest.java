package by.andersen.employee.controller;

import by.andersen.employee.TestConfig;
import by.andersen.employee.TestDataGenerator;
import by.andersen.employee.domain.Worker;
import by.andersen.employee.dto.worker.WorkerFilterDto;
import by.andersen.employee.dto.worker.WorkerRequestDto;
import by.andersen.employee.exception.ErrorMessage;
import by.andersen.employee.repository.WorkerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static by.andersen.employee.TestDataGenerator.BIRTH_DATE_FROM;
import static by.andersen.employee.TestDataGenerator.BIRTH_DATE_TO;
import static by.andersen.employee.TestDataGenerator.EMAIL;
import static by.andersen.employee.TestDataGenerator.FIRST_NAME;
import static by.andersen.employee.TestDataGenerator.HIRE_DATE_FROM;
import static by.andersen.employee.TestDataGenerator.HIRE_DATE_TO;
import static by.andersen.employee.TestDataGenerator.LAST_NAME;
import static by.andersen.employee.TestDataGenerator.MANAGER_EMAIL;
import static by.andersen.employee.TestDataGenerator.PAGE;
import static by.andersen.employee.TestDataGenerator.PATRONYMIC;
import static by.andersen.employee.TestDataGenerator.ROLE_ADMIN;
import static by.andersen.employee.TestDataGenerator.SIZE;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class WorkerControllerTest {

    private static final String BASE_PATH = "/api/v1/workers";

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void saveData() {
        workerRepository.saveAll(testDataGenerator.createWorkers());
    }

    @AfterEach
    void cleanDataBase() {
        workerRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE employee_sequence RESTART WITH 1");

    }

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @ParameterizedTest
    @MethodSource("getWorkerRequests")
    void save_ValidRequest_OtherDetailedDto(WorkerRequestDto workerRequestDto) throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .content(objectMapper.writeValueAsString(workerRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(workerRequestDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(workerRequestDto.getLastName()))
                .andExpect(jsonPath("$.patronymic").value(workerRequestDto.getPatronymic()))
                .andExpect(jsonPath("$.email").value(workerRequestDto.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(workerRequestDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.hireDate").value(workerRequestDto.getHireDate().toString()))
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andExpect(jsonPath("$.modifiedDate").isNotEmpty())
                .andExpect(jsonPath("$.createdBy").isNotEmpty())
                .andExpect(jsonPath("$.modifiedBy").isNotEmpty())
                .andExpect(jsonPath("$.id").isNotEmpty());

        Worker actual = workerRepository.findByEmail(workerRequestDto.getEmail()).get();

        assertEquals(workerRequestDto.getFirstName(), actual.getFirstName());
        assertEquals(workerRequestDto.getLastName(), actual.getLastName());
        assertEquals(workerRequestDto.getPatronymic(), actual.getPatronymic());
        assertNotNull(actual.getCreatedBy());
        assertNotNull(actual.getModifiedBy());
        assertEquals(workerRequestDto.getBirthDate(), actual.getBirthDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(workerRequestDto.getHireDate().truncatedTo(ChronoUnit.DAYS), actual.getHireDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(Instant.now().truncatedTo(ChronoUnit.DAYS), actual.getCreatedDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(Instant.now().truncatedTo(ChronoUnit.DAYS), actual.getModifiedDate().truncatedTo(ChronoUnit.DAYS));
        assertNull(actual.getManager());
    }

    @Test
    void save_WithExistingEmail_DataConflictException() throws Exception {
        WorkerRequestDto worker = new WorkerRequestDto();
        worker.setFirstName("Roger");
        worker.setLastName("Barrett");
        worker.setBirthDate(Instant.parse("1946-01-06T00:00:00Z"));
        worker.setEmail("syd.barrett@gmail.com");
        worker.setHireDate(Instant.parse("2023-12-12T00:00:00Z"));
        worker.setPatronymic("Keith");

        mockMvc.perform(post(BASE_PATH)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .content(objectMapper.writeValueAsString(worker))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(ErrorMessage.EMAIL_ALREADY_EXISTS.name()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jan.doe@gmail.com", "ray.manzarek@gmail.com", "robert.krieger@gmail.com", "syd.barrett@gmail.com"})
    void get_ValidEmail_WorkerDetailedDto(String email) throws Exception {
        mockMvc.perform(get(BASE_PATH + "/" + email)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jan.doegmail.com", "test.manzarek@gmail.com", "robertkrieger@gmail.com", "sydbarrett@gmail.com"})
    void get_InvalidEmail_NotFound(String email) throws Exception {
        mockMvc.perform(get(BASE_PATH + "/" + email)
                        .with(jwt()))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("getPageable")
    void getAll_Pageable_PageOfWorkerDto(Pageable pageable) throws Exception {
        mockMvc.perform(get(BASE_PATH)
                        .param(PAGE, String.valueOf(pageable.getPageNumber()))
                        .param(SIZE, String.valueOf(pageable.getPageSize()))
                        .with(jwt())
                        .content(objectMapper.writeValueAsString(pageable)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.page." + SIZE).value(pageable.getPageSize()))
                .andExpect(jsonPath("$.page.totalElements").value(4));

    }

    @Test
    void getAll_WithoutPageable_DefaultPageOfWorkerDto() throws Exception {
        mockMvc.perform(get(BASE_PATH)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page." + SIZE).value(10))
                .andExpect(jsonPath("$.page.totalElements").value(4));
    }

    @ParameterizedTest
    @MethodSource("getParamForUpdate")
    void update_ValidRequest_WorkerDetailedDto(Long id, WorkerRequestDto workerRequestDto) throws Exception {
        mockMvc.perform(put(BASE_PATH + "/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workerRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firstName").value(workerRequestDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(workerRequestDto.getLastName()))
                .andExpect(jsonPath("$.patronymic").value(workerRequestDto.getPatronymic()))
                .andExpect(jsonPath("$.email").value(workerRequestDto.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(workerRequestDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.hireDate").value(workerRequestDto.getHireDate().toString()));

        Worker actual = workerRepository.findById(id).get();
        assertEquals(workerRequestDto.getFirstName(), actual.getFirstName());
        assertEquals(workerRequestDto.getLastName(), actual.getLastName());
        assertEquals(workerRequestDto.getPatronymic(), actual.getPatronymic());
        assertEquals(workerRequestDto.getEmail(), actual.getEmail());
        assertEquals(workerRequestDto.getBirthDate().truncatedTo(ChronoUnit.DAYS),
                actual.getBirthDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(workerRequestDto.getHireDate().truncatedTo(ChronoUnit.DAYS),
                actual.getHireDate().truncatedTo(ChronoUnit.DAYS));
        assertNotNull(actual.getCreatedDate());
        assertEquals(Instant.now().truncatedTo(ChronoUnit.DAYS), actual.getModifiedDate().truncatedTo(ChronoUnit.DAYS));
        assertNotNull(actual.getModifiedBy());
    }

    @ParameterizedTest
    @MethodSource("getWorkerFilterDto")
    void search_ManagerFilterDtoWithoutPageable_DefaultPageWithManagers(WorkerFilterDto workerFilterDto) throws Exception {
        mockMvc.perform(get(BASE_PATH + "/search")
                        .with(jwt())
                        .param(EMAIL, workerFilterDto.getEmail())
                        .param(FIRST_NAME, workerFilterDto.getFirstName())
                        .param(LAST_NAME, workerFilterDto.getLastName())
                        .param(PATRONYMIC, workerFilterDto.getPatronymic())
                        .param(MANAGER_EMAIL, workerFilterDto.getManagerEmail())
                        .param(BIRTH_DATE_FROM, workerFilterDto.getBirthDateFrom().toString())
                        .param(BIRTH_DATE_TO, workerFilterDto.getBirthDateTo().toString())
                        .param(HIRE_DATE_FROM, workerFilterDto.getHireDateFrom().toString())
                        .param(HIRE_DATE_TO, workerFilterDto.getHireDateTo().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.*." + EMAIL).value(workerFilterDto.getEmail()))
                .andExpect(jsonPath("$.content.*." + FIRST_NAME).value(workerFilterDto.getFirstName()))
                .andExpect(jsonPath("$.content.*." + LAST_NAME).value(workerFilterDto.getLastName()))
                .andExpect(jsonPath("$.content.*." + PATRONYMIC).value(workerFilterDto.getPatronymic()))
                .andExpect(jsonPath("$.content.[0].hireDate").value(allOf(
                        greaterThanOrEqualTo(workerFilterDto.getHireDateFrom().toString()),
                        lessThanOrEqualTo(workerFilterDto.getHireDateTo().toString()))))
                .andExpect(jsonPath("$.content.[0].birthDate").value(allOf(
                        greaterThanOrEqualTo(workerFilterDto.getBirthDateFrom().toString()),
                        lessThanOrEqualTo(workerFilterDto.getBirthDateTo().toString()))));
    }

    private static Stream<WorkerFilterDto> getWorkerFilterDto() {
        WorkerFilterDto workerFilterDto = new WorkerFilterDto();
        workerFilterDto.setEmail("ray.manzarek@gmail.com");
        workerFilterDto.setFirstName("Raymond");
        workerFilterDto.setLastName("Manzarek");
        workerFilterDto.setPatronymic("Daniel");
        workerFilterDto.setBirthDateFrom(Instant.parse("1939-01-12T00:00:00Z"));
        workerFilterDto.setBirthDateTo(Instant.parse("1939-04-12T00:00:00Z"));
        workerFilterDto.setHireDateFrom(Instant.parse("1999-07-11T00:00:00Z"));
        workerFilterDto.setHireDateTo(Instant.parse("1999-07-12T00:00:00Z"));
        return Stream.of(workerFilterDto);
    }

    private static Stream<Arguments> getParamForUpdate() {
        WorkerRequestDto workerRequestDto = new WorkerRequestDto();
        workerRequestDto.setFirstName("Josh");
        workerRequestDto.setLastName("Homme");
        workerRequestDto.setPatronymic("test");
        workerRequestDto.setEmail("josh.homme@gmail.com");
        workerRequestDto.setBirthDate(Instant.parse("1973-05-17T00:00:00Z"));
        workerRequestDto.setHireDate(Instant.now());

        WorkerRequestDto workerRequestDto1 = new WorkerRequestDto();
        workerRequestDto1.setFirstName("Dave");
        workerRequestDto1.setLastName("Grohl");
        workerRequestDto1.setPatronymic("test");
        workerRequestDto1.setEmail("dave.grohl@gmail.com");
        workerRequestDto1.setBirthDate(Instant.parse("1969-01-14T00:00:00Z"));
        workerRequestDto1.setHireDate(Instant.now());

        WorkerRequestDto workerRequestDto2 = new WorkerRequestDto();
        workerRequestDto2.setFirstName("Mark");
        workerRequestDto2.setLastName("Lanegan");
        workerRequestDto2.setPatronymic("Homme");
        workerRequestDto2.setEmail("mark.lanegan@gmail.com");
        workerRequestDto2.setBirthDate(Instant.parse("1964-11-25T00:00:00Z"));
        workerRequestDto2.setHireDate(Instant.now());

        return Stream.of(Arguments.of(1L, workerRequestDto),
                Arguments.of(2L, workerRequestDto1),
                Arguments.of(3L, workerRequestDto2));
    }

    private static List<Pageable> getPageable() {
        return List.of(PageRequest.of(0, 4),
                PageRequest.of(1, 2),
                PageRequest.of(2, 1),
                PageRequest.of(10, 4));
    }

    private static List<WorkerRequestDto> getWorkerRequests() {
        WorkerRequestDto workerRequestDto = new WorkerRequestDto();
        workerRequestDto.setFirstName("Josh");
        workerRequestDto.setLastName("Homme");
        workerRequestDto.setPatronymic("test");
        workerRequestDto.setEmail("josh.homme@gmail.com");
        workerRequestDto.setBirthDate(Instant.parse("1973-05-17T00:00:00Z"));
        workerRequestDto.setHireDate(Instant.now());

        WorkerRequestDto workerRequestDto1 = new WorkerRequestDto();
        workerRequestDto1.setFirstName("Dave");
        workerRequestDto1.setLastName("Grohl");
        workerRequestDto1.setPatronymic("test");
        workerRequestDto1.setEmail("dave.grohl@gmail.com");
        workerRequestDto1.setBirthDate(Instant.parse("1969-01-14T00:00:00Z"));
        workerRequestDto1.setHireDate(Instant.now());

        WorkerRequestDto otherWorkerRequestDto2 = new WorkerRequestDto();
        otherWorkerRequestDto2.setFirstName("Mark");
        otherWorkerRequestDto2.setLastName("Lanegan");
        otherWorkerRequestDto2.setPatronymic("Homme");
        otherWorkerRequestDto2.setEmail("mark.lanegan@gmail.com");
        otherWorkerRequestDto2.setBirthDate(Instant.parse("1964-11-25T00:00:00Z"));
        otherWorkerRequestDto2.setHireDate(Instant.now());

        return List.of(workerRequestDto, workerRequestDto1, otherWorkerRequestDto2);
    }

}
