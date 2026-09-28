package com.samreen.medicationsafety.auth.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.samreen.medicationsafety.auth.controller.AdminController;
import com.samreen.medicationsafety.auth.entity.Role;
import com.samreen.medicationsafety.auth.entity.User;
import com.samreen.medicationsafety.config.SecurityConfig;
import com.samreen.medicationsafety.controller.MedicationAnalysisController;
import com.samreen.medicationsafety.mapper.MedicationAnalysisMapper;
import com.samreen.medicationsafety.service.MedicationAnalysisService;

@WebMvcTest(
    controllers = {
        MedicationAnalysisController.class,
        AdminController.class
    },
    properties = {
        "app.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Mw==",
        "app.jwt.expiration-ms=60000"
    }
)
@Import({
    SecurityConfig.class,
    JwtAuthenticationFilter.class,
    JwtService.class
})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private MedicationAnalysisService medicationAnalysisService;

    @MockitoBean
    private MedicationAnalysisMapper medicationAnalysisMapper;

    @Test
    void protectedEndpointWithoutJwtShouldReturn401()
            throws Exception {

        mockMvc.perform(
                get("/api/analyses")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void userJwtShouldAccessAnalysisEndpoint()
            throws Exception {

        String token = createToken(Role.USER);

        mockMvc.perform(
                get("/api/analyses")
                    .header(
                        "Authorization",
                        "Bearer " + token
                    )
        )
        .andExpect(status().isOk());
    }

    @Test
    void userJwtShouldNotAccessAdminEndpoint()
            throws Exception {

        String token = createToken(Role.USER);

        mockMvc.perform(
                get("/api/admin/status")
                    .header(
                        "Authorization",
                        "Bearer " + token
                    )
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void adminJwtShouldAccessAdminEndpoint()
            throws Exception {

        String token = createToken(Role.ADMIN);

        mockMvc.perform(
                get("/api/admin/status")
                    .header(
                        "Authorization",
                        "Bearer " + token
                    )
        )
        .andExpect(status().isOk());
    }

    @Test
    void adminJwtShouldAccessAnalysisEndpoint()
            throws Exception {

        String token = createToken(Role.ADMIN);

        mockMvc.perform(
                get("/api/analyses")
                    .header(
                        "Authorization",
                        "Bearer " + token
                    )
        )
        .andExpect(status().isOk());
    }

    @Test
    void invalidJwtShouldReturn401()
            throws Exception {

        mockMvc.perform(
                get("/api/analyses")
                    .header(
                        "Authorization",
                        "Bearer invalid.jwt.token"
                    )
        )
        .andExpect(status().isUnauthorized());
    }

    private String createToken(Role role) {

        User user = new User();
        user.setName("Security Test");
        user.setEmail(
                role.name().toLowerCase()
                        + "@example.com"
        );
        user.setRole(role);

        return jwtService.generateToken(user);
    }
}
