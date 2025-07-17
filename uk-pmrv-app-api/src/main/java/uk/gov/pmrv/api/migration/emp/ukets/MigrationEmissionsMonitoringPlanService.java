package uk.gov.pmrv.api.migration.emp.ukets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmissionsMonitoringPlanRepository;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation.EmpUkEtsContextValidator;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MigrationEmissionsMonitoringPlanService {

    private final EmissionsMonitoringPlanService emissionsMonitoringPlanService;

    private final EmissionsMonitoringPlanRepository emissionsMonitoringPlanRepository;

    private final FileAttachmentRepository fileAttachmentRepository;

    private final AviationAccountRepository aviationAccountRepository;

    private final LocationMapper locationMapper;

    private final List<EmpUkEtsContextValidator> empUkEtsContextValidators;

    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public void migrateEmp(Long accountId, EmissionsMonitoringPlanMigrationContainer empMigrationContainer, List<String> migrationResults) throws JsonProcessingException {

        EmissionsMonitoringPlanUkEtsContainer empContainer = empMigrationContainer.getEmpContainer();
        List<FileAttachment> empAttachments = empMigrationContainer.getFileAttachments();

        EmissionsMonitoringPlanUkEtsContainer empContainerUnescaped  = getUnescapedContainer(empContainer);

        try {
            fileAttachmentRepository.saveAll(empAttachments);
            String submittedEmpId = emissionsMonitoringPlanService.submitEmissionsMonitoringPlan(accountId, empContainerUnescaped);
            emissionsMonitoringPlanRepository.findById(submittedEmpId)
                    .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND)).setConsolidationNumber(empMigrationContainer.getConsolidationNumber());
            updateAccountUponEmpMigration(empContainerUnescaped, accountId);
        } catch (BusinessException e) {
            Optional<AviationAccount> aviationAccount = aviationAccountRepository.findById(accountId);
            Arrays
                    .asList(e.getData())
                    .forEach(violation ->
                            migrationResults.add(constructErrorMessage(
                                    accountId,
                                    aviationAccount.map(Account::getMigratedAccountId).orElse(""),
                                    aviationAccount.map(account -> account.getCompetentAuthority().getCode()).orElse(null),
                                    ((EmissionsMonitoringPlanViolation) violation).getSectionName(),
                                    ((EmissionsMonitoringPlanViolation) violation).getMessage(),
                                    getData((EmissionsMonitoringPlanViolation) violation),
                                    empContainerUnescaped.getEmissionsMonitoringPlan().getEmissionsMonitoringApproach().getMonitoringApproachType())
                            ));
            throw e;
        } catch (ConstraintViolationException e) {

            Optional<AviationAccount> aviationAccount = aviationAccountRepository.findById(accountId);
            e.getConstraintViolations().forEach((error -> {
                migrationResults.add(constructErrorMessage(
                        accountId,
                        aviationAccount.map(Account::getMigratedAccountId).orElse(""),
                        aviationAccount.map(account -> account.getCompetentAuthority().getCode()).orElse(null),
                        "",
                        error.getMessage(),
                        error.getPropertyPath() + ":" + error.getInvalidValue(),
                        empContainerUnescaped.getEmissionsMonitoringPlan().getEmissionsMonitoringApproach().getMonitoringApproachType()));
            }));

            List<EmissionsMonitoringPlanValidationResult> validationResults = new ArrayList<>();
            empUkEtsContextValidators.forEach(v -> validationResults.add(v.validate(empContainerUnescaped)));

            boolean isValid = validationResults.stream().allMatch(EmissionsMonitoringPlanValidationResult::isValid);

            if (!isValid) {
                validationResults.forEach((error -> error.getEmpViolations().forEach(violation ->
                        migrationResults.add(constructErrorMessage(
                                accountId,
                                aviationAccount.map(Account::getMigratedAccountId).orElse(""),
                                aviationAccount.map(account -> account.getCompetentAuthority().getCode()).orElse(null),
                                violation.getSectionName(),
                                violation.getMessage(),
                                getData(violation),
                                empContainerUnescaped.getEmissionsMonitoringPlan().getEmissionsMonitoringApproach().getMonitoringApproachType())))));
            }
            throw e;

        } catch (Exception e) {
            Optional<AviationAccount> aviationAccount = aviationAccountRepository.findById(accountId);
            migrationResults.add(constructErrorMessage(
                    accountId,
                    aviationAccount.map(Account::getMigratedAccountId).orElse(""),
                    aviationAccount.map(account -> account.getCompetentAuthority().getCode()).orElse(null),
                    "",
                    e.getMessage(),
                    "",
                    empContainerUnescaped.getEmissionsMonitoringPlan().getEmissionsMonitoringApproach().getMonitoringApproachType()));
            throw e;
        }
        Optional<AviationAccount> aviationAccount = aviationAccountRepository.findById(accountId);
        migrationResults.add(constructSuccessMessage(accountId,
                aviationAccount.map(AviationAccount::getMigratedAccountId).orElse(""),
                aviationAccount.map(account -> account.getCompetentAuthority().getCode()).orElse(null)));
    }

    private EmissionsMonitoringPlanUkEtsContainer getUnescapedContainer(EmissionsMonitoringPlanUkEtsContainer empContainer) throws JsonProcessingException {
        EmissionsMonitoringPlanUkEtsContainer empContainerUnescaped;
        try {
            String json = StringEscapeUtils.unescapeHtml4(objectMapper.writeValueAsString(empContainer));
            empContainerUnescaped = objectMapper.readValue(json, EmissionsMonitoringPlanUkEtsContainer.class);
        } catch (Exception ex) {
            //This is for account_id=3163
            String json = objectMapper.writeValueAsString(empContainer);
            json = json.replace("&quot;", "\\&quot;");
            String jsonUnescaped = StringEscapeUtils.unescapeHtml4(json);
            empContainerUnescaped = objectMapper.readValue(jsonUnescaped, EmissionsMonitoringPlanUkEtsContainer.class);
        }
        return empContainerUnescaped;
    }

    private void updateAccountUponEmpMigration(EmissionsMonitoringPlanUkEtsContainer empContainer, Long accountId) {
        AviationAccount account = aviationAccountRepository.findAviationAccountById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        account.setName(empContainer.getEmissionsMonitoringPlan().getOperatorDetails().getOperatorName());
        account.setLocation(locationMapper.toLocation(getContactLocationFromEmp(empContainer.getEmissionsMonitoringPlan().getOperatorDetails())));
    }

    private LocationOnShoreStateDTO getContactLocationFromEmp(EmpOperatorDetails empOperatorDetails) {
        OrganisationStructure organisationStructure = empOperatorDetails.getOrganisationStructure();

        if (OrganisationLegalStatusType.LIMITED_COMPANY.equals(organisationStructure.getLegalStatusType())) {

            LimitedCompanyOrganisation limitedCompanyOrganisation = (LimitedCompanyOrganisation) organisationStructure;

            if (Boolean.TRUE.equals(limitedCompanyOrganisation.getDifferentContactLocationExist())) {
                return limitedCompanyOrganisation.getDifferentContactLocation();
            }
        }

        return organisationStructure.getOrganisationLocation();
    }

    private String getData(EmissionsMonitoringPlanViolation violation) {
        StringBuilder builder = new StringBuilder();
        List<Object> collect = Arrays.stream(violation.getData())
                .map(data -> data instanceof Map.Entry ? ((Map.Entry<?, ?>) data).getValue() : data).toList();
        collect.forEach(element -> builder.append("[").append(element).append("]"));
        return builder.toString();
    }

    private String constructErrorMessage(Long pmrvAccountId, String emitterId, String ca, String sectionName, String errorMessage, String data, EmissionsMonitoringApproachType monitoringApproach) {
        return "pmrvAccountId: " + pmrvAccountId +
                " | emitterId: " + emitterId +
                " | CA: " + ca +
                " | sectionName: " + sectionName +
                " | Error: " + errorMessage +
                " | data: " + data +
                " | monitoringApproach: " + monitoringApproach;
    }

    private String constructSuccessMessage(Long pmrvAccountId, String emitterId, String ca) {
        return "pmrvAccountId: " + pmrvAccountId +
                " | emitterId: " + emitterId +
                " | CA: " + ca;
    }
}
