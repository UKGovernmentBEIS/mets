package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftData;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftDataDetails;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerAircraftDataSectionValidator;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;

import java.time.LocalDate;
import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaAircraftDataSectionValidatorTest {

    @InjectMocks
    private AviationAerCorsiaAircraftDataSectionValidator validator;

    @Mock
    private AviationAerAircraftDataSectionValidator aircraftDataSectionValidator;

    @Test
    void validate_valid() {

        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .aviationAerAircraftData(AviationAerAircraftData.builder()
                                .aviationAerAircraftDataDetails(Set.of(AviationAerAircraftDataDetails.builder()
                                                .aircraftTypeDesignator("icao")
                                                .subType("subType")
                                                .ownerOrLessor("owner")
                                                .registrationNumber("registrationNr1")
                                                .startDate(LocalDate.now())
                                                .endDate(LocalDate.now())
                                                .build(),
                                        AviationAerAircraftDataDetails.builder()
                                                .aircraftTypeDesignator("icao")
                                                .subType("subType")
                                                .ownerOrLessor("owner")
                                                .registrationNumber("registrationNr2")
                                                .startDate(LocalDate.now())
                                                .endDate(LocalDate.now())
                                                .build()
                                ))
                                .build())
                        .build())
                .reportingYear(Year.of(LocalDate.now().getYear()))
                .build();

        when(aircraftDataSectionValidator.validateAircaftData(aerContainer.getAer().getAviationAerAircraftData().getAviationAerAircraftDataDetails(),
                aerContainer.getReportingYear())).thenReturn(AviationAerValidationResult.validAviationAer());
        final AviationAerValidationResult actual = validator.validate(aerContainer);

        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }
}
