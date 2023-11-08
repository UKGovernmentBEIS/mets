package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchCriteria;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchResults;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.AircraftTypeRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AircraftTypeQueryServiceTest {

    @InjectMocks
    private AircraftTypeQueryService aircraftTypeQueryService;

    @Mock
    private AircraftTypeRepository aircraftTypeRepository;

    @Test
    void getAircraftTypesBySearchCriteria() {
        AircraftTypeSearchResults searchResults = AircraftTypeSearchResults.builder()
            .aircraftTypes(List.of(new AircraftTypeDTO("manufacturer","model", "icao")))
            .total(2L)
            .build();

        AircraftTypeSearchCriteria searchCriteria = AircraftTypeSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
            .build();

        when(aircraftTypeRepository.findBySearchCriteria(searchCriteria)).thenReturn(searchResults);

        assertEquals(searchResults, aircraftTypeQueryService.getAircraftTypesBySearchCriteria(searchCriteria));
    }

    @Test
    void areDesignatorCodesValid() {
        List<String> designatorCodes = List.of("ICAO1", "ICAO2");
        when(aircraftTypeRepository.findInvalidDesignatorCodes(designatorCodes)).thenReturn(Collections.emptyList());
        assertTrue(aircraftTypeQueryService.findInvalidDesignatorCodes(designatorCodes).isEmpty());
    }
}