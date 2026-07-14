package br.com.dled.dledbackend.infrastructure.security.web;

import br.com.dled.dledbackend.support.AbstractWebIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIT extends AbstractWebIntegrationTest {

    @Test
    void shouldLoginWithDefaultUserAndReturnCookieAndToken() throws Exception {
        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "user",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("AUTH_TOKEN"))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.username").value("user"));
    }

    @Test
    void shouldLogoutAndClearCookie() throws Exception {
        mockMvc.perform(post("/v1/auth/logout")
                        .cookie(authCookie()))
                .andExpect(status().isOk())
                .andExpect(cookie().value("AUTH_TOKEN", ""))
                .andExpect(cookie().maxAge("AUTH_TOKEN", 0));
    }

    @Test
    void shouldReturnAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/v1/auth/me")
                        .cookie(authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@dled.local"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void shouldReturnUnauthorizedWhenCurrentUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void shouldAllowAuthenticatedAccessUsingBearerToken() throws Exception {
        String token = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "user",
                                  "password": "123456"
                                }
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = token.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/v1/companies")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void shouldForbidClientRoleFromProtectedRoutes() throws Exception {
        mockMvc.perform(post("/v1/users")
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Cliente Final",
                                  "username": "client-user",
                                  "email": "client-user@dled.com",
                                  "phone": "11977777777",
                                  "companyName": "Cliente Final LTDA",
                                  "password": "Client@123",
                                  "role": "CLIENT",
                                  "active": true
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/companies")
                        .cookie(authCookie("client-user", "Client@123")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }
}
