package by.andersen.employee.controller;

import by.andersen.employee.TestConfig;
import by.andersen.employee.TestDataGenerator;
import by.andersen.employee.domain.OtherWorker;
import by.andersen.employee.dto.other_worker.OtherWorkerFilterDto;
import by.andersen.employee.dto.other_worker.OtherWorkerRequestDto;
import by.andersen.employee.exception.ErrorMessage;
import by.andersen.employee.repository.OtherWorkerRepository;
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
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
class OtherWorkerControllerTest {

    private static final String BASE_PATH = "/api/v1/other-workers";

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private OtherWorkerRepository otherWorkerRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @BeforeEach
    void saveData() {
        otherWorkerRepository.saveAll(testDataGenerator.createOtherWorkers());
    }

    @AfterEach
    void cleanDataBase() {
        otherWorkerRepository.deleteAll();
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
    @MethodSource("getOtherWorkerRequests")
    void save_ValidRequest_OtherWorkerDetailedDto(OtherWorkerRequestDto otherWorkerRequestDto) throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .content(objectMapper.writeValueAsString(otherWorkerRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(otherWorkerRequestDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(otherWorkerRequestDto.getLastName()))
                .andExpect(jsonPath("$.patronymic").value(otherWorkerRequestDto.getPatronymic()))
                .andExpect(jsonPath("$.email").value(otherWorkerRequestDto.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(otherWorkerRequestDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.hireDate").value(otherWorkerRequestDto.getHireDate().toString()))
                .andExpect(jsonPath("$.description").value(otherWorkerRequestDto.getDescription()))
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andExpect(jsonPath("$.modifiedDate").isNotEmpty())
                .andExpect(jsonPath("$.createdBy").isNotEmpty())
                .andExpect(jsonPath("$.modifiedBy").isNotEmpty())
                .andExpect(jsonPath("$.id").isNotEmpty());

        OtherWorker actual = otherWorkerRepository.findByEmail(otherWorkerRequestDto.getEmail()).get();

        assertEquals(otherWorkerRequestDto.getFirstName(), actual.getFirstName());
        assertEquals(otherWorkerRequestDto.getLastName(), actual.getLastName());
        assertEquals(otherWorkerRequestDto.getPatronymic(), actual.getPatronymic());
        assertNotNull(actual.getCreatedBy());
        assertNotNull(actual.getModifiedBy());
        assertEquals(otherWorkerRequestDto.getBirthDate(), actual.getBirthDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(otherWorkerRequestDto.getHireDate().truncatedTo(ChronoUnit.DAYS), actual.getHireDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(Instant.now().truncatedTo(ChronoUnit.DAYS), actual.getCreatedDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(Instant.now().truncatedTo(ChronoUnit.DAYS), actual.getModifiedDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(otherWorkerRequestDto.getDescription(), actual.getDescription());
        assertNull(actual.getManager());
    }

    @Test
    void save_WithExistingEmail_DataConflictException() throws Exception {
        OtherWorkerRequestDto otherWorker = new OtherWorkerRequestDto();
        otherWorker.setFirstName("Roger");
        otherWorker.setLastName("Barrett");
        otherWorker.setBirthDate(Instant.parse("1946-01-06T00:00:00Z"));
        otherWorker.setEmail("syd.barrett@gmail.com");
        otherWorker.setHireDate(Instant.parse("2023-12-12T00:00:00Z"));
        otherWorker.setPatronymic("Keith");
        otherWorker.setDescription("this is Syd");

        mockMvc.perform(post(BASE_PATH)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .content(objectMapper.writeValueAsString(otherWorker))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(ErrorMessage.EMAIL_ALREADY_EXISTS.name()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jan.doe@gmail.com", "ray.manzarek@gmail.com", "robert.krieger@gmail.com", "syd.barrett@gmail.com"})
    void get_ValidEmail_OtherWorkerDetailedDto(String email) throws Exception {
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
    void getAll_Pageable_PageOfOtherWorkerDto(Pageable pageable) throws Exception {
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
    void getAll_WithoutPageable_DefaultPageOfOtherWorkerDto() throws Exception {
        mockMvc.perform(get(BASE_PATH)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page." + SIZE).value(10))
                .andExpect(jsonPath("$.page.totalElements").value(4));
    }

    @ParameterizedTest
    @MethodSource("getParamForUpdate")
    void update_ValidRequest_OtherWorkerDetailedDto(OtherWorkerRequestDto otherWorkerRequestDto) throws Exception {
        Long id = otherWorkerRepository.findAll().get(0).getId();

        mockMvc.perform(put(BASE_PATH + "/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otherWorkerRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firstName").value(otherWorkerRequestDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(otherWorkerRequestDto.getLastName()))
                .andExpect(jsonPath("$.patronymic").value(otherWorkerRequestDto.getPatronymic()))
                .andExpect(jsonPath("$.email").value(otherWorkerRequestDto.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(otherWorkerRequestDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.description").value(otherWorkerRequestDto.getDescription()))
                .andExpect(jsonPath("$.hireDate").value(otherWorkerRequestDto.getHireDate().toString()));

        OtherWorker actual = otherWorkerRepository.findById(id).get();
        assertEquals(otherWorkerRequestDto.getFirstName(), actual.getFirstName());
        assertEquals(otherWorkerRequestDto.getLastName(), actual.getLastName());
        assertEquals(otherWorkerRequestDto.getPatronymic(), actual.getPatronymic());
        assertEquals(otherWorkerRequestDto.getEmail(), actual.getEmail());
        assertEquals(otherWorkerRequestDto.getDescription(), actual.getDescription());
        assertEquals(otherWorkerRequestDto.getBirthDate().truncatedTo(ChronoUnit.DAYS),
                actual.getBirthDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(otherWorkerRequestDto.getHireDate().truncatedTo(ChronoUnit.DAYS),
                actual.getHireDate().truncatedTo(ChronoUnit.DAYS));
        assertNotNull(actual.getCreatedDate());
        assertEquals(Instant.now().truncatedTo(ChronoUnit.DAYS), actual.getModifiedDate().truncatedTo(ChronoUnit.DAYS));
        assertNotNull(actual.getModifiedBy());
    }

    @ParameterizedTest
    @MethodSource("getOtherWorkerFilterDto")
    void search_ManagerFilterDtoWithoutPageable_DefaultPageWithManagers(OtherWorkerFilterDto otherWorkerFilterDto) throws Exception {
        mockMvc.perform(get(BASE_PATH + "/search")
                        .with(jwt())
                        .param(EMAIL, otherWorkerFilterDto.getEmail())
                        .param(FIRST_NAME, otherWorkerFilterDto.getFirstName())
                        .param(LAST_NAME, otherWorkerFilterDto.getLastName())
                        .param(PATRONYMIC, otherWorkerFilterDto.getPatronymic())
                        .param(MANAGER_EMAIL, otherWorkerFilterDto.getManagerEmail())
                        .param(BIRTH_DATE_FROM, otherWorkerFilterDto.getBirthDateFrom().toString())
                        .param(BIRTH_DATE_TO, otherWorkerFilterDto.getBirthDateTo().toString())
                        .param(HIRE_DATE_FROM, otherWorkerFilterDto.getHireDateFrom().toString())
                        .param(HIRE_DATE_TO, otherWorkerFilterDto.getHireDateTo().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.*." + EMAIL).value(otherWorkerFilterDto.getEmail()))
                .andExpect(jsonPath("$.content.*." + FIRST_NAME).value(otherWorkerFilterDto.getFirstName()))
                .andExpect(jsonPath("$.content.*." + LAST_NAME).value(otherWorkerFilterDto.getLastName()))
                .andExpect(jsonPath("$.content.*." + PATRONYMIC).value(otherWorkerFilterDto.getPatronymic()))
                .andExpect(jsonPath("$.content.[0].hireDate").value(allOf(
                        greaterThanOrEqualTo(otherWorkerFilterDto.getHireDateFrom().toString()),
                        lessThanOrEqualTo(otherWorkerFilterDto.getHireDateTo().toString()))))
                .andExpect(jsonPath("$.content.[0].birthDate").value(allOf(
                        greaterThanOrEqualTo(otherWorkerFilterDto.getBirthDateFrom().toString()),
                        lessThanOrEqualTo(otherWorkerFilterDto.getBirthDateTo().toString()))));
    }

    private static Stream<OtherWorkerFilterDto> getOtherWorkerFilterDto() {
        OtherWorkerFilterDto otherWorkerFilterDto = new OtherWorkerFilterDto();
        otherWorkerFilterDto.setEmail("ray.manzarek@gmail.com");
        otherWorkerFilterDto.setFirstName("Raymond");
        otherWorkerFilterDto.setLastName("Manzarek");
        otherWorkerFilterDto.setPatronymic("Daniel");
        otherWorkerFilterDto.setBirthDateFrom(Instant.parse("1939-01-12T00:00:00Z"));
        otherWorkerFilterDto.setBirthDateTo(Instant.parse("1939-04-12T00:00:00Z"));
        otherWorkerFilterDto.setHireDateFrom(Instant.parse("1999-07-11T00:00:00Z"));
        otherWorkerFilterDto.setHireDateTo(Instant.parse("1999-07-12T00:00:00Z"));
        return Stream.of(otherWorkerFilterDto);
    }


    private static Stream<OtherWorkerRequestDto> getParamForUpdate() {
        OtherWorkerRequestDto otherWorkerRequestDto = new OtherWorkerRequestDto();
        otherWorkerRequestDto.setFirstName("Josh");
        otherWorkerRequestDto.setLastName("Homme");
        otherWorkerRequestDto.setPatronymic("test");
        otherWorkerRequestDto.setEmail("josh.homme@gmail.com");
        otherWorkerRequestDto.setBirthDate(Instant.parse("1973-05-17T00:00:00Z"));
        otherWorkerRequestDto.setHireDate(Instant.now());
        otherWorkerRequestDto.setDescription("This is Josh");

        return Stream.of( otherWorkerRequestDto);
    }

    private static List<Pageable> getPageable() {
        return List.of(PageRequest.of(0, 4),
                PageRequest.of(1, 2),
                PageRequest.of(2, 1),
                PageRequest.of(10, 4));
    }

    private static List<OtherWorkerRequestDto> getOtherWorkerRequests() {
        OtherWorkerRequestDto otherWorkerRequestDto = new OtherWorkerRequestDto();
        otherWorkerRequestDto.setFirstName("Josh");
        otherWorkerRequestDto.setLastName("Homme");
        otherWorkerRequestDto.setPatronymic("test");
        otherWorkerRequestDto.setEmail("josh.homme@gmail.com");
        otherWorkerRequestDto.setBirthDate(Instant.parse("1973-05-17T00:00:00Z"));
        otherWorkerRequestDto.setHireDate(Instant.now());
        otherWorkerRequestDto.setDescription("This is Josh");

        OtherWorkerRequestDto otherWorkerRequestDto1 = new OtherWorkerRequestDto();
        otherWorkerRequestDto1.setFirstName("Dave");
        otherWorkerRequestDto1.setLastName("Grohl");
        otherWorkerRequestDto1.setPatronymic("test");
        otherWorkerRequestDto1.setEmail("dave.grohl@gmail.com");
        otherWorkerRequestDto1.setBirthDate(Instant.parse("1969-01-14T00:00:00Z"));
        otherWorkerRequestDto1.setHireDate(Instant.now());
        otherWorkerRequestDto1.setDescription("This is Dave");

        OtherWorkerRequestDto otherWorkerRequestDto2 = new OtherWorkerRequestDto();
        otherWorkerRequestDto2.setFirstName("Mark");
        otherWorkerRequestDto2.setLastName("Lanegan");
        otherWorkerRequestDto2.setPatronymic("Homme");
        otherWorkerRequestDto2.setEmail("mark.lanegan@gmail.com");
        otherWorkerRequestDto2.setBirthDate(Instant.parse("1964-11-25T00:00:00Z"));
        otherWorkerRequestDto2.setHireDate(Instant.now());
        otherWorkerRequestDto2.setDescription("This is Mark");

        return List.of(otherWorkerRequestDto, otherWorkerRequestDto1, otherWorkerRequestDto2);
    }

}