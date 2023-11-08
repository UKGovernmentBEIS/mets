package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferASaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.TransferParty;

@ExtendWith(MockitoExtension.class)
class PermitTransferAApplyServiceTest {

    @InjectMocks
    private PermitTransferAApplyService service;

    @Mock
    private PermitTransferAValidatorService validatorService;
    
    @Mock
    private RequestService requestService;

    @Test
    void applySaveAction() {

        final PermitTransferAApplicationRequestTaskPayload taskPayload = PermitTransferAApplicationRequestTaskPayload
            .builder()
            .permitTransferDetails(PermitTransferDetails.builder().build())
            .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_A_APPLICATION_SUBMIT_PAYLOAD)
            .build();

        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        final String reason = "the reason";
        final UUID document = UUID.randomUUID();
        final LocalDate transferDate = LocalDate.of(2022, 1, 1);
        final String transferCode = "the transfer code";
        final PermitTransferASaveApplicationRequestTaskActionPayload taskActionPayload =
            PermitTransferASaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_TRANSFER_A_SAVE_APPLICATION_PAYLOAD)
                .permitTransferDetails(PermitTransferDetails.builder()
                    .reason(reason)
                    .reasonAttachments(Set.of(document))
                    .transferDate(transferDate)
                    .payer(TransferParty.TRANSFERER)
                    .aerLiable(TransferParty.RECEIVER)
                    .transferCode(transferCode)
                    .build())
                .sectionCompleted(true)
                .build();

        service.applySaveAction(requestTask, taskActionPayload);

        assertThat(taskPayload.getPermitTransferDetails().getReason()).isEqualTo(reason);
        assertThat(taskPayload.getPermitTransferDetails().getReasonAttachments()).isEqualTo(Set.of(document));
        assertThat(taskPayload.getPermitTransferDetails().getTransferDate()).isEqualTo(transferDate);
        assertThat(taskPayload.getPermitTransferDetails().getPayer()).isEqualTo(TransferParty.TRANSFERER);
        assertThat(taskPayload.getPermitTransferDetails().getAerLiable()).isEqualTo(TransferParty.RECEIVER);
        assertThat(taskPayload.getPermitTransferDetails().getTransferCode()).isEqualTo(transferCode);
        assertThat(taskPayload.getSectionCompleted()).isTrue();
    }

    @Test
    void applySubmitAction() {
        
        final PmrvUser user = PmrvUser.builder().userId("user").build();
        
        final Request request = Request.builder()
            .payload(PermitTransferARequestPayload.builder()
                .permitTransferDetails(PermitTransferDetails.builder().build())
                .build())
            .processInstanceId("processInstanceId")
            .build();

        final String reason = "the reason";
        final UUID attachment = UUID.randomUUID();
        final LocalDate transferDate = LocalDate.of(2022, 1, 1);
        final String transferCode = "the transfer code";
        final Map<UUID, String> transferAttachments = Map.of(attachment, "attachment");
        final Set<UUID> reasonAttachments = Set.of(attachment);
        final PermitTransferAApplicationRequestTaskPayload taskPayload =
            PermitTransferAApplicationRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_A_APPLICATION_SUBMIT_PAYLOAD)
                .permitTransferDetails(PermitTransferDetails.builder()
                    .reason(reason)
                    .reasonAttachments(reasonAttachments)
                    .transferDate(transferDate)
                    .payer(TransferParty.TRANSFERER)
                    .aerLiable(TransferParty.RECEIVER)
                    .transferCode(transferCode)
                    .build())
                .transferAttachments(transferAttachments)
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .processTaskId("processTaskId")
            .payload(taskPayload)
            .build();

        final PermitTransferAApplicationSubmittedRequestActionPayload actionPayload =
            PermitTransferAApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_TRANSFER_A_APPLICATION_SUBMITTED_PAYLOAD)
                .permitTransferDetails(PermitTransferDetails.builder()
                    .reason(reason)
                    .reasonAttachments(reasonAttachments)
                    .transferDate(transferDate)
                    .payer(TransferParty.TRANSFERER)
                    .aerLiable(TransferParty.RECEIVER)
                    .transferCode(transferCode)
                    .build())
                .transferAttachments(transferAttachments)
                .build();

        service.applySubmitAction(requestTask, user);

        assertThat(((PermitTransferARequestPayload)request.getPayload()).getPermitTransferDetails().getReason()).isEqualTo(reason);
        assertThat(((PermitTransferARequestPayload)request.getPayload()).getPermitTransferDetails().getReasonAttachments()).isEqualTo(reasonAttachments);
        assertThat(((PermitTransferARequestPayload)request.getPayload()).getPermitTransferDetails().getTransferDate()).isEqualTo(transferDate);
        assertThat(((PermitTransferARequestPayload)request.getPayload()).getPermitTransferDetails().getPayer()).isEqualTo(TransferParty.TRANSFERER);
        assertThat(((PermitTransferARequestPayload)request.getPayload()).getPermitTransferDetails().getAerLiable()).isEqualTo(TransferParty.RECEIVER);
        assertThat(((PermitTransferARequestPayload)request.getPayload()).getPermitTransferDetails().getTransferCode()).isEqualTo(transferCode);
        assertThat(((PermitTransferARequestPayload)request.getPayload()).getTransferAttachments()).isEqualTo(transferAttachments);
        
        verify(validatorService, times(1)).validateTaskPayload(taskPayload);
        verify(validatorService, times(1)).validatePermitTransferA(requestTask);
        verify(requestService, times(1)).addActionToRequest(
            request,
            actionPayload,
            RequestActionType.PERMIT_TRANSFER_A_APPLICATION_SUBMITTED,
            user.getUserId()
        );
    }
}
