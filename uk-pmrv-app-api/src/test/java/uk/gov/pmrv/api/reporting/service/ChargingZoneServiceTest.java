package uk.gov.pmrv.api.reporting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.reporting.domain.ChargingZone;
import uk.gov.pmrv.api.reporting.domain.dto.ChargingZoneDTO;
import uk.gov.pmrv.api.reporting.repository.ChargingZoneRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChargingZoneServiceTest {

    @InjectMocks
    private ChargingZoneService chargingZoneService;

    @Mock
    private ChargingZoneRepository chargingZoneRepository;

    @Test
    void getChargingZonesByPostCode() {
        String code = "AB12";
        List<ChargingZone> chargingZones = List.of(
            ChargingZone.builder().code("EA").name("Eastern").build(),
            ChargingZone.builder().code("SO").name("Southern").build()
        );

        when(chargingZoneRepository.findByPostCodesCodeIgnoreCase(code)).thenReturn(chargingZones);

        List<ChargingZoneDTO> result = chargingZoneService.getChargingZonesByPostCode(code);

        assertThat(result).isNotEmpty();
        assertEquals(2, result.size());
        assertThat(result).extracting(ChargingZoneDTO::getCode)
            .containsExactlyInAnyOrderElementsOf(chargingZones.stream().map(ChargingZone::getCode).collect(Collectors.toList()));
        assertThat(result).extracting(ChargingZoneDTO::getName)
            .containsExactlyInAnyOrderElementsOf(chargingZones.stream().map(ChargingZone::getName).collect(Collectors.toList()));

        verify(chargingZoneRepository, times(1)).findByPostCodesCodeIgnoreCase(code);
    }

    @Test
    void getChargingZonesByPostCode_returns_empty_list() {
        String code = "AB12";
        when(chargingZoneRepository.findByPostCodesCodeIgnoreCase(code)).thenReturn(List.of());

        List<ChargingZoneDTO> result = chargingZoneService.getChargingZonesByPostCode(code);

        assertThat(result).isEmpty();
        verify(chargingZoneRepository, times(1)).findByPostCodesCodeIgnoreCase(code);
    }
}