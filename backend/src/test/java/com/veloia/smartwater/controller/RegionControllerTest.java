package com.veloia.smartwater.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.region.RegionRequest;
import com.veloia.smartwater.dto.region.RegionResponse;
import com.veloia.smartwater.exception.DuplicateResourceException;
import com.veloia.smartwater.exception.GlobalExceptionHandler;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.service.RegionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegionController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegionService regionService;

    private RegionRequest validRequest;
    private RegionResponse mockResponse;

    @BeforeEach
    void setUp() {
        validRequest = new RegionRequest();
        validRequest.setCode("REG-TYO-01");
        validRequest.setName("Tokyo Chiyoda Water District");
        validRequest.setPrefecture("Tokyo");
        validRequest.setDescription("Central administrative district");
        validRequest.setStatus("ACTIVE");

        mockResponse = new RegionResponse();
        mockResponse.setId(10L);
        mockResponse.setCode("REG-TYO-01");
        mockResponse.setName("Tokyo Chiyoda Water District");
        mockResponse.setPrefecture("Tokyo");
        mockResponse.setDescription("Central administrative district");
        mockResponse.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("POST /api/regions returns 201 Created on valid input")
    void createRegion_ValidInput_Returns201() throws Exception {
        when(regionService.createRegion(any(RegionRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.code").value("REG-TYO-01"))
                .andExpect(jsonPath("$.data.name").value("Tokyo Chiyoda Water District"));
    }

    @Test
    @DisplayName("POST /api/regions returns 400 Bad Request when validation fails")
    void createRegion_InvalidInput_Returns400() throws Exception {
        RegionRequest invalidRequest = new RegionRequest();
        invalidRequest.setCode(""); // Blank code triggers @NotBlank
        invalidRequest.setName("");

        mockMvc.perform(post("/api/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("POST /api/regions returns 409 Conflict when duplicate code exists")
    void createRegion_DuplicateCode_Returns409() throws Exception {
        when(regionService.createRegion(any(RegionRequest.class)))
                .thenThrow(new DuplicateResourceException("Region", "code", "REG-TYO-01"));

        mockMvc.perform(post("/api/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Region already exists with code: 'REG-TYO-01'"));
    }

    @Test
    @DisplayName("GET /api/regions/{id} returns 200 OK when found")
    void getRegionById_Found_Returns200() throws Exception {
        when(regionService.getRegionById(10L)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/regions/10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("REG-TYO-01"));
    }

    @Test
    @DisplayName("GET /api/regions/{id} returns 404 Not Found when missing")
    void getRegionById_NotFound_Returns404() throws Exception {
        when(regionService.getRegionById(999L))
                .thenThrow(new ResourceNotFoundException("Region", "id", 999L));

        mockMvc.perform(get("/api/regions/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Region not found with id: '999'"));
    }

    @Test
    @DisplayName("GET /api/regions returns paginated 200 OK list")
    void getAllRegions_ReturnsPaginatedList() throws Exception {
        PageResponse<RegionResponse> pageResponse = PageResponse.of(
                new PageImpl<>(List.of(mockResponse), PageRequest.of(0, 10), 1)
        );
        when(regionService.getAllRegions(any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/regions?page=0&size=10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].code").value("REG-TYO-01"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("DELETE /api/regions/{id} returns 200 OK")
    void deleteRegion_Success_Returns200() throws Exception {
        doNothing().when(regionService).deleteRegion(10L);

        mockMvc.perform(delete("/api/regions/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Region deleted successfully"));

        verify(regionService).deleteRegion(10L);
    }
}
