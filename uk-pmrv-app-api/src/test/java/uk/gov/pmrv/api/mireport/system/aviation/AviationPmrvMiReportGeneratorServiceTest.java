package uk.gov.pmrv.api.mireport.system.aviation;

import org.junit.jupiter.api.Test;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.system.MiReportSystemType;
import uk.gov.netz.api.mireport.system.executedactions.ExecutedRequestActionsMiReportResult;
import uk.gov.netz.api.mireport.system.EmptyMiReportSystemParams;
import uk.gov.netz.api.mireport.system.MiReportSystemGeneratorDelegator;
import uk.gov.netz.api.mireport.system.MiReportSystemResult;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AviationPmrvMiReportGeneratorServiceTest {
    private final MiReportSystemGeneratorDelegator miReportSystemGeneratorDelegator = mock(MiReportSystemGeneratorDelegator.class);
    private final AviationMiReportGeneratorHandler<EmptyMiReportSystemParams> aviationMiReportGeneratorHandler = mock(AviationMiReportGeneratorHandler.class);
    private final List<AviationMiReportGeneratorHandler<EmptyMiReportSystemParams>> handlers = Collections.singletonList(aviationMiReportGeneratorHandler);

	private final AviationPmrvMiReportGeneratorService<EmptyMiReportSystemParams> service = new AviationPmrvMiReportGeneratorService<>(
			miReportSystemGeneratorDelegator, handlers);
	
    @Test
    void generateReport() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
		EmptyMiReportSystemParams reportParams = EmptyMiReportSystemParams.builder()
				.reportType(MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS).build();
        MiReportSystemResult expectedMiReportResult = ExecutedRequestActionsMiReportResult.builder().columnNames(List.of("col1")).build();

        when(miReportSystemGeneratorDelegator.generateReport(competentAuthority, reportParams, handlers)).thenReturn(
            expectedMiReportResult);

        MiReportSystemResult actualMiReportResult = service.generateReport(competentAuthority, reportParams);

        assertThat(actualMiReportResult).isEqualTo(expectedMiReportResult);
        verify(miReportSystemGeneratorDelegator, times(1)).generateReport(competentAuthority, reportParams, handlers);
    }

    @Test
    void getAccountType() {
        assertEquals(AccountType.AVIATION, service.getAccountType());
    }
}

