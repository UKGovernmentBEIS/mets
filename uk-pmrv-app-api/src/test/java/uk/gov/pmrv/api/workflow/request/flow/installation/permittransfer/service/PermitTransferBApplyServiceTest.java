package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.AbbreviationDefinition;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetailsConfirmation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper.PermitTransferMapper;

@ExtendWith(MockitoExtension.class)
class PermitTransferBApplyServiceTest {

    @InjectMocks
    private PermitTransferBApplyService service;

    @Mock
    private PermitTransferBValidatorService transferValidatorService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    
    @Mock
    private PermitValidatorService permitValidatorService;

    @Mock
    private RequestService requestService;

    @Test
    void applySaveAction() {

        final PermitTransferBApplicationRequestTaskPayload taskPayload = PermitTransferBApplicationRequestTaskPayload
            .builder()
            .permitTransferDetails(PermitTransferDetails.builder().build())
            .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_SUBMIT_PAYLOAD)
            .build();

        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        final PermitType permitType = PermitType.GHGE;
        final Permit permit = Permit.builder().abbreviations(Abbreviations.builder().exist(false).build()).build();
        final Map<String, List<Boolean>> permitSectionsCompleted =
            Map.of("section", List.of(Boolean.TRUE, Boolean.FALSE));
        final PermitTransferDetailsConfirmation permitTransferDetailsConfirmation = PermitTransferDetailsConfirmation.builder()
            .detailsAccepted(true)
            .transferAccepted(true)
            .regulatedActivitiesInOperation(false)
            .regulatedActivitiesNotInOperationReason("the reason")
            .build();
        final PermitTransferBSaveApplicationRequestTaskActionPayload taskActionPayload =
            PermitTransferBSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_TRANSFER_B_SAVE_APPLICATION_PAYLOAD)
                .permit(permit)
                .permitType(permitType)
                .permitTransferDetailsConfirmation(permitTransferDetailsConfirmation)
                .permitSectionsCompleted(permitSectionsCompleted)
                .build();

        service.applySaveAction(requestTask, taskActionPayload);

        assertThat(taskPayload.getPermit()).isEqualTo(permit);
        assertThat(taskPayload.getPermitType()).isEqualTo(permitType);
        assertThat(taskPayload.getPermitTransferDetailsConfirmation()).isEqualTo(permitTransferDetailsConfirmation);
        assertThat(taskPayload.getPermitSectionsCompleted()).isEqualTo(permitSectionsCompleted);
    }
    
    @Test
    void applySubmitAction() {

        final AppUser user = AppUser.builder().userId("user").build();

        final long accountId = 1L;
        final Request request = Request.builder()
            .accountId(accountId)
            .payload(PermitTransferBRequestPayload.builder()
                .permitTransferDetails(PermitTransferDetails.builder().build())
                .build())
            .processInstanceId("processInstanceId")
            .build();

        final Map<UUID, String> permitAttachments = Map.of(UUID.randomUUID(), "filename");
        final PermitTransferDetailsConfirmation transferDetailsConfirmation = PermitTransferDetailsConfirmation.builder()
            .transferAccepted(false)
            .transferRejectedReason("the reason")
            .build();
        final InstallationOperatorDetails installationOperatorDetails1 = InstallationOperatorDetails.builder()
            .installationName("installationName1")
            .build();
        final InstallationOperatorDetails installationOperatorDetails2 = InstallationOperatorDetails.builder()
            .installationName("installationName2")
            .build();
        final Permit permit = Permit.builder().abbreviations(Abbreviations.builder()
                .exist(true)
                .abbreviationDefinitions(
                    List.of(AbbreviationDefinition.builder().definition("def").abbreviation("abb").build()))
                .build())
            .build();
        final Map<String, List<Boolean>> permitSectionsCompleted = Map.of("section1", List.of(true, false));
        final PermitTransferBApplicationRequestTaskPayload taskPayload =
            PermitTransferBApplicationRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_SUBMIT_PAYLOAD)
                .permitTransferDetails(PermitTransferDetails.builder().transferCode("123456789").build())
                .permitTransferDetailsConfirmation(transferDetailsConfirmation)
                .permit(permit)
                .permitType(PermitType.GHGE)
                .installationOperatorDetails(installationOperatorDetails1)
                .permitAttachments(permitAttachments)
                .permitSectionsCompleted(permitSectionsCompleted)
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .processTaskId("processTaskId")
            .payload(taskPayload)
            .build();

        final PermitTransferBApplicationSubmittedRequestActionPayload actionPayload =
            PermitTransferBApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_TRANSFER_B_APPLICATION_SUBMITTED_PAYLOAD)
                .permitTransferDetails(PermitTransferDetails.builder().transferCode("123456789").build())
                .permitTransferDetailsConfirmation(transferDetailsConfirmation)
                .permit(permit)
                .permitType(PermitType.GHGE)
                .permitAttachments(permitAttachments)
                .installationOperatorDetails(installationOperatorDetails2)
                .permitSectionsCompleted(permitSectionsCompleted)
                .build();
        
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails2);
        
        service.applySubmitAction(requestTask, user);

        assertThat(((PermitTransferBRequestPayload)request.getPayload()).getPermitTransferDetailsConfirmation()).isEqualTo(transferDetailsConfirmation);
        assertThat(((PermitTransferBRequestPayload)request.getPayload()).getPermit()).isEqualTo(permit);
        assertThat(((PermitTransferBRequestPayload)request.getPayload()).getPermitType()).isEqualTo(PermitType.GHGE);
        assertThat(((PermitTransferBRequestPayload)request.getPayload()).getPermitAttachments()).isEqualTo(permitAttachments);
        assertThat(((PermitTransferBRequestPayload)request.getPayload()).getPermitSectionsCompleted()).isEqualTo(permitSectionsCompleted);
        
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(permitValidatorService, times(1)).validatePermit(Mappers.getMapper(PermitTransferMapper.class).toPermitContainer(taskPayload, installationOperatorDetails2));
        verify(transferValidatorService, times(1)).validateTransferDetailsConfirmation(transferDetailsConfirmation);
        verify(transferValidatorService, times(1)).validateAndDisableTransferCodeStatus("123456789");
        verify(requestService, times(1)).addActionToRequest(
            request,
            actionPayload,
            RequestActionType.PERMIT_TRANSFER_B_APPLICATION_SUBMITTED,
            user.getUserId()
        );
    }
}
