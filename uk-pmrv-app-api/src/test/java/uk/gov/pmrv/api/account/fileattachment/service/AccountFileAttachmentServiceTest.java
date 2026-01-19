package uk.gov.pmrv.api.account.fileattachment.service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachment;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentStatus;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.mapper.AccountFileAttachmentMapper;
import uk.gov.pmrv.api.account.fileattachment.repository.AccountFileAttachmentRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class AccountFileAttachmentServiceTest {

    @InjectMocks
    private AccountFileAttachmentService service;

    @Mock
    private AccountFileAttachmentRepository repository;

    @Mock
    private AccountFileAttachmentMapper mapper;

    @Test
    void getFilesByWorkflowAndPeriodAndCompetentAuthority_returnsList() {
        AccountFileAttachmentWorkflow workflow = AccountFileAttachmentWorkflow.ALR;
        String period = "2024";
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        AccountFileAttachment entity1 = AccountFileAttachment.builder().accountId(1L).build();
        AccountFileAttachment entity2 = AccountFileAttachment.builder().accountId(2L).build();

        AccountFileAttachmentDTO dto1 = AccountFileAttachmentDTO.builder().fileUuid("U1").build();
        AccountFileAttachmentDTO dto2 = AccountFileAttachmentDTO.builder().fileUuid("U2").build();

        when(repository.findByWorkflowAndPeriodAndCompetentAuthority(workflow, period, ca))
                .thenReturn(List.of(entity1, entity2));
        when(mapper.toDto(entity1)).thenReturn(dto1);
        when(mapper.toDto(entity2)).thenReturn(dto2);

        List<AccountFileAttachmentDTO> result =
                service.getFilesByWorkflowAndPeriodAndCompetentAuthority(workflow, period, ca);

        assertThat(result).containsExactlyInAnyOrder(dto1, dto2);
    }

    @Test
    void getFileByFileUuid_found() {
        AccountFileAttachment entity = new AccountFileAttachment();
        AccountFileAttachmentDTO dto = AccountFileAttachmentDTO.builder().fileUuid("ABC").build();

        when(repository.findByFileUuid("ABC")).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        AccountFileAttachmentDTO result = service.getFileByFileUuid("ABC").orElse(null);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getFileByFileUuid_notFound_returnsNull() {
        when(repository.findByFileUuid("ABC")).thenReturn(Optional.empty());

        AccountFileAttachmentDTO result = service.getFileByFileUuid("ABC").orElse(null);

        assertThat(result).isNull();
    }

    @Test
    void updateOrInsertAccountFileAttachment_insertCase() {
        Long accountId = 1L;
        String requestId = "REQ1";
        String fileUuid = "FILE123";
        String period = "2024";

        when(repository.findByAccountIdAndWorkflowAndWorkflowSubtypeAndPeriod(
                accountId,
                AccountFileAttachmentWorkflow.ALR,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT
                ,period))
            .thenReturn(Optional.empty());

        when(mapper.createEntity(any(AccountFileAttachmentDTO.class)))
            .thenReturn(AccountFileAttachment.builder().accountId(accountId)
                .originatedRequestId(requestId).fileUuid(fileUuid).period(period).creationDate(LocalDateTime.now())
                .build());

        ArgumentCaptor<AccountFileAttachment> captor =
                ArgumentCaptor.forClass(AccountFileAttachment.class);

        service.updateOrInsertAccountFileAttachment(
                AccountFileAttachmentDTO.builder()
                        .workflow(AccountFileAttachmentWorkflow.ALR)
                        .workflowSubtype(AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT)
                        .originatedRequestId(requestId)
                        .status(AccountFileAttachmentStatus.IN_PROGRESS)
                        .accountId(accountId)
                        .period(period)
                        .fileUuid(fileUuid)
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()
        );

        verify(repository).save(captor.capture());

        AccountFileAttachment saved = captor.getValue();

        assertThat(saved.getAccountId()).isEqualTo(accountId);
        assertThat(saved.getOriginatedRequestId()).isEqualTo(requestId);
        assertThat(saved.getFileUuid()).isEqualTo(fileUuid);
        assertThat(saved.getPeriod()).isEqualTo(period);
        assertThat(saved.getCreationDate()).isNotNull();
    }

    @Test
    void updateOrInsertAccountFileAttachment_updateCase() throws NoSuchFieldException, IllegalAccessException {
        Long accountId = 77L;

        AccountFileAttachment existing = new AccountFileAttachment();
        existing.setId(99L);

        when(repository.findByAccountIdAndWorkflowAndWorkflowSubtypeAndPeriod(
                accountId,
                AccountFileAttachmentWorkflow.DOAL,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
            "2025"
        )).thenReturn(Optional.of(existing));

        // Use real mapper for this test
        AccountFileAttachmentMapper customMapper = Mappers.getMapper(AccountFileAttachmentMapper.class);
        Field mapperField = AccountFileAttachmentService.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        mapperField.set(service, customMapper);

        ArgumentCaptor<AccountFileAttachment> captor =
                ArgumentCaptor.forClass(AccountFileAttachment.class);

        service.updateOrInsertAccountFileAttachment(
                AccountFileAttachmentDTO.builder()
                        .workflow(AccountFileAttachmentWorkflow.DOAL)
                        .workflowSubtype(AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT)
                        .originatedRequestId("REQ1")
                        .status(AccountFileAttachmentStatus.IN_PROGRESS)
                        .accountId(accountId)
                        .period("2025")
                        .fileUuid("NEWFILE")
                        .competentAuthority(CompetentAuthorityEnum.WALES)
                        .build()
        );

        verify(repository).save(captor.capture());

        AccountFileAttachment saved = captor.getValue();

        assertThat(saved.getId()).isEqualTo(99L);
        assertThat(saved.getAccountId()).isEqualTo(accountId);
        assertThat(saved.getFileUuid()).isEqualTo("NEWFILE");
    }

    @Test
    void updateAccountFileAttachmentsStatusByAccountId_updatesAndSaves() {
        Long accountId = 1L;
        AccountFileAttachmentWorkflow workflow = AccountFileAttachmentWorkflow.ALR;
        AccountFileAttachmentStatus newStatus = AccountFileAttachmentStatus.FINALIZED;

        AccountFileAttachment attachment = new AccountFileAttachment();
        attachment.setStatus(AccountFileAttachmentStatus.IN_PROGRESS);

        when(repository.findByWorkflowAndAccountId(workflow, accountId))
                .thenReturn(List.of(attachment));

        service.updateAccountFileAttachmentsStatusByAccountId(workflow, newStatus, accountId);

        assertThat(attachment.getStatus()).isEqualTo(newStatus);
        verify(repository).saveAll(List.of(attachment));
    }

    @Test
    void shouldNotSaveWhenNoAttachmentsExist() {
        // given
        Long accountId = 1L;
        AccountFileAttachmentWorkflow workflow = AccountFileAttachmentWorkflow.ALR;
        AccountFileAttachmentStatus status = AccountFileAttachmentStatus.IN_PROGRESS;

        when(repository.findByWorkflowAndAccountId(workflow, accountId))
                .thenReturn(Collections.emptyList());

        // when
        service.updateAccountFileAttachmentsStatusByAccountId(workflow, status, accountId);

        // then
        verify(repository).findByWorkflowAndAccountId(workflow, accountId);
        verify(repository, never()).saveAll(any());
    }

    @Test
    void getLatestCompletedFileByAccountId_returnsLatestFinalized() {
        // given
        Long accountId = 1L;
        Set<AccountFileAttachmentWorkflow> workflows =
                Set.of(AccountFileAttachmentWorkflow.ALR);

        AccountFileAttachmentWorkflowSubType subtype =
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT;

        AccountFileAttachment finalizedLatest = new AccountFileAttachment();
        finalizedLatest.setStatus(AccountFileAttachmentStatus.FINALIZED);

        AccountFileAttachment nonFinalizedOlder = new AccountFileAttachment();
        nonFinalizedOlder.setStatus(AccountFileAttachmentStatus.IN_PROGRESS);

        AccountFileAttachmentDTO dto =
                AccountFileAttachmentDTO.builder().fileUuid("FINAL").build();

        when(repository.findByAccountIdAndWorkflowInAndWorkflowSubtypeAndStatusOrderByCreationDateDesc(
                accountId, workflows, subtype, AccountFileAttachmentStatus.FINALIZED))
                .thenReturn(List.of(finalizedLatest));

        when(mapper.toDto(finalizedLatest)).thenReturn(dto);

        // when
        Optional<AccountFileAttachmentDTO> result =
                service.getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(
                        accountId, workflows, subtype);

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(dto);
        verify(mapper).toDto(finalizedLatest);
    }

    @Test
    void getLatestFinalizedFileByAccountId_skipsNonFinalizedUntilFinalizedFound() {
        // given
        Long accountId = 1L;
        Set<AccountFileAttachmentWorkflow> workflows =
                Set.of(AccountFileAttachmentWorkflow.ALR);

        AccountFileAttachmentWorkflowSubType subtype =
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT;

        AccountFileAttachment latestNonFinalized = new AccountFileAttachment();
        latestNonFinalized.setStatus(AccountFileAttachmentStatus.IN_PROGRESS);

        AccountFileAttachment finalizedOlder = new AccountFileAttachment();
        finalizedOlder.setStatus(AccountFileAttachmentStatus.FINALIZED);

        AccountFileAttachmentDTO dto =
                AccountFileAttachmentDTO.builder().fileUuid("FINAL").build();

        when(repository.findByAccountIdAndWorkflowInAndWorkflowSubtypeAndStatusOrderByCreationDateDesc(
                accountId, workflows, subtype, AccountFileAttachmentStatus.FINALIZED))
                .thenReturn(List.of(finalizedOlder));

        when(mapper.toDto(finalizedOlder)).thenReturn(dto);

        // when
        Optional<AccountFileAttachmentDTO> result =
                service.getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(
                        accountId, workflows, subtype);

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(dto);
        verify(mapper).toDto(finalizedOlder);
    }

    @Test
    void getLatestFinalizedFileByAccountId_noFinalized_returnsNull() {
        // given
        Long accountId = 1L;
        Set<AccountFileAttachmentWorkflow> workflows =
                Set.of(AccountFileAttachmentWorkflow.ALR);

        AccountFileAttachmentWorkflowSubType subtype =
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT;

        AccountFileAttachment nonFinalized = new AccountFileAttachment();
        nonFinalized.setStatus(AccountFileAttachmentStatus.IN_PROGRESS);

        when(repository.findByAccountIdAndWorkflowInAndWorkflowSubtypeAndStatusOrderByCreationDateDesc(
                accountId, workflows, subtype, AccountFileAttachmentStatus.FINALIZED))
                .thenReturn(Collections.emptyList());

        // when
        Optional<AccountFileAttachmentDTO> result =
                service.getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(
                        accountId, workflows, subtype);

        // then
        assertThat(result.isEmpty()).isTrue();
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId_emptyList_returnsNull() {
        // given
        Long accountId = 1L;
        Set<AccountFileAttachmentWorkflow> workflows =
                Set.of(AccountFileAttachmentWorkflow.ALR);

        AccountFileAttachmentWorkflowSubType subtype =
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT;

        when(repository.findByAccountIdAndWorkflowInAndWorkflowSubtypeAndStatusOrderByCreationDateDesc(
                accountId, workflows, subtype, AccountFileAttachmentStatus.FINALIZED))
                .thenReturn(Collections.emptyList());

        // when
        Optional<AccountFileAttachmentDTO> result =
                service.getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(
                        accountId, workflows, subtype);

        // then
        assertThat(result.isEmpty()).isTrue();
        verify(mapper, never()).toDto(any());
    }
}
