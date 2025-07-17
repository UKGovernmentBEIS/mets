package uk.gov.pmrv.api.aviationreporting.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerContainer;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerSubmitParams;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationAerRepository;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerValidatorService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.time.Year;
import java.util.ArrayList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerServiceTest {

    @InjectMocks
    private AviationAerService aviationAerService;

    @Mock
    private AviationAerRepository aerRepository;

    @Mock
    private AviationAerValidatorService aerValidatorService;

    @Mock
    private AviationReportableEmissionsService reportableEmissionsService;

    @Spy
    private ArrayList<AviationAerSubmittedEmissionsCalculationService> aviationAerSubmittedEmissionsCalculationServices;

    @Mock
    private TestAviationAerSubmittedEmissionsCalculationService testAerTradingSchemeValidatorService;

    @BeforeEach
    void setUp() {
        aviationAerSubmittedEmissionsCalculationServices.add(testAerTradingSchemeValidatorService);
    }

    @Test
    void submitAer() {
        EmissionTradingScheme scheme = EmissionTradingScheme.UK_ETS_AVIATION;
        Long accountId = 100L;
        Year reportingYear = Year.of(2023);

        AviationAerContainer aerContainer = Mockito.mock(AviationAerContainer.class);
        when(aerContainer.getScheme()).thenReturn(scheme);
        when(aerContainer.getReportingYear()).thenReturn(reportingYear);
        when(aerContainer.getReportingRequired()).thenReturn(Boolean.TRUE);

        AviationAerTotalReportableEmissions totalReportableEmissions = Mockito.mock(AviationAerTotalReportableEmissions.class);
        AviationAerSubmittedEmissions submittedEmissions = Mockito.mock(AviationAerSubmittedEmissions.class);

        when(reportableEmissionsService.updateReportableEmissions(aerContainer, accountId, true)).thenReturn(totalReportableEmissions);
        when(testAerTradingSchemeValidatorService.getEmissionTradingScheme()).thenReturn(EmissionTradingScheme.UK_ETS_AVIATION);
        when(testAerTradingSchemeValidatorService.calculateSubmittedEmissions(aerContainer)).thenReturn(submittedEmissions);

        AviationAerSubmitParams params = AviationAerSubmitParams.builder()
            .aerContainer(aerContainer)
            .accountId(accountId)
            .build();
        AviationAerEntity aerEntity = AviationAerEntity.builder()
            .id("AEM100-2023")
            .aerContainer(aerContainer)
            .accountId(accountId)
            .year(reportingYear)
            .build();

        //invoke
        aviationAerService.submitAer(params);

        //verify
        verify(reportableEmissionsService, times(1)).updateReportableEmissions(aerContainer, accountId, true);
        verify(testAerTradingSchemeValidatorService, times(1)).getEmissionTradingScheme();
        verify(testAerTradingSchemeValidatorService, times(1)).calculateSubmittedEmissions(aerContainer);
        verify(aerValidatorService, times(1)).validate(accountId, aerContainer);
        verify(aerRepository, times(1)).save(aerEntity);
    }

    @Test
    void submitAer_when_no_reporting_required() {
        Long accountId = 100L;
        Year reportingYear = Year.of(2023);

        AviationAerContainer aerContainer = Mockito.mock(AviationAerContainer.class);
        when(aerContainer.getReportingYear()).thenReturn(reportingYear);
        when(aerContainer.getReportingRequired()).thenReturn(Boolean.FALSE);

        AviationAerSubmitParams params = AviationAerSubmitParams.builder()
            .aerContainer(aerContainer)
            .accountId(accountId)
            .build();

        //invoke
        aviationAerService.submitAer(params);

        //verify
        verifyNoInteractions(reportableEmissionsService, testAerTradingSchemeValidatorService, aerValidatorService, aerRepository);
    }

    private static class TestAviationAerSubmittedEmissionsCalculationService implements AviationAerSubmittedEmissionsCalculationService<AviationAerContainer, AviationAerSubmittedEmissions> {

        @Override
        public AviationAerSubmittedEmissions calculateSubmittedEmissions(AviationAerContainer aerContainer) {
            return null;
        }

        @Override
        public EmissionTradingScheme getEmissionTradingScheme() {
            return null;
        }
    }
}