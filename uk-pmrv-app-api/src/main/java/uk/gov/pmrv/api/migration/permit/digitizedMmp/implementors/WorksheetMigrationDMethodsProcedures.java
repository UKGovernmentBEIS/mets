package uk.gov.pmrv.api.migration.permit.digitizedMmp.implementors;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationError;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationException;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationUtils;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.WorksheetNames;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationEntity;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.methodtasks.MethodTask;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.methodtasks.MethodTaskConnection;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.procedures.Procedure;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.procedures.ProcedureType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorksheetMigrationDMethodsProcedures implements WorksheetMigrationImplementor{

    private final static int METHODS_PHYSICAL_PARTS_ROWS = 15;
    private final static int METHODS_SUB_INSTALLATION_COLUMNS = 5;
    private final static String ASSIGNMENT_OF_RESPONSIBILITIES_START_CELL = "G61";
    private final static String MONITORING_PLAN_APPROPRIATENESS_START_CELL = "G73";
    private final static String DATA_FLOW_ACTIVITIES_START_CELL = "G84";
    private final static String CONTROL_ACTIVITIES_START_CELL = "G95";



    @Override
    public int getOrder() {return 4;}

    @Override
    public WorksheetNames getWorksheetname() {return WorksheetNames.D_METHODS_PROCEDURES;}

    @Override
    public List<String> migrateWorksheet(Workbook workbook, List<MmpFilesMigrationEntity> accompanyingFiles, String accountIdString, String fileUuid, DigitizedPlan digitizedPlan) {
        Sheet sheet = workbook.getSheet(this.getWorksheetname().getName());
        List<String> results = new ArrayList<>();
        try {
            doMigrate_dMethodsProcedures(sheet,digitizedPlan);
        } catch (DigitizedMmpMigrationException e) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                    .fileUuid(fileUuid).worksheetName(sheet.getSheetName()).errorReport(e.getMessage()).build()));
        }
        return results;
    }

    private void doMigrate_dMethodsProcedures(Sheet sheet, DigitizedPlan digitizedPlan) throws DigitizedMmpMigrationException {
        migrateMethods(sheet,digitizedPlan);
        migrateProcedures(sheet,digitizedPlan);
    }

    private void migrateMethods(Sheet sheet, DigitizedPlan digitizedPlan) throws DigitizedMmpMigrationException {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        if((digitizedPlan.getSubInstallations().isEmpty()) || (digitizedPlan.getSubInstallations().size() == 1)) {
            String avoidDoubleCount = getMethodsToAvoidDoubleCounting(new CellReference("E50"),sheet,evaluator);
            digitizedPlan.setMethodTask(MethodTask.builder().avoidDoubleCount(avoidDoubleCount).avoidDoubleCountToggle(Boolean.TRUE).build());
        }
        CellReference cellReference = new CellReference("F20");
        List<MethodTaskConnection> methodTaskConnections = new ArrayList<>();
        for (int i = 0; i < METHODS_PHYSICAL_PARTS_ROWS; i++) {
            CellReference currentCellReference = new CellReference(cellReference.getRow()+i,cellReference.getCol());
            String value = DigitizedMmpMigrationUtils.getStringValueOfCell(currentCellReference,sheet,evaluator);
            if(StringUtils.hasText(value)) {
                List<String> subInstallations = getSubInstallations(currentCellReference,sheet,evaluator);
                methodTaskConnections.add(getMethodTaskConnection(String.valueOf(methodTaskConnections.size()),value,subInstallations));
            }
        }
        MethodTask methodTask = MethodTask.builder().physicalPartsAndUnitsAnswer(!methodTaskConnections.isEmpty())
                .connections(methodTaskConnections).avoidDoubleCount(getMethodsToAvoidDoubleCounting(new CellReference("E50"),sheet,evaluator))
                .assignParts(getMethodsToAssignParts(new CellReference("E41"),sheet,evaluator)).build();
        methodTask.setAvoidDoubleCountToggle(Boolean.TRUE);
        digitizedPlan.setMethodTask(methodTask);
    }

    private void migrateProcedures(Sheet sheet, DigitizedPlan digitizedPlan) {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        Map<ProcedureType,Procedure> procedures = new HashMap<>();
        procedures.put(ProcedureType.ASSIGNMENT_OF_RESPONSIBILITIES,extractProcedureSectionFromSheet(sheet,evaluator,ASSIGNMENT_OF_RESPONSIBILITIES_START_CELL));
        procedures.put(ProcedureType.MONITORING_PLAN_APPROPRIATENESS,extractProcedureSectionFromSheet(sheet,evaluator,MONITORING_PLAN_APPROPRIATENESS_START_CELL));
        procedures.put(ProcedureType.DATA_FLOW_ACTIVITIES,extractProcedureSectionFromSheet(sheet,evaluator,DATA_FLOW_ACTIVITIES_START_CELL));
        procedures.put(ProcedureType.CONTROL_ACTIVITIES,extractProcedureSectionFromSheet(sheet,evaluator,CONTROL_ACTIVITIES_START_CELL));
        digitizedPlan.setProcedures(procedures);
    }

    private Procedure extractProcedureSectionFromSheet(Sheet sheet,FormulaEvaluator evaluator,String startingCell) {
        CellReference cellReference = new CellReference(startingCell);
        return Procedure.builder().
                procedureName(DigitizedMmpMigrationUtils.getStringValueOfCell(cellReference,sheet,evaluator)).
                procedureReference(DigitizedMmpMigrationUtils.getStringValueOfCell(new CellReference(
                        cellReference.getRow() + 1, cellReference.getCol()),sheet,evaluator)).
                diagramReference(DigitizedMmpMigrationUtils.getStringValueOfCell(new CellReference(
                        cellReference.getRow() + 2, cellReference.getCol()),sheet,evaluator)).
                procedureDescription(DigitizedMmpMigrationUtils.getStringValueOfCell(new CellReference(
                        cellReference.getRow() + 3, cellReference.getCol()),sheet,evaluator)).
                dataMaintenanceResponsibleEntity(DigitizedMmpMigrationUtils.getStringValueOfCell(new CellReference(
                        cellReference.getRow() + 4, cellReference.getCol()),sheet,evaluator)).
                locationOfRecords(DigitizedMmpMigrationUtils.getStringValueOfCell(new CellReference(
                        cellReference.getRow() + 5, cellReference.getCol()),sheet,evaluator)).
                itSystemUsed(DigitizedMmpMigrationUtils.getStringValueOfCell(new CellReference(
                        cellReference.getRow() + 6, cellReference.getCol()),sheet,evaluator)).
                standardsAppliedList(DigitizedMmpMigrationUtils.getStringValueOfCell(new CellReference(
                        cellReference.getRow() + 7, cellReference.getCol()),sheet,evaluator)).build();
    }

    private List<String> getSubInstallations(CellReference physicalPartReference,Sheet sheet,FormulaEvaluator evaluator) {
        List<String> subInstallations = new ArrayList<>();
        CellReference subInstallationReference = new CellReference(physicalPartReference.getRow(),CellReference.convertColStringToIndex("J"));
        for (int i = 1; i<= METHODS_SUB_INSTALLATION_COLUMNS; i++) {
            String value = DigitizedMmpMigrationUtils.getStringValueOfCell(subInstallationReference,sheet,evaluator);
            if(StringUtils.hasText(value)) {
                subInstallations.add(value);
            }
            subInstallationReference = new CellReference(subInstallationReference.getRow(),subInstallationReference.getCol()+1);
        }
        return subInstallations;
    }

    private MethodTaskConnection getMethodTaskConnection(String id,String name,List<String> subInstallations) throws DigitizedMmpMigrationException {
        List<SubInstallationType> subInstallationTypes;
        try {
            subInstallationTypes = subInstallations.stream().map(SubInstallationType::getByValue).toList();
        } catch (IllegalArgumentException e) {
            throw new DigitizedMmpMigrationException(
                    String.format("Unable to map relevant sub-installation of physical part %s during the migration of methods at installation level with cause [%s]", name,e.getMessage()));
        }
        if(subInstallationTypes.isEmpty()) {
            throw new DigitizedMmpMigrationException(
                    String.format("No relevant sub installations found for physical part %s during the migration of methods at installation level", name));
        }
        return MethodTaskConnection.builder().itemId(id).itemName(name).subInstallations(subInstallationTypes).build();
    }

    private String getMethodsToAssignParts(CellReference cellReference,Sheet sheet,FormulaEvaluator evaluator) {
        CellReference e42Reference = DigitizedMmpMigrationUtils.getNextRow(cellReference);
        CellReference e43Reference = DigitizedMmpMigrationUtils.getNextRow(e42Reference);

        String e41Value = DigitizedMmpMigrationUtils.getStringValueOfCell(cellReference,sheet,evaluator);
        String e42Value = DigitizedMmpMigrationUtils.getStringValueOfCell(e42Reference,sheet,evaluator);
        String e43Value = DigitizedMmpMigrationUtils.getStringValueOfCell(e43Reference,sheet,evaluator);

        String externalFiles = DigitizedMmpMigrationUtils.getStringValueOfCell(new CellReference("K45"),sheet,evaluator);

        String methodsToAssignParts = e41Value.concat(e42Value).concat(e43Value);
        if(StringUtils.hasText(externalFiles)) {
            methodsToAssignParts = methodsToAssignParts.concat("\r\r").concat("Reference to external files if relevant: "+externalFiles);
        }
        return StringUtils.hasText(methodsToAssignParts) ? methodsToAssignParts : null;
    }

    private String getMethodsToAvoidDoubleCounting(CellReference cellReference,Sheet sheet,FormulaEvaluator evaluator) {
        String methods = DigitizedMmpMigrationUtils.getStringValueOfCell(cellReference,sheet,evaluator);
        String externalFiles = DigitizedMmpMigrationUtils.getStringValueOfCell(new CellReference("K52"),sheet,evaluator);
        if(StringUtils.hasText(externalFiles)) {
            methods = methods.concat("\r\r").concat("Reference to external files if relevant: "+externalFiles);
        }
        return StringUtils.hasText(methods) ? methods : null;
    }
}
