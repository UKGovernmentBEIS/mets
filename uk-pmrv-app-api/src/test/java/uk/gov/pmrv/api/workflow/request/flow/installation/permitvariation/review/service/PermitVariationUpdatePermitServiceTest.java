package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationUpdatePermitService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitVariationUpdatePermitServiceTest {

	@InjectMocks
    private PermitVariationUpdatePermitService cut;

    @Mock
    private RequestService requestService;
    
    @Mock
    private PermitService permitService;

	@Mock
	private InstallationAccountUpdateService installationAccountUpdateService;

	@Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    
    @Test
    void updatePermitUponGrantedDetermination() {
    	Long accountId = 1L;
    	String requestId = "requestId";
    	Permit permit = Permit.builder()
				.abbreviations(Abbreviations.builder().exist(true).build())
				.estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(BigDecimal.valueOf(50000)).build())
				.build();
    	LocalDate activationDate = LocalDate.now().plusDays(10);
    	PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
    			.permitType(PermitType.GHGE)
    			.permit(permit)
    			.determination(PermitVariationGrantDetermination.builder()
    					.type(DeterminationType.GRANTED)
    					.activationDate(activationDate)
    					.build())
    			.build();
    	Request request = Request.builder()
    			.payload(requestPayload)
    			.accountId(accountId)
    			.build();
    	InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
    			.installationName("acc1").companyReferenceNumber("ref1")
    			.build();
    	
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);
    	
    	cut.updatePermitUponGrantedDetermination(requestId);
    	
    	verify(requestService, times(1)).findRequestById(requestId);
    	verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
    	verify(permitService, times(1)).updatePermit(PermitContainer.builder()
    			.permit(permit)
    			.permitType(PermitType.GHGE)
    			.activationDate(activationDate)
    			.installationOperatorDetails(installationOperatorDetails)
    			.build(), accountId);
		verify(installationAccountUpdateService, times(1)).updateAccountUponPermitVariationGranted(accountId,
				EmitterType.GHGE, BigDecimal.valueOf(50000));
	}
}
