package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.noncompliance.service.AviationNonComplianceSendOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.noncompliance.service.InstallationNonComplianceSendOfficialNoticeService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class NonComplianceSendOfficialNoticeServiceDelegatorTest {

	private NonComplianceSendOfficialNoticeServiceDelegator serviceDelegator;
	private InstallationNonComplianceSendOfficialNoticeService installationNonComplianceSendOfficialNoticeService;
	private AviationNonComplianceSendOfficialNoticeService aviationNonComplianceSendOfficialNoticeService;
	
	@BeforeAll
    void setUp() {
		installationNonComplianceSendOfficialNoticeService = mock(InstallationNonComplianceSendOfficialNoticeService.class);
		aviationNonComplianceSendOfficialNoticeService = mock(AviationNonComplianceSendOfficialNoticeService.class);

        serviceDelegator = new NonComplianceSendOfficialNoticeServiceDelegator(
            List.of(aviationNonComplianceSendOfficialNoticeService, installationNonComplianceSendOfficialNoticeService));

        when(aviationNonComplianceSendOfficialNoticeService.getAccountType()).thenReturn(AccountType.AVIATION);
        when(installationNonComplianceSendOfficialNoticeService.getAccountType()).thenReturn(AccountType.INSTALLATION);
    }
	
	@Test
    void sendOfficialNotice_when_aviation() {
		UUID uuid = UUID.randomUUID();
		Request request = Request.builder().type(RequestType.AVIATION_NON_COMPLIANCE).build();
		NonComplianceDecisionNotification decisionNotification =
	            NonComplianceDecisionNotification.builder().operators(Set.of("operator")).build();

        serviceDelegator.sendOfficialNotice(uuid, request, decisionNotification);

        verify(aviationNonComplianceSendOfficialNoticeService, times(1))
            .sendOfficialNotice(uuid, request, decisionNotification);
    }

    @Test
    void sendOfficialNotice_when_installation() {
    	UUID uuid = UUID.randomUUID();
		Request request = Request.builder().type(RequestType.NON_COMPLIANCE).build();
		NonComplianceDecisionNotification decisionNotification =
	            NonComplianceDecisionNotification.builder().operators(Set.of("operator")).build();

        serviceDelegator.sendOfficialNotice(uuid, request, decisionNotification);

        verify(installationNonComplianceSendOfficialNoticeService, times(1))
            .sendOfficialNotice(uuid, request, decisionNotification);
    }
}
