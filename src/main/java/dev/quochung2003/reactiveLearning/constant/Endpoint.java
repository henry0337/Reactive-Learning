package dev.quochung2003.reactiveLearning.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Endpoint {
    /**
     * Base API route, can be used to manage API version.
     */
    private static final String BASE_ROUTE = "/api/v1";

    /**
     * Special route, used to mark <b>Spring</b> that child routes of a parent route should be included.
     */
    public static final String ALL_CHILD_ROUTE = "/**";

    // Parent route (based on controller's class name)
    public static final String AUTH_ROUTE = BASE_ROUTE +  "/auth";
    public static final String USER_ROUTE = BASE_ROUTE + "/user";
    public static final String SHUTDOWN_ROUTE = BASE_ROUTE + "/shutdown";

    // Child route (Auth)
    public static final String REGISTER_ROUTE = AUTH_ROUTE + "/register";
    public static final String LOGIN_ROUTE = AUTH_ROUTE + "/login";
    public static final String CHANGE_PASSWORD_ROUTE = AUTH_ROUTE + "/changePassword";
    public static final String USER_INFO_ROUTE = AUTH_ROUTE + "/userInfo";

    // Swagger routes
    public static final String SWAGGER_UI_1_ROUTE = "/webjars/swagger-ui";
    public static final String SWAGGER_UI_2_ROUTE = "/swagger-ui.html";
    public static final String API_DOC_ROUTE = "/v3/api-docs";

    // Other
    public static final String LIST_ALL_ROUTE = "/actuator/mappings";
}
