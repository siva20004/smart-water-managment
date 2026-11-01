package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.region.RegionRequest;
import com.veloia.smartwater.dto.region.RegionResponse;
import com.veloia.smartwater.entity.Region;
import com.veloia.smartwater.exception.DuplicateResourceException;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.mapper.RegionMapper;
import com.veloia.smartwater.repository.RegionRepository;
import com.veloia.smartwater.service.impl.RegionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    private final RegionMapper regionMapper = new RegionMapper();

    private RegionServiceImpl regionService;

    private Region sampleRegion;
    private RegionRequest sampleRequest;

    @BeforeEach
    void setUp() {
        regionService = new RegionServiceImpl(regionRepository, regionMapper);

        sampleRegion = new Region();
        sampleRegion.setId(1L);
        sampleRegion.setCode("REG-KTO-01");
        sampleRegion.setName("Kyoto Central");
        sampleRegion.setPrefecture("Kyoto");
        sampleRegion.setDescription("Primary urban district");
        sampleRegion.setStatus("ACTIVE");

        sampleRequest = new RegionRequest();
        sampleRequest.setCode("REG-KTO-01");
        sampleRequest.setName("Kyoto Central");
        sampleRequest.setPrefecture("Kyoto");
        sampleRequest.setDescription("Primary urban district");
        sampleRequest.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("createRegion successfully creates and returns RegionResponse")
    void createRegion_Success() {
        when(regionRepository.existsByCode(sampleRequest.getCode())).thenReturn(false);
        when(regionRepository.save(any(Region.class))).thenAnswer(invocation -> {
            Region r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        RegionResponse result = regionService.createRegion(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("REG-KTO-01");
        assertThat(result.getName()).isEqualTo("Kyoto Central");
        verify(regionRepository).save(any(Region.class));
    }

    @Test
    @DisplayName("createRegion throws DuplicateResourceException when code already exists")
    void createRegion_DuplicateCode_ThrowsException() {
        when(regionRepository.existsByCode(sampleRequest.getCode())).thenReturn(true);

        assertThatThrownBy(() -> regionService.createRegion(sampleRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("REG-KTO-01");

        verify(regionRepository, never()).save(any());
    }

    @Test
    @DisplayName("getRegionById returns RegionResponse when found")
    void getRegionById_Success() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(sampleRegion));

        RegionResponse result = regionService.getRegionById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Kyoto Central");
    }

    @Test
    @DisplayName("getRegionById throws ResourceNotFoundException when not found")
    void getRegionById_NotFound_ThrowsException() {
        when(regionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> regionService.getRegionById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("getRegionByCode returns RegionResponse when code exists")
    void getRegionByCode_Success() {
        when(regionRepository.findByCode("REG-KTO-01")).thenReturn(Optional.of(sampleRegion));

        RegionResponse result = regionService.getRegionByCode("REG-KTO-01");

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("REG-KTO-01");
    }

    @Test
    @DisplayName("deleteRegion succeeds when region exists")
    void deleteRegion_Success() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(sampleRegion));
        doNothing().when(regionRepository).delete(any(Region.class));

        regionService.deleteRegion(1L);

        verify(regionRepository).delete(sampleRegion);
    }

    @Test
    @DisplayName("deleteRegion throws ResourceNotFoundException when region does not exist")
    void deleteRegion_NotFound_ThrowsException() {
        when(regionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> regionService.deleteRegion(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(regionRepository, never()).delete(any(Region.class));
    }
}
