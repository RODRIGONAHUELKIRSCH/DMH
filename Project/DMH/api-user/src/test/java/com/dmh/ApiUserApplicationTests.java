package com.dmh;

import com.dmh.UserController.ApiUserControllerTest;
import com.dmh.UserService.ApiUserServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("User API Test Suite")
@SelectClasses({
        ApiUserServiceTest.class,
        ApiUserControllerTest.class,

})
class ApiUserApplicationTests {
    // Intencionalmente vacía: actúa únicamente como entry point del suite.
}
