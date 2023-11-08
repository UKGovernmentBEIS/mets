package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler.PermitVariationApplicationSubmitRegulatorLedRequestTaskInitializer;

@ExtendWith(MockitoExtension.class)
class PermitVariationApplicationSubmitRegulatorLedRequestTaskInitializerTest {

	@InjectMocks
    private PermitVariationApplicationSubmitRegulatorLedRequestTaskInitializer handler;
	
	@Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	
	@Mock
    private PermitQueryService permitQueryService;
	
	@Test
	void initializePayload_not_determinated() {
		UUID attachment = UUID.randomUUID();
		Long accountId = 1L;
		Request request = Request.builder().accountId(accountId).payload(PermitVariationRequestPayload.builder().build()).build();
		InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
        		.installationName("Account name")
				.siteName("siteName")
				.installationLocation(LocationOnShoreDTO.builder()
						.type(LocationType.ONSHORE)
						.gridReference("ST330000")
						.address(AddressDTO.builder()
								.line1("line1")
								.city("city")
								.country("GB")
								.postcode("postcode")
								.build())
						.build())
        		.operator("operator")
				.operatorType(LegalEntityType.LIMITED_COMPANY)
				.companyReferenceNumber("408812")
				.operatorDetailsAddress(AddressDTO.builder()
						.line1("line1")
						.city("city")
						.country("GR")
						.postcode("postcode")
						.build())
        		.build();
		PermitContainer permitContainer = PermitContainer.builder()
				.permitType(PermitType.GHGE)
				.permit(Permit.builder()
						.abbreviations(Abbreviations.builder().exist(false).build())
						.build())
				.permitAttachments(Map.of(attachment, "att"))
				.build();
		
		when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
		when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
		.thenReturn(installationOperatorDetails);
		
		RequestTaskPayload result = handler.initializePayload(request);
		
		InstallationOperatorDetails actualInstallationOperatorDetails =
                ((PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload)result).getInstallationOperatorDetails();
		
		verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
		verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
		
		
		PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload payload = (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) result;
		assertThat(payload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD);
		assertThat(actualInstallationOperatorDetails).isEqualTo(installationOperatorDetails);
		assertThat(payload.getPermitType()).isEqualTo(permitContainer.getPermitType());
		assertThat(payload.getPermit()).isEqualTo(permitContainer.getPermit());
		assertThat(payload.getPermitAttachments()).isEqualTo(permitContainer.getPermitAttachments());
		assertThat(payload.getOriginalPermitContainer()).isEqualTo(permitContainer);
	}
	
	@Test
	void initializePayload_determinated() {
		Long accountId = 1L;
		Request request = Request.builder().accountId(accountId)
				.payload(PermitVariationRequestPayload.builder()
						.determinationRegulatorLed(PermitVariationRegulatorLedGrantDetermination.builder()
								.logChanges("logChanges")
								.build())
						.build()).build();
		InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
        		.installationName("Account name")
				.siteName("siteName")
				.installationLocation(LocationOnShoreDTO.builder()
						.type(LocationType.ONSHORE)
						.gridReference("ST330000")
						.address(AddressDTO.builder()
								.line1("line1")
								.city("city")
								.country("GB")
								.postcode("postcode")
								.build())
						.build())
        		.operator("operator")
				.operatorType(LegalEntityType.LIMITED_COMPANY)
				.companyReferenceNumber("408812")
				.operatorDetailsAddress(AddressDTO.builder()
						.line1("line1")
						.city("city")
						.country("GR")
						.postcode("postcode")
						.build())
        		.build();
		
		when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
		.thenReturn(installationOperatorDetails);
		
		RequestTaskPayload result = handler.initializePayload(request);
		
		InstallationOperatorDetails actualInstallationOperatorDetails =
                ((PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload)result).getInstallationOperatorDetails();
		
		verifyNoInteractions(permitQueryService);
		verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
		
		
		PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload payload = (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) result;
		assertThat(payload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD);
		assertThat(actualInstallationOperatorDetails).isEqualTo(installationOperatorDetails);
		assertThat(payload.getDetermination()).isEqualTo(PermitVariationRegulatorLedGrantDetermination.builder()
								.logChanges("logChanges")
								.build());
	}
	
	@Test
	void getRequestTaskTypes() {
		assertThat(handler.getRequestTaskTypes()).containsExactlyInAnyOrder(
				RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT);
	}
}
