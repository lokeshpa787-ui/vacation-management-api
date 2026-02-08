package vacation.application.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkerVacationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    // 401
    @Test
    void workerEndpoint_requiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/worker/remaining-days"))
                .andExpect(status().isUnauthorized());
    }

    // 200
    @Test
    void workerEndpoint_allowsWorkerRole() throws Exception {
        mockMvc.perform(get("/api/v1/worker/remaining-days")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject("11111111-1111-1111-1111-111111111111"))
                                .authorities(() -> "ROLE_WORKER")))
                .andExpect(status().isOk());
    }

    // 403
    @Test
    void workerEndpoint_blocksManagerRole() throws Exception {
        mockMvc.perform(get("/api/v1/worker/remaining-days")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(() -> "ROLE_MANAGER")))
                .andExpect(status().isForbidden());
    }
}
