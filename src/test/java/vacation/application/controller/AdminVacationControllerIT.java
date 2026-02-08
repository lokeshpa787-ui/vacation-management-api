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
class AdminVacationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminEndpoint_blocksWorker() throws Exception {
        mockMvc.perform(get("/api/v1/admin/requests")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(() -> "ROLE_WORKER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_allowsManager() throws Exception {
        mockMvc.perform(get("/api/v1/admin/requests")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(() -> "ROLE_MANAGER")))
                .andExpect(status().isOk());
    }
}



