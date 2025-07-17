package uk.gov.pmrv.api.migration.permit.digitizedMmp.implementors;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.docx4j.wml.P;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationError;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationException;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationUtils;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.MmpFileType;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.WorksheetNames;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFileAttachmentRepository;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationEntity;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription.DigitizedMmpInstallationDescription;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription.EntityType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription.FlowDirection;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription.InstallationConnection;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription.InstallationConnectionType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorksheetMigrationCInstallationDescription implements WorksheetMigrationImplementor {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final MmpFileAttachmentRepository mmpFileAttachmentRepository;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public WorksheetNames getWorksheetname() {
        return WorksheetNames.C_INSTALLATION_DESCRIPTION;
    }

    @Override
    public List<String> migrateWorksheet(Workbook workbook, List<MmpFilesMigrationEntity> accompanyingFiles, String accountIdString,
                                         String fileUuid, DigitizedPlan digitizedPlan) {
        Sheet sheet = workbook.getSheet(this.getWorksheetname().getName());
        return this.doMigrate_cInstallationDescriptionSheet(sheet, accompanyingFiles, accountIdString, fileUuid, digitizedPlan);
    }

    private List<String> doMigrate_cInstallationDescriptionSheet(Sheet sheet, List<MmpFilesMigrationEntity> accompanyingFiles,
                                                                 String accountIdString, String fileUuid, DigitizedPlan digitizedPlan) {
        DigitizedMmpInstallationDescription.DigitizedMmpInstallationDescriptionBuilder installationDescriptionBuilder = DigitizedMmpInstallationDescription.builder();
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        List<String> results = new ArrayList<>();
        List<SubInstallation> subInstallations = new ArrayList<>();

        try {
            // DigitizedMmpInstallationDescription.description
            CellReference crE49 = new CellReference("E49");
            String e49Val = DigitizedMmpMigrationUtils.getStringValueOfCell(crE49, sheet, evaluator);
            CellReference crE50 = DigitizedMmpMigrationUtils.getNextRow(crE49);
            String e50Val = DigitizedMmpMigrationUtils.getStringValueOfCell(crE50, sheet, evaluator);
            CellReference crE51 = DigitizedMmpMigrationUtils.getNextRow(crE50);
            String e51Val = DigitizedMmpMigrationUtils.getStringValueOfCell(crE51, sheet, evaluator);

            String j56Val = DigitizedMmpMigrationUtils.getStringValueOfCell(new CellReference("J56"), sheet, evaluator);

            installationDescriptionBuilder.description(e49Val.concat(e50Val).concat(e51Val).concat("\r\r")
                .concat("\r\r").concat("Reference to a flow diagram: ").concat(j56Val)
            );

            // DigitizedMmpInstallationDescription.connections (InstallationConnection)
            List<InstallationConnection> installationConnectionList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                InstallationConnection.InstallationConnectionBuilder builder = InstallationConnection.builder();

                CellReference crFxx = new CellReference("F90");
                String fxxVal = DigitizedMmpMigrationUtils.getStringValueOfCell(i == 0 ? crFxx : new CellReference(crFxx.getRow() + i, crFxx.getCol()), sheet, evaluator);
                CellReference crIxx = new CellReference("I90");
                String ixxVal = DigitizedMmpMigrationUtils.getStringValueOfCell(i == 0 ? crIxx : new CellReference(crIxx.getRow() + i, crIxx.getCol()), sheet, evaluator);
                CellReference crKxx = new CellReference("K90");
                String kxxVal = DigitizedMmpMigrationUtils.getStringValueOfCell(i == 0 ? crKxx : new CellReference(crKxx.getRow() + i, crKxx.getCol()), sheet, evaluator);
                CellReference crMxx = new CellReference("M90");
                String mxxVal = DigitizedMmpMigrationUtils.getStringValueOfCell(i == 0 ? crMxx : new CellReference(crMxx.getRow() + i, crMxx.getCol()), sheet, evaluator);

                if (!StringUtils.hasText(fxxVal) && !StringUtils.hasText(ixxVal) && !StringUtils.hasText(kxxVal) && !StringUtils.hasText(mxxVal)) {
                    continue;
                }
                builder
                    .connectionNo(String.valueOf(i))
                    .installationOrEntityName(fxxVal)
                    .entityType(EntityType.getByValue(ixxVal))
                    .connectionType(InstallationConnectionType.getByValue(kxxVal))
                    .flowDirection(FlowDirection.getByValue(mxxVal))
                ;
                CellReference crF2xx = new CellReference("F104");
                String f2xxVal = DigitizedMmpMigrationUtils.getStringValueOfCell(i == 0 ? crF2xx : new CellReference(crF2xx.getRow() + i, crF2xx.getCol()), sheet, evaluator);
                CellReference crI2xx = new CellReference("H104");
                String i2xxVal = DigitizedMmpMigrationUtils.getStringValueOfCell(i == 0 ? crI2xx : new CellReference(crI2xx.getRow() + i, crI2xx.getCol()), sheet, evaluator);
                CellReference crK2xx = new CellReference("J104");
                String k2xxVal = DigitizedMmpMigrationUtils.getStringValueOfCell(i == 0 ? crK2xx : new CellReference(crK2xx.getRow() + i, crK2xx.getCol()), sheet, evaluator);
                CellReference crM2xx = new CellReference("M104");
                String m2xxVal = DigitizedMmpMigrationUtils.getStringValueOfCell(i == 0 ? crM2xx : new CellReference(crM2xx.getRow() + i, crM2xx.getCol()), sheet, evaluator);
                builder
                    .installationId(StringUtils.hasText(f2xxVal) ? f2xxVal : "N/A")
                    .contactPersonName(StringUtils.hasText(i2xxVal) ? i2xxVal : "N/A")
                    .emailAddress(StringUtils.hasText(k2xxVal) ? k2xxVal : "N/A")
                    .phoneNumber(StringUtils.hasText(m2xxVal) ? m2xxVal : "N/A");

                installationConnectionList.add(builder.build());
            }
            installationDescriptionBuilder.connections(installationConnectionList);
            installationDescriptionBuilder.flowDiagrams(handleFlowDiagrams(accompanyingFiles));

            // DigitizedMmp.SubInstallations

            // Process Product Benchmarks (E17 starting cell, 10 rows)
            processSubInstallations(sheet, subInstallations, 0, 10, "E17", evaluator, false);

            // Process Fallback Approaches (E37 starting cell, next 10 rows)
             processSubInstallations(sheet, subInstallations, subInstallations.size(), 7, "E37", evaluator, true);

        } catch (DigitizedMmpMigrationException e) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                .fileUuid(fileUuid).worksheetName(sheet.getSheetName()).errorReport(e.getMessage()).build()));
        }

        digitizedPlan.setInstallationDescription(installationDescriptionBuilder.build());
        digitizedPlan.setSubInstallations(subInstallations);

        return results;
    }

    private Set<UUID> handleFlowDiagrams(List<MmpFilesMigrationEntity> flowDiagrams) {
        for (MmpFilesMigrationEntity flowDiagram : flowDiagrams) {
            FileAttachment fileAttachment = fileAttachmentRepository.save(mapMigrationEntityToAttachment(flowDiagram));
            flowDiagram.setFileUuid(fileAttachment.getUuid());
        }
        return flowDiagrams.stream().filter(file -> MmpFileType.FLOW_DIAGRAM.equals(file.getMmpFileType()))
                .map(MmpFilesMigrationEntity::getFileUuid).map(UUID::fromString).collect(
                        Collectors.toSet());
    }

    // Method to handle the common logic for processing subInstallations
    private void processSubInstallations(Sheet sheet, List<SubInstallation> subInstallations, int startRow, int rowCount, String startCell, FormulaEvaluator evaluator, boolean isFallbackApproach) throws DigitizedMmpMigrationException {
        for (int i = startRow; i < startRow + rowCount; i++) {
            SubInstallation.SubInstallationBuilder builder = SubInstallation.builder();

            CellReference cr = new CellReference(startCell);
            if (isFallbackApproach) {
                CellReference rightCell = i == startRow ? new CellReference(cr.getRow(), cr.getCol() + 6)
                        : new CellReference(cr.getRow() + i - startRow, cr.getCol() + 6);

                boolean isRelative = DigitizedMmpMigrationUtils.getCellValueBoolean(
                        rightCell,
                        sheet
                );

                if (!isRelative)
                    continue;

            }
            String cellValue = DigitizedMmpMigrationUtils.getStringValueOfCell(
                    i == startRow ? cr : new CellReference(cr.getRow() + i - startRow, cr.getCol()),
                    sheet,
                    evaluator
            );

            if (!StringUtils.hasText(cellValue)) {
                continue;
            }

            builder
                    .subInstallationNo(String.valueOf(subInstallations.size()))
                    .subInstallationType(SubInstallationType.getByValue(cellValue));

            subInstallations.add(builder.build());
        }
    }

    private FileAttachment mapMigrationEntityToAttachment(MmpFilesMigrationEntity migrationEntity) {
        return FileAttachment.builder()
                .fileContent(migrationEntity.getFileContent())
                .fileName(migrationEntity.getFileName())
                .uuid(String.valueOf(UUID.randomUUID()))
                .fileSize(migrationEntity.getFileContent().length)
                .fileType(MimeTypeUtils.detect(migrationEntity.getFileContent(),migrationEntity.getFileName()))
                .status(FileStatus.SUBMITTED)
                .createdBy(MigrationConstants.MIGRATION_PROCESS_USER)
                .build();
    }
}
