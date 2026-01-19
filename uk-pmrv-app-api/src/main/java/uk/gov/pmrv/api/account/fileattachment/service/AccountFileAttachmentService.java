package uk.gov.pmrv.api.account.fileattachment.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachment;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentStatus;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.mapper.AccountFileAttachmentMapper;
import uk.gov.pmrv.api.account.fileattachment.repository.AccountFileAttachmentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountFileAttachmentService {

    private final AccountFileAttachmentRepository repository;
    private final AccountFileAttachmentMapper mapper;

    public List<AccountFileAttachmentDTO> getFilesByWorkflowAndPeriodAndCompetentAuthority(AccountFileAttachmentWorkflow workflow,
                                                                                           String period,
                                                                                           CompetentAuthorityEnum competentAuthority) {
        return repository.findByWorkflowAndPeriodAndCompetentAuthority(workflow, period, competentAuthority)
                     .stream()
                .map(mapper::toDto)
                .toList();
    }

    public Optional<AccountFileAttachmentDTO> getFileByFileUuid(String uuid) {
        Optional<AccountFileAttachment> fileOptional = repository.findByFileUuid(uuid);
        return fileOptional.map(mapper::toDto);
    }

    public Optional<AccountFileAttachmentDTO> getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(Long accountId, Set<AccountFileAttachmentWorkflow> workflows,
                                                                                                    AccountFileAttachmentWorkflowSubType fileAttachmentWorkflowSubType) {
        List<AccountFileAttachment> files =
                repository.findByAccountIdAndWorkflowInAndWorkflowSubtypeAndStatusOrderByCreationDateDesc(accountId, workflows,
                    fileAttachmentWorkflowSubType, AccountFileAttachmentStatus.FINALIZED);

        return files.stream()
                .findFirst()
                .map(mapper::toDto);
    }

    @Transactional
    public void updateOrInsertAccountFileAttachment(AccountFileAttachmentDTO accountFileAttachmentDTO) {
        AccountFileAttachment entity = this.repository
            .findByAccountIdAndWorkflowAndWorkflowSubtypeAndPeriod(accountFileAttachmentDTO.getAccountId(),
                accountFileAttachmentDTO.getWorkflow(), accountFileAttachmentDTO.getWorkflowSubtype(),
                accountFileAttachmentDTO.getPeriod())
            .map(existing -> {
                mapper.updateEntityFromDto(accountFileAttachmentDTO, existing);
                return existing;
            })
            .orElseGet(() -> mapper.createEntity(accountFileAttachmentDTO));

        repository.save(entity); // ← updates if ID exists, inserts otherwise
    }

    @Transactional
    public void updateAccountFileAttachmentsStatusByAccountId(AccountFileAttachmentWorkflow workflow, AccountFileAttachmentStatus status, Long accountId) {
        List<AccountFileAttachment> fileAttachments = repository.findByWorkflowAndAccountId(workflow, accountId);

        if (!fileAttachments.isEmpty()) {
            fileAttachments.forEach(fileAttachment -> fileAttachment.setStatus(status));

            repository.saveAll(fileAttachments);
        }

    }
}
