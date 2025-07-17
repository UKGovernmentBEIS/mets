package uk.gov.pmrv.api.migration.permit.digitizedMmp;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.implementors.WorksheetMigrationImplementor;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationEntity;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationRepository;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.MonitoringMethodologyPlans;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.*;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.procedures.Procedure;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.procedures.ProcedureType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.*;


import uk.gov.pmrv.api.permit.repository.PermitRepository;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.permit.validation.PermitGrantedValidatorService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class DigitizedMmpMigrationService extends MigrationBaseService {

    private final List<WorksheetMigrationImplementor> migrationImplementors;

    private final MmpFilesMigrationRepository mmpFilesMigrationRepository;

    private final PermitGrantedValidatorService permitGrantedValidatorService;

    private final PermitQueryService permitQueryService;

    private final PermitRepository permitRepo;

    @Override
    public String getResource() {
        return "digitized-mmp";
    }

    @Override
    public List<String> migrate(String ids) {
        final List<String> results = new ArrayList<>();
        Set<String> accountIds = Set.of(ids.trim().split("\\s*,\\s*"));

        for (String accountIdString : accountIds) {
            Long accountId = -1l;
            try {
                accountId = Long.parseLong(accountIdString);
            } catch (NumberFormatException e) {
                results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                    .errorReport("wrong accountId format!").build()));
                continue;
            }

            List<MmpFilesMigrationEntity> allFiles = mmpFilesMigrationRepository.findByAccountId(accountId);
            List<MmpFilesMigrationEntity> mmpFilesMigrationEntities =
                allFiles.stream().filter(entity -> MmpFileType.MMP.equals(entity.getMmpFileType())).toList();
            if (CollectionUtils.isEmpty(mmpFilesMigrationEntities)){
                results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                    .errorReport("Cannot find MMP file to parse!").build()));
                continue;
            } else if (mmpFilesMigrationEntities.size() > 1) {
                results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                    .errorReport("More than one MMP files found for same accountId. There can be only one!").build()));
                continue;
            }
            MmpFilesMigrationEntity mmpFile = mmpFilesMigrationEntities.get(0);

            List<MmpFilesMigrationEntity> accompanyingFiles = mmpFilesMigrationRepository.findByAccountId(accountId)
                .stream().filter(entity -> !MmpFileType.MMP.equals(entity.getMmpFileType())).toList();

            results.addAll(this.doMigrate(mmpFile, accompanyingFiles, accountIdString, mmpFile.getFileUuid()));
        }

        return results;
    }

    private List<String> doMigrate(MmpFilesMigrationEntity mmpFilesMigrationEntity, List<MmpFilesMigrationEntity> accompanyingFiles, String accountIdString, String fileUuid) {
        List<String> results = new ArrayList<>();
        DigitizedPlan digitizedMmp = new DigitizedPlan();
        byte[] fileContent = mmpFilesMigrationEntity.getFileContent();

        List<WorksheetMigrationImplementor> sortedMigrationImplementors = migrationImplementors.stream()
                .sorted(Comparator.comparingInt(WorksheetMigrationImplementor::getOrder))
                .toList();

        ZipSecureFile.setMinInflateRatio(0.0001);
        ZipSecureFile.setMaxEntrySize(200 * 1024 * 1024);

        try (InputStream fis = new ByteArrayInputStream(fileContent);
             Workbook workbook = new XSSFWorkbook(fis)) {

            results.addAll(Stream.of(WorksheetNames.values())
                .flatMap(worksheetName -> sortedMigrationImplementors.stream()
                    .filter(migrationImplementor -> migrationImplementor.getWorksheetname().equals(worksheetName))
                    .map(migrationImplementor -> migrationImplementor.migrateWorksheet(workbook, accompanyingFiles, accountIdString, fileUuid, digitizedMmp))
                )
                .flatMap(Collection::stream)
                .toList());

            } catch (IOException e) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                .fileUuid(fileUuid).errorReport("Cannot open workbook!").build()));
        }

        // validate final object
        try {
            PermitContainer permitContainerToValidate = permitQueryService.getPermitContainerByAccountId(Long.valueOf(accountIdString));
            MonitoringMethodologyPlans mmp = permitContainerToValidate.getPermit().getMonitoringMethodologyPlans();
            mmp.setDigitizedPlan(digitizedMmp);
            mmp.setExist(true);
            handlePermitAttachments(permitContainerToValidate,accompanyingFiles);

            permitGrantedValidatorService.validatePermit(permitContainerToValidate);

            //set new permit container to permit entity if there is no validation error
            Optional<PermitEntity> permitEntityOptional = permitRepo.findByAccountId(Long.valueOf(accountIdString));
            PermitEntity permitEntity = permitEntityOptional.orElse(null);
            if (!ObjectUtils.isEmpty(permitEntity)) {
                permitEntity.setPermitContainer(permitContainerToValidate);
                permitRepo.save(permitEntity);
            }


        } catch (BusinessException e) {
            String concatenatedMessages = Arrays.stream(e.getData())
                    .filter(PermitViolation.class::isInstance) // Filter only instances of PermitViolation
                    .map(PermitViolation.class::cast) // Cast each element to PermitViolation
                    .map(violation -> {
                        // Main violation message
                        String violationMessage = violation.getMessage();

                        // Concatenate any messages within the 'data' array of each violation
                        String additionalMessages = Arrays.stream(violation.getData())
                                .map(Object::toString)
                                .collect(Collectors.joining("; "));

                        return violationMessage + (additionalMessages.isEmpty() ? "" : "; " + additionalMessages);
                    })
                    .collect(Collectors.joining("; "));

            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(
                    DigitizedMmpMigrationError.builder()
                            .accountId(accountIdString)
                            .fileUuid(fileUuid)
                            .errorReport(concatenatedMessages)
                            .build()));

            return results;
        }

        catch (Exception e) {
            if(e instanceof ConstraintViolationException constraintViolationException) {
                StringBuilder errors = new StringBuilder();
                constraintViolationException.getConstraintViolations().forEach(constraintViolation -> {
                    errors.append(constraintViolation.getPropertyPath()).append(":").append(constraintViolation.getMessage()).append(';');
                });
                results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                        .fileUuid(fileUuid).errorReport(errors.toString()).build()));
                return results;
            }
            else {
                results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                        .fileUuid(fileUuid).errorReport(e.getMessage()).build()));
                return results;
            }

        }

        if (results.isEmpty()) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                .fileUuid(fileUuid).errorReport("MMP successfully migrated!").build()));
        }
        return results;
    }

    private void handlePermitAttachments(PermitContainer permitContainer,List<MmpFilesMigrationEntity> accompanyingFiles) {
        Map<UUID,String> permitAttachmentsExisting = permitContainer.getPermitAttachments();
        Map<UUID,String> permitAttachments = accompanyingFiles.stream().collect(Collectors.toMap(item ->
                        UUID.fromString(item.getFileUuid()),MmpFilesMigrationEntity::getFileName));
        for (Map.Entry<UUID, String> entry : permitAttachments.entrySet()) {
            permitAttachmentsExisting.putIfAbsent(entry.getKey(), entry.getValue());
        }
        permitContainer.setPermitAttachments(permitAttachmentsExisting);
    }
}
