package uk.gov.pmrv.api.web.orchestrator.account.installation.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountDetailsDTO;
import uk.gov.pmrv.api.account.installation.service.ApprovedInstallationAccountQueryService;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountHeaderInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.transform.InstallationAccountHeaderInfoMapper;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstallationAccountQueryOrchestrator {

    private final InstallationAccountQueryService installationAccountQueryService;
    private final ApprovedInstallationAccountQueryService approvedInstallationAccountQueryService;
    private final PermitQueryService permitQueryService;
    private final AccountFileAttachmentService accountFileAttachmentService;
    private final FileAttachmentService fileAttachmentService;
    private static final InstallationAccountHeaderInfoMapper ACCOUNT_HEADER_INFO_MAPPER = Mappers.getMapper(InstallationAccountHeaderInfoMapper.class);

    @Transactional(readOnly = true)
    public InstallationAccountPermitDTO getAccountWithPermit(Long accountId) {
    	InstallationAccountDTO account = installationAccountQueryService.getAccountDTOById(accountId);
		Optional<PermitDetailsDTO> permitDetailsOpt = permitQueryService.getPermitDetailsByAccountId(accountId);

		return InstallationAccountPermitDTO.builder()
        		.account(account)
        		.permit(permitDetailsOpt.orElse(null))
        		.build();
    }

    public Optional<InstallationAccountHeaderInfoDTO> getAccountHeaderInfoWithPermitId(Long accountId) {
        Optional<InstallationAccountHeaderInfoDTO> accountHeaderInfo =
            approvedInstallationAccountQueryService.getApprovedAccountById(accountId).map(ACCOUNT_HEADER_INFO_MAPPER::toAccountHeaderInfoDTO);
        accountHeaderInfo.ifPresent(headerInfo -> headerInfo.setPermitId(permitQueryService.getPermitIdByAccountId(accountId).orElse(null)));
        return accountHeaderInfo;
    }

    @Transactional
    public InstallationAccountDetailsDTO getAccountDetails(Long accountId) {
        InstallationAccountPermitDTO accountPermitDTO = getAccountWithPermit(accountId);

        FileInfoDTO latestAlrFile = getLatestAlrFile_ALRandDOAL(accountId);
        return InstallationAccountDetailsDTO.builder().accountPermitDto(accountPermitDTO).latestAlrFile(latestAlrFile).build();
    }

    protected FileInfoDTO getLatestAlrFile_ALRandDOAL(Long accountId) {
        Optional<AccountFileAttachmentDTO> latestALR = accountFileAttachmentService.getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(accountId,
                Set.of(AccountFileAttachmentWorkflow.ALR, AccountFileAttachmentWorkflow.DOAL),
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT);

        if (latestALR.isEmpty() || !fileAttachmentService.fileAttachmentExist(latestALR.get().getFileUuid())) {
            return null;
        }

        FileDTO latestALRFileDTO = fileAttachmentService.getFileDTO(latestALR.get().getFileUuid());

        return FileInfoDTO.builder().name(latestALRFileDTO.getFileName()).uuid(latestALR.get().getFileUuid()).build();
    }


}
