package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation.WasteQDRValidationService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class WasteQDRSubmitServiceTest {

    @InjectMocks
    private WasteQDRSubmitService service;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private WasteQDRValidationService wasteQDRValidationService;

    @Mock
    private RequestService requestService;


    @Test
    void applySaveAction() {

        final Map<String, Boolean> expectedSectionsCompleted = new HashMap<>();
        expectedSectionsCompleted.put("test",false);

        final WasteQDRApplicationSubmitRequestTaskPayload expectedTaskPayload =
                WasteQDRApplicationSubmitRequestTaskPayload
                        .builder()
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(expectedTaskPayload)
                .build();

        final WasteQDRApplicationSaveRequestTaskActionPayload expectedTaskActionPayload =
                WasteQDRApplicationSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.WASTE_QDR_APPLICATION_SAVE_PAYLOAD)
                        .qdr(WasteQDR.builder().build())
                        .wasteQDRSectionsCompleted(expectedSectionsCompleted)
                        .build();

        service.applySaveAction(requestTask, expectedTaskActionPayload);

        assertEquals(expectedTaskPayload.getQdr(), expectedTaskActionPayload.getQdr());
        assertEquals(expectedTaskPayload.getWasteQDRSectionsCompleted(), expectedTaskActionPayload.getWasteQDRSectionsCompleted());
    }

    @Test
    void submitToRegulator() {
        // Arrange
        final long accountId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        WasteQDR qdr = WasteQDR.builder().build();

        WasteQDRRequestPayload requestPayload = WasteQDRRequestPayload.builder()
                .payloadType(RequestPayloadType.WASTE_QDR_REQUEST_PAYLOAD)
                .build();

        Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .metadata(WasteQDRRequestMetaData.builder()
                        .type(RequestMetadataType.WASTE_QDR)
                        .build())
                .build();

        Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "Attachment 1");
        Map<String, Boolean> sectionsCompleted = Map.of("section1", true);

        WasteQDRApplicationSubmitRequestTaskPayload taskPayload = WasteQDRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.WASTE_QDR_SUBMIT_PAYLOAD)
                .qdr(qdr)
                .wasteQDRAttachments(attachments)
                .wasteQDRSectionsCompleted(sectionsCompleted)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        WasteQDRRequestPayload expectedPayload = WasteQDRRequestPayload.builder()
                .payloadType(RequestPayloadType.WASTE_QDR_REQUEST_PAYLOAD)
                .qdr(qdr)
                .wasteQDRAttachments(attachments)
                .wasteQDRSectionsCompleted(sectionsCompleted)
                .build();

        WasteQDRApplicationSubmittedRequestActionPayload actionPayload =
                WasteQDRApplicationSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.WASTE_QDR_APPLICATION_SUBMITTED_PAYLOAD)
                        .qdr(qdr)
                        .installationOperatorDetails(installationOperatorDetails)
                        .wasteQDRAttachments(attachments)
                        .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        // Act
        service.submitToRegulator(requestTask, appUser);

        // Assert
        verify(wasteQDRValidationService, times(1)).validateWasteQDR(qdr);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(accountId);
        verify(requestService, times(1)).addActionToRequest(
                eq(request),
                any(WasteQDRApplicationSubmittedRequestActionPayload.class),
                eq(RequestActionType.WASTE_QDR_APPLICATION_SUBMITTED),
                eq(appUser.getUserId())
        );

        assertThat(request.getPayload()).isEqualTo(expectedPayload);
    }

    private InstallationOperatorDetails getInstallationOperatorDetails() {
        return InstallationOperatorDetails.builder()
                .installationName("Test Installation")
                .build();
    }

    @Test
    void createApplicationSubmittedRequestActionPayload_setsInstallationDetailsAndAttachments() {
        // Arrange
        final long accountId = 1L;
        WasteQDR qdr = WasteQDR.builder().build();
        Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "Attachment 1");

        Request request = Request.builder()
                .accountId(accountId)
                .build();

        WasteQDRApplicationSubmitRequestTaskPayload taskPayload =
                WasteQDRApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.WASTE_QDR_SUBMIT_PAYLOAD)
                        .qdr(qdr)
                        .wasteQDRAttachments(attachments)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        WasteQDRApplicationSubmittedRequestActionPayload expectedPayload =
                WasteQDRApplicationSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.WASTE_QDR_APPLICATION_SUBMITTED_PAYLOAD)
                        .qdr(qdr)
                        .installationOperatorDetails(installationOperatorDetails)
                        .wasteQDRAttachments(attachments)
                        .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        // Act
        WasteQDRApplicationSubmittedRequestActionPayload result =
                service.createApplicationSubmittedRequestActionPayload(
                        requestTask,
                        taskPayload,
                        RequestActionPayloadType.WASTE_QDR_APPLICATION_SUBMITTED_PAYLOAD);

        // Assert
        assertThat(result).isEqualTo(expectedPayload);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(accountId);
    }
    @Test
    void submitWasteQDR_populatesFieldsAndAddsRequestAction() {
        // Arrange
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final WasteQDR qdr = WasteQDR.builder().build();
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "Attachment 1");
        final Map<String, Boolean> sectionsCompleted = Map.of("section1", true);

        WasteQDRRequestPayload requestPayload = WasteQDRRequestPayload.builder()
                .payloadType(RequestPayloadType.WASTE_QDR_REQUEST_PAYLOAD)
                .build();

        WasteQDRApplicationSubmitRequestTaskPayload taskPayload = WasteQDRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.WASTE_QDR_SUBMIT_PAYLOAD)
                .qdr(qdr)
                .wasteQDRAttachments(attachments)
                .wasteQDRSectionsCompleted(sectionsCompleted)
                .build();

        Request request = Request.builder()
                .payload(requestPayload)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        WasteQDRApplicationSubmittedRequestActionPayload actionPayload =
                WasteQDRApplicationSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.WASTE_QDR_APPLICATION_SUBMITTED_PAYLOAD)
                        .build();

        // Act
        service.submitWasteQDR(
                requestPayload,
                requestTask,
                appUser,
                RequestActionType.WASTE_QDR_APPLICATION_SUBMITTED,
                actionPayload,
                sectionsCompleted);

        // Assert
        assertThat(requestPayload.getQdr()).isEqualTo(qdr);
        assertThat(requestPayload.getWasteQDRAttachments()).isEqualTo(attachments);
        assertThat(requestPayload.getWasteQDRSectionsCompleted()).isEqualTo(sectionsCompleted);

        verify(requestService, times(1)).addActionToRequest(
                eq(request),
                eq(actionPayload),
                eq(RequestActionType.WASTE_QDR_APPLICATION_SUBMITTED),
                eq(appUser.getUserId())
        );
    }
}
