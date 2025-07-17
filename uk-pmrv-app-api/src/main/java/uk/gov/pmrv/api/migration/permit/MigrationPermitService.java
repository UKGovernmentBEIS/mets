package uk.gov.pmrv.api.migration.permit;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.repository.InstallationAccountRepository;
import uk.gov.pmrv.api.account.installation.transform.InstallationCategoryMapper;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.repository.PermitRepository;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.permit.validation.PermitContextValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MigrationPermitService {


    private final PermitService permitService;
    private final InstallationAccountRepository installationAccountRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final PermitRepository permitRepository;
    private final List<PermitContextValidator> permitContextValidators;

    @Transactional(propagation = Propagation.REQUIRED)
    public void migratePermit(Long accountId, PermitMigrationContainer permitMigrationContainer, List<String> migrationResults) {
        PermitContainer permit = permitMigrationContainer.getPermitContainer();
        List<FileAttachment> permitAttachments = permitMigrationContainer.getFileAttachments();

        try {
            fileAttachmentRepository.saveAll(permitAttachments);
            String submittedPermitId = permitService.submitPermit(permit, accountId);
            permitRepository.findById(submittedPermitId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND)).setConsolidationNumber(permitMigrationContainer.getConsolidationNumber());
            updateAccountUponPermitMigration(permit, accountId);
        } catch (BusinessException e) {
            Optional<InstallationAccount> account = installationAccountRepository.findById(accountId);
            Arrays
                    .asList(e.getData())
                    .forEach(violation ->
                            migrationResults.add(constructErrorMessage(
                                    accountId,
                                    account.map(Account::getMigratedAccountId).orElse(""),
                                    account.map(account1 -> account1.getCompetentAuthority().getCode()).orElse(null),
                                    ((PermitViolation) violation).getSectionName(),
                                    ((PermitViolation) violation).getMessage(),
                                    getData((PermitViolation) violation))
                            ));
            throw e;
        } catch (ConstraintViolationException e) {
            //run validators anyway to collect all errors. validators should be made null safe
            Optional<InstallationAccount> account = installationAccountRepository.findById(accountId);
            e.getConstraintViolations().forEach((error -> {
                String data = error.getLeafBean() instanceof SourceStream ? ((SourceStream)error.getLeafBean()).getReference():null;
                migrationResults.add(constructErrorMessage(
                        accountId,
                        account.map(Account::getMigratedAccountId).orElse(""),
                        account.map(account1 -> account1.getCompetentAuthority().getCode()).orElse(null),
                        "",
                        error.getMessage(),
                        data != null ? error.getPropertyPath() + ":" + error.getInvalidValue() + ", Reference: " + data : error.getPropertyPath() + ":" + error.getInvalidValue()));
            }));

            List<PermitValidationResult> validationResults = new ArrayList<>();
            permitContextValidators.forEach(v -> validationResults.add(v.validate(permit)));

            boolean isValid = validationResults.stream().allMatch(PermitValidationResult::isValid);

            if (!isValid) {
                validationResults.forEach((error -> error.getPermitViolations().forEach(violation ->
                        migrationResults.add(constructErrorMessage(
                                accountId,
                                account.map(Account::getMigratedAccountId).orElse(""),
                                account.map(account1 -> account1.getCompetentAuthority().getCode()).orElse(null),
                                violation.getSectionName(),
                                violation.getMessage(),
                                getData(violation))))));
            }
            throw e;
        } catch (Exception e) {
            Optional<InstallationAccount> account = installationAccountRepository.findById(accountId);
            migrationResults.add(constructErrorMessage(
                    accountId,
                    account.map(Account::getMigratedAccountId).orElse(""),
                    account.map(account1 -> account1.getCompetentAuthority().getCode()).orElse(null),
                    "",
                    e.getMessage(),
                    ""));
            throw e;
        }
        Optional<InstallationAccount> account = installationAccountRepository.findById(accountId);
        migrationResults.add(constructSuccessMessage(accountId,
                account.map(InstallationAccount::getMigratedAccountId).orElse(""),
                account.map(account1 -> account1.getCompetentAuthority().getCode()).orElse(null)));
    }

    private void updateAccountUponPermitMigration(PermitContainer permitContainer, Long accountId) {
        InstallationAccount account = installationAccountRepository.findById(accountId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        EmitterType emitterType = permitContainer.getPermitType() == PermitType.HSE ? EmitterType.HSE : EmitterType.GHGE;
        account.setEmitterType(emitterType);
        account.setInstallationCategory(
            InstallationCategoryMapper.getInstallationCategory(emitterType, permitContainer.getPermit().getEstimatedAnnualEmissions().getQuantity()));

        installationAccountRepository.save(account);
    }

    private String getData(PermitViolation violation) {
        StringBuilder builder = new StringBuilder();
        List<Object> collect = Arrays.stream(violation.getData())
                .map(data -> data instanceof Map.Entry ? ((Map.Entry<?, ?>) data).getValue() : data)
                .collect(Collectors.toList());
        collect.forEach(element -> builder.append("[").append(element).append("]"));
        return builder.toString();
    }

    private String constructErrorMessage(Long pmrvAccountId, String emitterId, String ca, String sectionName, String errorMessage, String data) {
        return "pmrvAccountId: " + pmrvAccountId +
                " | emitterId: " + emitterId +
                " | CA: " + ca +
                " | sectionName: " + sectionName +
                " | Error: " + errorMessage +
                " | data: " + data;
    }

    private String constructSuccessMessage(Long pmrvAccountId, String emitterId, String ca) {
        return "pmrvAccountId: " + pmrvAccountId +
                " | emitterId: " + emitterId +
                " | CA: " + ca;
    }
}
