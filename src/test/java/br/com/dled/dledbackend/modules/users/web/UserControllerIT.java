package br.com.dled.dledbackend.modules.users.web;

import br.com.dled.dledbackend.support.AbstractWebIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIT extends AbstractWebIntegrationTest {

    @Test
    void shouldReturnUsersList() throws Exception {
        mockMvc.perform(get("/v1/users")
                        .cookie(authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void shouldCreateUser() throws Exception {
        mockMvc.perform(post("/v1/users")
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Administrador da Loja",
                                  "username": "admin-store",
                                  "email": "admin-store@dled.com",
                                  "phone": "11999998888",
                                  "companyName": "Loja Centro",
                                  "password": "Admin@123",
                                  "role": "ADMIN",
                                  "active": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("admin-store"))
                .andExpect(jsonPath("$.companyName").value("Loja Centro"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldReturnUserById() throws Exception {
        String response = mockMvc.perform(post("/v1/users")
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Vendedor Interno",
                                  "username": "seller-one",
                                  "email": "seller-one@dled.com",
                                  "phone": "11999997777",
                                  "companyName": "Equipe Comercial",
                                  "password": "Seller@123",
                                  "role": "SELLER",
                                  "active": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(get("/v1/users/{id}", id)
                        .cookie(authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("seller-one"))
                .andExpect(jsonPath("$.role").value("SELLER"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        String response = mockMvc.perform(post("/v1/users")
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Equipe Suporte",
                                  "username": "support-user",
                                  "email": "support-user@dled.com",
                                  "phone": "11912345678",
                                  "companyName": "Suporte",
                                  "password": "Support@123",
                                  "role": "EMPLOY",
                                  "active": true
                                }
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(put("/v1/users/{id}", id)
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Equipe Suporte Atualizada",
                                  "username": "support-user",
                                  "email": "support-user@dled.com",
                                  "phone": "11912345679",
                                  "companyName": "Suporte Premium",
                                  "password": "Updated@123",
                                  "role": "USER",
                                  "active": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Equipe Suporte Atualizada"))
                .andExpect(jsonPath("$.companyName").value("Suporte Premium"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        String response = mockMvc.perform(post("/v1/users")
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Usuario Temporario",
                                  "username": "temp-user",
                                  "email": "temp-user@dled.com",
                                  "phone": "11911112222",
                                  "companyName": "Operacao",
                                  "password": "Temp@1234",
                                  "role": "USER",
                                  "active": true
                                }
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(delete("/v1/users/{id}", id)
                        .cookie(authCookie()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/users/{id}", id)
                        .cookie(authCookie()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldRejectWeakPassword() throws Exception {
        mockMvc.perform(post("/v1/users")
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Usuario Fraco",
                                  "username": "weak-user",
                                  "email": "weak-user@dled.com",
                                  "phone": "11911113333",
                                  "companyName": "Operacao",
                                  "password": "12345678",
                                  "role": "USER",
                                  "active": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Password must be 8 to 20 characters long and contain uppercase, lowercase, number and special character."));
    }
}
