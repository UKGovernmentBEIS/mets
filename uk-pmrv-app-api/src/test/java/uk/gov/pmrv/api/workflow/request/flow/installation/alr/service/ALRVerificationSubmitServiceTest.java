package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRValidationService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class ALRVerificationSubmitServiceTest {

    @InjectMocks
    private ALRVerificationSubmitService service;

    @Mock
    private ALRValidationService alrValidationService;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void applySaveAction() {

        final ALRRequestPayload alrRequestPayload = ALRRequestPayload.builder()
                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD).build();

        final Long accountId = 100L;
        final String requestId = "requestId";
        final Long verificationBodyId = 101L;
        final UUID attachmentId = UUID.randomUUID();

        final Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .payload(alrRequestPayload)
                .verificationBodyId(verificationBodyId)
                .build();

        final ALRVerificationData verificationData = ALRVerificationData.builder()
                .opinionStatement(ALRVerificationOpinionStatement
                        .builder()
                        .opinionStatementFiles(Set.of(attachmentId))
                        .notes("Test")
                        .build())
                .build();

        final ALRApplicationVerificationSaveRequestTaskActionPayload actionPayload =
                ALRApplicationVerificationSaveRequestTaskActionPayload.builder()
                        .verificationData(verificationData)
                        .verificationSectionsCompleted(Map.of("group", List.of(true)))
                        .build();

        ALRApplicationVerificationSubmitRequestTaskPayload taskPayload =
                ALRApplicationVerificationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.ALR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)
                        .verificationAttachments(Map.of(attachmentId, "attachment"))
                        .verificationReport(ALRVerificationReport.builder()
                                .verificationBodyDetails(VerificationBodyDetails.builder()
                                        .name("nameNew")
                                        .accreditationReferenceNumber("accreditationRefNumNew")
                                        .address(AddressDTO.builder().city("cityNew").country("countryNew").line1("lineNew").build())
                                        .emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS, EmissionTradingScheme.CORSIA))
                                        .build())
                                .build())
                        .build();

        RequestTask requestTask = RequestTask.builder().request(request).payload(taskPayload).build();

        service.applySaveAction(actionPayload, requestTask);

        ALRApplicationVerificationSubmitRequestTaskPayload payloadSaved =
                (ALRApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getVerificationReport().getVerificationData())
                .isEqualTo(verificationData);
        assertThat(payloadSaved.getVerificationSectionsCompleted())
                .isEqualTo(actionPayload.getVerificationSectionsCompleted());
        assertThat(payloadSaved.getVerificationReport().getVerificationBodyDetails())
                .isEqualTo(taskPayload.getVerificationReport().getVerificationBodyDetails());

        assertThat(((ALRRequestPayload) request.getPayload()).getVerificationReport())
                .isEqualTo(taskPayload.getVerificationReport());

        assertThat(((ALRRequestPayload) request.getPayload()).isVerificationPerformed()).isFalse();

        assertThat(((ALRRequestPayload) request.getPayload()).getVerificationReport().getVerificationBodyId())
                .isEqualTo(verificationBodyId);

        assertThat(((ALRRequestPayload) request.getPayload()).getVerificationSectionsCompleted())
                .containsExactlyEntriesOf(actionPayload.getVerificationSectionsCompleted());

        assertThat(((ALRRequestPayload) request.getPayload()).getVerificationAttachments())
                .containsExactlyEntriesOf(taskPayload.getVerificationAttachments());
    }

    @Test
    void sendToOperator() {
        final UUID attachmentId = UUID.randomUUID();
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();
        final Map<UUID, String> verificationAttachments = Map.of(attachmentId, "attachmentName");

        final ALR alr = ALR.builder().build();
        final ALRVerificationReport verificationReport = ALRVerificationReport.builder().build();

        Request request = Request.builder()
                .payload(ALRRequestPayload.builder().alr(alr).payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD).build())
                .build();

        ALRApplicationVerificationSubmitRequestTaskPayload taskPayload =
                ALRApplicationVerificationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.ALR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)
                        .alr(alr)
                        .verificationReport(verificationReport)
                        .verificationAttachments(verificationAttachments)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        ALRApplicationVerificationSubmittedRequestActionPayload actionPayload =
                ALRApplicationVerificationSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.ALR_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD)
                        .alr(alr)
                        .verificationReport(verificationReport)
                        .installationOperatorDetails(installationOperatorDetails)
                        .verificationAttachments(verificationAttachments)
                        .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);

        Request savedRequest = Request.builder()
                .accountId(1L)
                .payload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .alr(alr)
                        .verificationReport(verificationReport)
                        .verificationPerformed(true)
                        .verificationSectionsCompleted(Map.of())
                        .verificationAttachments(verificationAttachments)
                        .build())
                .build();

        service.sendToOperator(requestTask, appUser);

        verify(alrValidationService, times(1))
                .validateVerificationReport(verificationReport);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(requestTask.getRequest(),
                actionPayload, RequestActionType.ALR_APPLICATION_VERIFICATION_SUBMITTED, appUser.getUserId());

        assertThat(request.getPayload()).isEqualTo(savedRequest.getPayload());
    }

    private InstallationOperatorDetails getInstallationOperatorDetails() {
        return InstallationOperatorDetails.builder()
                .installationName("Account name")
                .siteName("Site name")
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
                .operator("le")
                .operatorType(LegalEntityType.LIMITED_COMPANY)
                .companyReferenceNumber("408812")
                .operatorDetailsAddress(AddressDTO.builder()
                        .line1("line1")
                        .city("city")
                        .country("GR")
                        .postcode("postcode")
                        .build())
                .build();
    }
}
