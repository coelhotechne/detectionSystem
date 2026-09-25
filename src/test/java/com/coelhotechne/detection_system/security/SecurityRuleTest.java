package com.coelhotechne.detection_system.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "dev"})
class SecurityRuleTest {

    @Autowired
    MockMvc mvc;

    @Test
    void deviceAuthIsPublic() throws Exception {
        mvc.perform(post("/api/v1/cam/{camId}/auth", UUID.randomUUID()))
                .andExpect(status().is(not(401)));
    }

    @Test
    @WithMockUser(roles = "HOME_USER")
    void homologationForbiddenForHomeUser() throws Exception {
        mvc.perform(post("/api/v1/cam/{camId}/homologation/test", UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void homologationAllowedForTechnician() throws Exception {
        mvc.perform(post("/api/v1/cam/{camId}/homologation/test", UUID.randomUUID()))
                .andExpect(status().is(not(403)));
    }
}