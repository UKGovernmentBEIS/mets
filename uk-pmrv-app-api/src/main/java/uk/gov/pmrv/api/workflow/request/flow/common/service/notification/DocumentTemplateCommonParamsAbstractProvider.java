package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.common.config.CompetentAuthorityProperties;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthorityService;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.AccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.SignatoryTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.WorkflowTemplateParams;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.service.CompetentAuthorityDTOByRequestResolverDelegator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RequiredArgsConstructor
public abstract class DocumentTemplateCommonParamsAbstractProvider implements DocumentTemplateCommonParamsProvider {

    private final RegulatorUserAuthService regulatorUserAuthService;
    private final UserAuthService userAuthService;
    private final CompetentAuthorityProperties competentAuthorityProperties;
    private final DateService dateService;
    private final CompetentAuthorityDTOByRequestResolverDelegator competentAuthorityDTOByRequestResolverDelegator;

    public abstract String getPermitReferenceId(Long accountId);
    public abstract AccountTemplateParams getAccountTemplateParams(Long accountId);


    public TemplateParams constructCommonTemplateParams(final Request request,
                                                        final String signatory) {
        final Long accountId = request.getAccountId();
        final String permitReferenceId = getPermitReferenceId(accountId);
        final AccountType accountType = request.getType().getAccountType();

        // account params
        final AccountTemplateParams accountTemplateParams = getAccountTemplateParams(accountId);

        // CA params
        final CompetentAuthorityEnum competentAuthority = accountTemplateParams.getCompetentAuthority();
        final CompetentAuthorityDTO competentAuthorityDTO = competentAuthorityDTOByRequestResolverDelegator
                .resolveCA(request, accountType);
        final CompetentAuthorityTemplateParams competentAuthorityParams = CompetentAuthorityTemplateParams.builder()
            .competentAuthority(competentAuthorityDTO)
            .logo(PmrvCompetentAuthorityService.getCompetentAuthorityLogo(competentAuthority))
            .build();

        // Signatory params
        final RegulatorUserDTO signatoryUser = regulatorUserAuthService.getRegulatorUserById(signatory);
        final FileInfoDTO signatureInfo = signatoryUser.getSignature();
        if (signatureInfo == null) {
            throw new BusinessException(ErrorCode.USER_SIGNATURE_NOT_EXIST, signatory);
        }
        final FileDTO signatorySignature = userAuthService.getUserSignature(signatureInfo.getUuid())
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, signatureInfo.getUuid()));
        final SignatoryTemplateParams signatoryParams = SignatoryTemplateParams.builder()
            .fullName(signatoryUser.getFullName())
            .jobTitle(signatoryUser.getJobTitle())
            .signature(signatorySignature.getFileContent())
            .build();

        // workflow params
        // request end date is set when the request closes, so for the permit issuance flow it is null at this point
        final LocalDateTime requestEndDate = request.getEndDate() != null ? request.getEndDate() : dateService.getLocalDateTime();
        final Date requestSubmissionDate = request.getSubmissionDate() != null ? 
            Date.from(request.getSubmissionDate().atZone(ZoneId.systemDefault()).toInstant()) :
            Date.from(dateService.getLocalDateTime().atZone(ZoneId.systemDefault()).toInstant());
        final WorkflowTemplateParams workflowParams = WorkflowTemplateParams.builder()
            .requestId(request.getId())
            .requestSubmissionDate(requestSubmissionDate)
            .requestEndDate(requestEndDate)
            .requestTypeInfo(RequestTypeDocumentTemplateInfoMapper.getTemplateInfo(request.getType()))
            .requestType(request.getType().name())
            .build();

        return TemplateParams.builder()
            .competentAuthorityParams(competentAuthorityParams)
            .competentAuthorityCentralInfo(competentAuthorityProperties.getCentralInfo())
            .signatoryParams(signatoryParams)
            .accountParams(accountTemplateParams)
            .permitId(permitReferenceId)
            .workflowParams(workflowParams)
            .build();
    }
}
