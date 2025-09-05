package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceAmendDetailsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCloseApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCloseJustification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDetailsAmendedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonCompliancePenalties;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceReason;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceSaveApplicationRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceApplyServiceTest {

    @InjectMocks
    private NonComplianceApplyService service;


    @Mock
    private RequestService requestService;

    @Test
    void applyCloseAction() {

        final UUID file = UUID.randomUUID();
        final String reason = "the reason";
        final NonComplianceCloseApplicationRequestTaskActionPayload taskActionPayload =
            NonComplianceCloseApplicationRequestTaskActionPayload.builder()
                .closeJustification(NonComplianceCloseJustification.builder()
                    .reason(reason)
                    .files(Set.of(file))
                    .build())
                .build();

        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder().build();
        final Request request = Request.builder().payload(requestPayload).build();

        final Map<UUID, String> nonComplianceAttachments = Map.of(file, "filename");
        final NonComplianceApplicationSubmitRequestTaskPayload requestTaskPayload =
            NonComplianceApplicationSubmitRequestTaskPayload
                .builder()
                .nonComplianceAttachments(nonComplianceAttachments)
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(requestTaskPayload)
            .build();

        service.applyCloseAction(requestTask, taskActionPayload);

        assertEquals(requestTaskPayload.getCloseJustification(), taskActionPayload.getCloseJustification());
        assertEquals(requestPayload.getCloseJustification(), taskActionPayload.getCloseJustification());
        assertEquals(requestPayload.getNonComplianceAttachments(), nonComplianceAttachments);
    }

    @Test
    void applySaveAction() {

        final NonComplianceSaveApplicationRequestTaskActionPayload taskActionPayload =
            NonComplianceSaveApplicationRequestTaskActionPayload.builder()
                .reason(NonComplianceReason.CARRYING_OUT_A_REGULATED_ACTIVITY_WITHOUT_A_PERMIT)
                .nonComplianceDate(LocalDate.of(2021, 1, 1))
                .complianceDate(LocalDate.of(2022, 1, 1))
                .comments("comments")
                .selectedRequests(Set.of("reqId"))
                .nonCompliancePenalties(NonCompliancePenalties.builder()
                    .civilPenalty(true)
                    .noticeOfIntent(false)
                    .dailyPenalty(false).build())
                .sectionCompleted(true)
                .build();

        final NonComplianceApplicationSubmitRequestTaskPayload taskPayload =
            NonComplianceApplicationSubmitRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();

        service.applySaveAction(requestTask, taskActionPayload);

        assertEquals(taskPayload.getReason(), taskActionPayload.getReason());
        assertEquals(taskPayload.getNonComplianceDate(), taskActionPayload.getNonComplianceDate());
        assertEquals(taskPayload.getComplianceDate(), taskActionPayload.getComplianceDate());
        assertEquals(taskPayload.getComments(), taskActionPayload.getComments());
        assertEquals(taskPayload.getSelectedRequests(), taskActionPayload.getSelectedRequests());
        assertEquals(taskPayload.getNonCompliancePenalties(), taskActionPayload.getNonCompliancePenalties());
        assertEquals(taskPayload.getSectionCompleted(), taskActionPayload.getSectionCompleted());
    }

    @Test
    public void amendDetails() {

        AppUser appUser = AppUser.builder().userId("id").build();

        LocalDate nonComplianceDate = LocalDate.of(2024, 9, 1);
        LocalDate complianceDate = LocalDate.of(2024, 10, 1);

        Request request = Request.builder().type(RequestType.NON_COMPLIANCE).build();

        NonComplianceNoticeOfIntentRequestTaskPayload taskPayload =
                NonComplianceNoticeOfIntentRequestTaskPayload
                        .builder()
                        .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_PAYLOAD)
                        .build();

        RequestTask requestTask = RequestTask
                .builder()
                .request(request)
                .type(RequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT)
                .payload(taskPayload)
                .build();

        NonComplianceAmendDetailsRequestTaskActionPayload taskActionPayload =
                NonComplianceAmendDetailsRequestTaskActionPayload
                        .builder()
                        .reason(NonComplianceReason.EXCEEDING_EMISSIONS_TARGET)
                        .nonComplianceDate(nonComplianceDate)
                        .complianceDate(complianceDate)
                        .comments("test comments")
                        .build();


        NonComplianceDetailsAmendedRequestActionPayload actionPayload =
                NonComplianceDetailsAmendedRequestActionPayload
                        .builder()
                        .payloadType(RequestActionPayloadType.NON_COMPLIANCE_DETAILS_AMENDED_PAYLOAD)
                        .reason(NonComplianceReason.EXCEEDING_EMISSIONS_TARGET)
                        .nonComplianceDate(nonComplianceDate)
                        .complianceDate(complianceDate)
                        .comments("test comments")
                        .build();


        service.amendDetails(requestTask, taskActionPayload, appUser);

        assertThat(((NonComplianceNoticeOfIntentRequestTaskPayload) requestTask.getPayload()).getReason()).isEqualTo(NonComplianceReason.EXCEEDING_EMISSIONS_TARGET);
        assertThat(((NonComplianceNoticeOfIntentRequestTaskPayload) requestTask.getPayload()).getComplianceDate()).isEqualTo(complianceDate).isEqualTo(complianceDate);
        assertThat(((NonComplianceNoticeOfIntentRequestTaskPayload) requestTask.getPayload()).getNonComplianceDate()).isEqualTo(nonComplianceDate);
        assertThat(((NonComplianceNoticeOfIntentRequestTaskPayload) requestTask.getPayload()).getNonComplianceComments()).isEqualTo("test comments");

        verify(requestService, times(1)).addActionToRequest(request, actionPayload, RequestActionType.NON_COMPLIANCE_DETAILS_AMENDED, appUser.getUserId() );
    }
}
