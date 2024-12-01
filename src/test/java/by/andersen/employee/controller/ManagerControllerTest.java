package by.andersen.employee.controller;

import by.andersen.employee.TestConfig;
import by.andersen.employee.TestDataGenerator;
import by.andersen.employee.domain.Employee;
import by.andersen.employee.domain.Manager;
import by.andersen.employee.dto.manager.ManagerFilterDto;
import by.andersen.employee.dto.manager.ManagerRequestDto;
import by.andersen.employee.exception.ErrorMessage;
import by.andersen.employee.repository.ManagerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
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
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
class ManagerControllerTest {

    private static final String BASE_PATH = "/api/v1/managers";

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @BeforeEach
    void saveData() {
        managerRepository.saveAll(testDataGenerator.createManagers());
    }

    @AfterEach
    void cleanDataBase() {
        managerRepository.deleteAll();
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
    @MethodSource("getManagerRequestDto")
    void save_ValidInput_ManagerDetailedDto(ManagerRequestDto managerRequestDto) throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .content(objectMapper.writeValueAsString(managerRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(managerRequestDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(managerRequestDto.getLastName()))
                .andExpect(jsonPath("$.patronymic").value(managerRequestDto.getPatronymic()))
                .andExpect(jsonPath("$.email").value(managerRequestDto.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(managerRequestDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.hireDate").value(managerRequestDto.getHireDate().toString()))
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andExpect(jsonPath("$.modifiedDate").isNotEmpty())
                .andExpect(jsonPath("$.createdBy").isNotEmpty())
                .andExpect(jsonPath("$.modifiedBy").isNotEmpty())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        Manager actual = managerRepository.findByEmail(managerRequestDto.getEmail()).get();

        assertEquals(managerRequestDto.getFirstName(), actual.getFirstName());
        assertEquals(managerRequestDto.getLastName(), actual.getLastName());
        assertEquals(managerRequestDto.getPatronymic(), actual.getPatronymic());
        assertNotNull(actual.getCreatedBy());
        assertNotNull(actual.getModifiedBy());
        assertEquals(managerRequestDto.getBirthDate(), actual.getBirthDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(managerRequestDto.getHireDate().truncatedTo(ChronoUnit.DAYS), actual.getHireDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(Instant.now().truncatedTo(ChronoUnit.DAYS), actual.getCreatedDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(Instant.now().truncatedTo(ChronoUnit.DAYS), actual.getModifiedDate().truncatedTo(ChronoUnit.DAYS));
        assertNull(actual.getManager());
        assertTrue(actual.getSubordinates().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("getManagerRequestDto")
    void save_WithoutAdminRole_Forbidden(ManagerRequestDto managerRequestDto) throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .with(jwt())
                        .content(objectMapper.writeValueAsString(managerRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        Optional<Manager> byEmail = managerRepository.findByEmail(managerRequestDto.getEmail());
        assertTrue(byEmail.isEmpty());
    }

    @Transactional
    @ParameterizedTest
    @ValueSource(strings = {"john.black@gmail.com", "julia.tomchuk@gmail.com", "matt.bellamy@gmail.com"})
    void get_ValidEmail_ManagerDetailedDto(String email) throws Exception {
        mockMvc.perform(get(BASE_PATH + "/" + email)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"john.test@gmail.com", "test.tomchuk@gmail.com", "matt.test@gmail.com"})
    void get_InvalidEmail_NotFound(String email) throws Exception {
        mockMvc.perform(get(BASE_PATH + "/" + email)
                        .with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorMessage.MANAGER_NOT_FOUND.toString()));
    }

    @ParameterizedTest
    @MethodSource("getParamForUpdate")
    void update_ValidRequest_ManagerDetailedDto(int index, ManagerRequestDto managerRequestDto) throws Exception {
        Long id = managerRepository.findAll().get(index).getId();

        mockMvc.perform(put(BASE_PATH + "/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firstName").value(managerRequestDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(managerRequestDto.getLastName()))
                .andExpect(jsonPath("$.patronymic").value(managerRequestDto.getPatronymic()))
                .andExpect(jsonPath("$.email").value(managerRequestDto.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(managerRequestDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.hireDate").value(managerRequestDto.getHireDate().toString()));

        Manager actual = managerRepository.findById(id).get();
        assertEquals(managerRequestDto.getFirstName(), actual.getFirstName());
        assertEquals(managerRequestDto.getLastName(), actual.getLastName());
        assertEquals(managerRequestDto.getPatronymic(), actual.getPatronymic());
        assertEquals(managerRequestDto.getEmail(), actual.getEmail());
        assertEquals(managerRequestDto.getBirthDate().truncatedTo(ChronoUnit.DAYS),
                actual.getBirthDate().truncatedTo(ChronoUnit.DAYS));
        assertEquals(managerRequestDto.getHireDate().truncatedTo(ChronoUnit.DAYS),
                actual.getHireDate().truncatedTo(ChronoUnit.DAYS));
        assertNotNull(actual.getCreatedDate());
        assertEquals(Instant.now().truncatedTo(ChronoUnit.DAYS), actual.getModifiedDate().truncatedTo(ChronoUnit.DAYS));
        assertNotNull(actual.getModifiedBy());
    }

    @ParameterizedTest
    @MethodSource("getParamForUpdateWithInvalidId")
    void update_InvalidId_NotFound(Long id, ManagerRequestDto managerRequestDto) throws Exception {
        mockMvc.perform(put(BASE_PATH + "/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorMessage.MANAGER_NOT_FOUND.toString()));
    }

    @ParameterizedTest
    @MethodSource("getParamForUpdateWithInvalidManagerRequest")
    void update_InvalidManagerRequest_BadRequest(int index, ManagerRequestDto managerRequestDto) throws Exception {
        Long id = managerRepository.findAll().get(index).getId();
        mockMvc.perform(put(BASE_PATH + "/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("getParamForUpdate")
    void update_WithoutRoleAdmin_Forbidden(int index, ManagerRequestDto managerRequestDto) throws Exception {
        Long id = managerRepository.findAll().get(index).getId();
        mockMvc.perform(put(BASE_PATH + "/" + id)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerRequestDto)))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("getPageable")
    void getAll_Pageable_PageOfManagerDto(Pageable pageable) throws Exception {
        mockMvc.perform(get(BASE_PATH)
                        .param(PAGE, String.valueOf(pageable.getPageNumber()))
                        .param(SIZE, String.valueOf(pageable.getPageSize()))
                        .with(jwt())
                        .content(objectMapper.writeValueAsString(pageable)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.page." + SIZE).value(pageable.getPageSize()))
                .andExpect(jsonPath("$.page.totalElements").value(3));

    }

    @Test
    void getAll_WithoutPageable_DefaultPageOfManagerDto() throws Exception {
        mockMvc.perform(get(BASE_PATH)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page." + SIZE).value(10))
                .andExpect(jsonPath("$.page.totalElements").value(3));

    }

    @Test
    void getAll_WithoutJwt_Unauthorized() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isUnauthorized());

    }

    @ParameterizedTest
    @MethodSource("getManagerFilterDto")
    void search_ManagerFilterDtoWithoutPageable_DefaultPageWithManagers(ManagerFilterDto managerFilterDto) throws Exception {
        mockMvc.perform(get(BASE_PATH + "/search")
                        .with(jwt())
                        .param(EMAIL, managerFilterDto.getEmail())
                        .param(FIRST_NAME, managerFilterDto.getFirstName())
                        .param(LAST_NAME, managerFilterDto.getLastName())
                        .param(PATRONYMIC, managerFilterDto.getPatronymic())
                        .param(MANAGER_EMAIL, managerFilterDto.getManagerEmail())
                        .param(BIRTH_DATE_FROM, managerFilterDto.getBirthDateFrom().toString())
                        .param(BIRTH_DATE_TO, managerFilterDto.getBirthDateTo().toString())
                        .param(HIRE_DATE_FROM, managerFilterDto.getHireDateFrom().toString())
                        .param(HIRE_DATE_TO, managerFilterDto.getHireDateTo().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.*." + EMAIL).value(managerFilterDto.getEmail()))
                .andExpect(jsonPath("$.content.*." + FIRST_NAME).value(managerFilterDto.getFirstName()))
                .andExpect(jsonPath("$.content.*." + LAST_NAME).value(managerFilterDto.getLastName()))
                .andExpect(jsonPath("$.content.*." + PATRONYMIC).value(managerFilterDto.getPatronymic()))
                .andExpect(jsonPath("$.content.*.manager.email").value(managerFilterDto.getManagerEmail()))
                .andExpect(jsonPath("$.content.[0].hireDate").value(allOf(
                        greaterThanOrEqualTo(managerFilterDto.getHireDateFrom().toString()),
                        lessThanOrEqualTo(managerFilterDto.getHireDateTo().toString()))))
                .andExpect(jsonPath("$.content.[0].birthDate").value(allOf(
                        greaterThanOrEqualTo(managerFilterDto.getBirthDateFrom().toString()),
                        lessThanOrEqualTo(managerFilterDto.getBirthDateTo().toString()))));
    }

    @Test
    void search_PageableAndNameStartsWithJ_PageWithManagers() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/search")
                        .with(jwt())
                        .param(FIRST_NAME, "J")
                        .param(PAGE, String.valueOf(0))
                        .param(SIZE, String.valueOf(2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content.*." + FIRST_NAME, everyItem(startsWith("J"))));
    }

    @Transactional
    @Test
    void addSubordinates_ValidRequest_ManagerDetailedDto() throws Exception {
        List<Manager> all = managerRepository.findAll();
        Long managerId = all.get(0).getId();
        all.remove(0);
        List<Long> employeeIds = all.stream().map(Employee::getId).toList();

        mockMvc.perform(patch(BASE_PATH + "/" + managerId + "/subordinates")
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(managerId))
                .andExpect(jsonPath("$.subordinates.length()").value(employeeIds.size()));

        Manager actual = managerRepository.findById(managerId).get();
        List<Long> subordinateIds = actual.getSubordinates().stream().map(Employee::getId).toList();

        assertTrue(subordinateIds.containsAll(employeeIds));
    }

    @Transactional
    @Test
    void addSubordinates_AddMangerToHisOwnSubordinates_DataConflictException() throws Exception {
        List<Manager> all = managerRepository.findAll();
        Long managerId = all.get(0).getId();
        List<Long> employeeIds = all.stream().map(Employee::getId).toList();

        mockMvc.perform(patch(BASE_PATH + "/" + managerId + "/subordinates")
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeIds)))
                .andExpect(status().isConflict())
                .andExpect(content().string(ErrorMessage.SELF_SUBORDINATION_NOT_ALLOWED.toString()));

        Manager actual = managerRepository.findById(managerId).get();
        List<Long> subordinateIds = actual.getSubordinates().stream().map(Employee::getId).toList();

        assertEquals(actual.getId(), managerId);
        assertFalse(subordinateIds.containsAll(employeeIds));
    }

    @Test
    void addSubordinates_InvalidManagerId_NotFound() throws Exception {
        List<Manager> all = managerRepository.findAll();
        Long lastId = all.get(all.size()-1).getId();
        long id = lastId+1L;
        mockMvc.perform(patch(BASE_PATH + "/" + id+ "/subordinates")
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(2, 3))))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorMessage.MANAGER_NOT_FOUND.toString()));
    }

    @Transactional
    @Test
    void removeSubordinates_ValidRequest_ManagerDetailedDto() throws Exception {
        Long managerId = 2L;
        Long employeeId = 1L;

        Manager manager = managerRepository.findById(managerId).get();
        List<Long> subordinateIdsBeforeDeletion = manager.getSubordinates().stream().map(Employee::getId).toList();
        assertTrue(subordinateIdsBeforeDeletion.contains(employeeId));

        mockMvc.perform(delete(BASE_PATH + "/" + managerId + "/subordinates")
                        .param("employeeIds", employeeId.toString())
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(managerId));

        Manager actual = managerRepository.findById(managerId).get();
        List<Long> subordinateIdsAfterDeletion = actual.getSubordinates().stream().map(Employee::getId).toList();

        assertFalse(subordinateIdsAfterDeletion.contains(employeeId));
    }

    private static Stream<ManagerFilterDto> getManagerFilterDto() {
        ManagerFilterDto managerFilterDto = new ManagerFilterDto();
        managerFilterDto.setEmail("john.black@gmail.com");
        managerFilterDto.setFirstName("John");
        managerFilterDto.setLastName("Black");
        managerFilterDto.setPatronymic("John");
        managerFilterDto.setManagerEmail("julia.tomchuk@gmail.com");
        managerFilterDto.setBirthDateFrom(Instant.parse("1990-11-19T00:00:00Z"));
        managerFilterDto.setBirthDateTo(Instant.parse("1990-11-21T00:00:00Z"));
        managerFilterDto.setHireDateFrom(Instant.parse("2010-11-19T00:00:00Z"));
        managerFilterDto.setHireDateTo(Instant.parse("2010-11-21T00:00:00Z"));
        return Stream.of(managerFilterDto);
    }


    private static List<Pageable> getPageable() {
        return List.of(PageRequest.of(0, 3),
                PageRequest.of(1, 2),
                PageRequest.of(2, 1),
                PageRequest.of(10, 4));
    }


    private static Stream<Arguments> getParamForUpdateWithInvalidManagerRequest() {
        ManagerRequestDto managerRequestDto = new ManagerRequestDto();
        managerRequestDto.setFirstName("Josh");
        managerRequestDto.setLastName("Homme");
        managerRequestDto.setPatronymic("test");
        managerRequestDto.setEmail("josh.hommegmail.com");
        managerRequestDto.setBirthDate(Instant.parse("1973-05-17T00:00:00Z"));
        managerRequestDto.setHireDate(Instant.now());

        ManagerRequestDto managerRequestDto1 = new ManagerRequestDto();
        managerRequestDto1.setFirstName("");
        managerRequestDto1.setLastName("Grohl");
        managerRequestDto1.setPatronymic("test");
        managerRequestDto1.setEmail("dave.grohl@gmail.com");
        managerRequestDto1.setBirthDate(Instant.parse("1969-01-14T00:00:00Z"));
        managerRequestDto1.setHireDate(Instant.now());

        ManagerRequestDto managerRequestDto2 = new ManagerRequestDto();
        managerRequestDto2.setFirstName("Mark");
        managerRequestDto2.setLastName("");
        managerRequestDto2.setPatronymic("Homme");
        managerRequestDto2.setEmail("mark.lanegan@gmail.com");
        managerRequestDto2.setBirthDate(Instant.parse("1964-11-25T00:00:00Z"));
        managerRequestDto2.setHireDate(Instant.now());

        ManagerRequestDto managerRequestDto3 = new ManagerRequestDto();
        managerRequestDto3.setFirstName("Mark");
        managerRequestDto3.setLastName("Lanegan");
        managerRequestDto3.setPatronymic("");
        managerRequestDto3.setEmail("mark.lanegan@gmail.com");
        managerRequestDto3.setBirthDate(Instant.parse("1964-11-25T00:00:00Z"));
        managerRequestDto3.setHireDate(Instant.now());

        ManagerRequestDto managerRequestDto4 = new ManagerRequestDto();
        managerRequestDto4.setFirstName("Dave");
        managerRequestDto4.setLastName("Grohl");
        managerRequestDto4.setPatronymic("test");
        managerRequestDto4.setEmail("dave.grohl@gmail.com");
        managerRequestDto4.setHireDate(Instant.now());

        ManagerRequestDto managerRequestDto5 = new ManagerRequestDto();
        managerRequestDto5.setFirstName("Dave");
        managerRequestDto5.setLastName("Grohl");
        managerRequestDto5.setPatronymic("test");
        managerRequestDto5.setEmail("dave.grohl@gmail.com");
        managerRequestDto5.setBirthDate(Instant.parse("1969-01-14T00:00:00Z"));

        return Stream.of(Arguments.of(0, managerRequestDto),
                Arguments.of(1, managerRequestDto1),
                Arguments.of(2, managerRequestDto2),
                Arguments.of(0, managerRequestDto4),
                Arguments.of(1, managerRequestDto5));
    }

    private static Stream<Arguments> getParamForUpdateWithInvalidId() {
        List<ManagerRequestDto> managerRequestDtos = getManagerRequestDto();
        return Stream.of(Arguments.of(100L, managerRequestDtos.get(0)),
                Arguments.of(4L, managerRequestDtos.get(1)),
                Arguments.of(88L, managerRequestDtos.get(2)));
    }

    private static Stream<Arguments> getParamForUpdate() {
        List<ManagerRequestDto> managerRequestDtos = getManagerRequestDto();
        return Stream.of(Arguments.of(0, managerRequestDtos.get(0)),
                Arguments.of(1, managerRequestDtos.get(1)),
                Arguments.of(2, managerRequestDtos.get(2)));
    }

    private static List<ManagerRequestDto> getManagerRequestDto() {
        ManagerRequestDto managerRequestDto = new ManagerRequestDto();
        managerRequestDto.setFirstName("Josh");
        managerRequestDto.setLastName("Homme");
        managerRequestDto.setPatronymic("test");
        managerRequestDto.setEmail("josh.homme@gmail.com");
        managerRequestDto.setBirthDate(Instant.parse("1973-05-17T00:00:00Z"));
        managerRequestDto.setHireDate(Instant.now());

        ManagerRequestDto managerRequestDto1 = new ManagerRequestDto();
        managerRequestDto1.setFirstName("Dave");
        managerRequestDto1.setLastName("Grohl");
        managerRequestDto1.setPatronymic("test");
        managerRequestDto1.setEmail("dave.grohl@gmail.com");
        managerRequestDto1.setBirthDate(Instant.parse("1969-01-14T00:00:00Z"));
        managerRequestDto1.setHireDate(Instant.now());

        ManagerRequestDto managerRequestDto2 = new ManagerRequestDto();
        managerRequestDto2.setFirstName("Mark");
        managerRequestDto2.setLastName("Lanegan");
        managerRequestDto2.setPatronymic("Homme");
        managerRequestDto2.setEmail("mark.lanegan@gmail.com");
        managerRequestDto2.setBirthDate(Instant.parse("1964-11-25T00:00:00Z"));
        managerRequestDto2.setHireDate(Instant.now());

        return List.of(managerRequestDto, managerRequestDto1, managerRequestDto2);
    }

}