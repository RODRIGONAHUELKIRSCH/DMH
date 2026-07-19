package com.dmh;

import com.dmh.UserController.ApiUserControllerTest;
import com.dmh.UserService.ApiUserServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Entry point suite para los tests del módulo api-user.
 *
 * Al ejecutar esta clase (ya sea desde IntelliJ, Maven o CI) se disparan
 * secuencialmente las clases incluidas en @SelectClasses. Cada clase objetivo
 * corre con su propio motor: Mockito (@ExtendWith(MockitoExtension.class))
 * o Spring (@SpringBootTest + @AutoConfigureMockMvc) según corresponda.
 *
 * Requisito: junit-platform-suite-engine en pom.xml (gestionado por
 * spring-boot-starter-parent 4.0.x -> JUnit Platform 6.x).
 */
@Suite
@SuiteDisplayName("User API Test Suite")
@SelectClasses({
        ApiUserServiceTest.class,
        ApiUserControllerTest.class,

})
class ApiUserApplicationTests {
    // Intencionalmente vacía: actúa únicamente como entry point del suite.
}
