package com.chubb.claimsmanagement;

import com.chubb.claimsmanagement.claimant.entity.Claimant;
import com.chubb.claimsmanagement.claimant.repository.ClaimantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
class ClaimControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClaimantRepository claimantRepository;

    @Test
    void shouldCreateClaim() throws Exception {
        Claimant claimant = new Claimant();
        claimant.setFirstName("Jane");
        claimant.setLastName("Doe");
        claimant.setEmail("jane.doe@example.com");
        claimant.setPhone("0400000000");
        claimant.setAddress("1 Test Street");
        claimant.setPolicyNumber("POL-12345");
        Claimant savedClaimant = claimantRepository.save(claimant);

        Map<String, Object> payload = Map.of(
                "claimantId", savedClaimant.getId().toString(),
                "claimType", "MOTOR",
                "description", "Rear bumper damage after collision"
        );

        mockMvc.perform(post("/api/v1/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }
}
