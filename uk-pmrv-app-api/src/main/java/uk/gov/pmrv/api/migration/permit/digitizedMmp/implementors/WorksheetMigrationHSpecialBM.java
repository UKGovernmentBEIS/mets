package uk.gov.pmrv.api.migration.permit.digitizedMmp.implementors;

import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationError;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationException;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationUtils;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.WorksheetNames;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationEntity;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection46;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.AnnexIIPoint1;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.AnnexIIPoint2;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.SpecialProductType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics.AromaticsQuantitiesOfSupplementalFieldDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics.AromaticsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.dolime.DolimeDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.dolime.DolimeSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols.EthyleneOxideEthyleneGlycolsDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols.EthyleneOxideEthyleneGlycolsDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols.EthyleneOxideEthyleneGlycolsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.lime.LimeDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.lime.LimeSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts.RefineryProductsQuantitiesOfSupplementalFieldDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts.RefineryProductsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.steamcracking.SteamCrackingDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.steamcracking.SteamCrackingSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.vinylchloridemonomer.VinylChlorideMonomerDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.vinylchloridemonomer.VinylChlorideMonomerSP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationUtils.getHydrogenSynthesisGasPairAnnexValues;


@Service
public class WorksheetMigrationHSpecialBM implements WorksheetMigrationImplementor{

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public WorksheetNames getWorksheetname() {
        return WorksheetNames.H_SPECIAL_BM;
    }

    @Override
    public List<String> migrateWorksheet(Workbook workbook, List<MmpFilesMigrationEntity> accompanyingFiles, String accountIdString,
                                         String fileUuid, DigitizedPlan digitizedPlan) {
        Sheet sheet = workbook.getSheet(this.getWorksheetname().getName());
        return this.doMigrate_hSpecialBM(sheet, accountIdString, fileUuid, digitizedPlan);
    }

    private List<String> doMigrate_hSpecialBM(Sheet sheet, String accountIdString, String fileUuid,
                                                       DigitizedPlan digitizedPlan) {
        List<String> results = new ArrayList<>();

        try{
            for (int i = 0; i < digitizedPlan.getSubInstallations().size(); i++) {
                SubInstallation subInstallation = digitizedPlan.getSubInstallations().get(i);

                if (ObjectUtils.isEmpty(subInstallation.getSubInstallationType())  ||
                        !subInstallation.getSubInstallationType().isHasSpecialProduct())
                    continue;
                FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

                switch (subInstallation.getSubInstallationType()) {
                    case REFINERY_PRODUCTS -> {
                        RefineryProductsSP refineryProductsSP = new RefineryProductsSP();
                        refineryProductsSP.setSpecialProductType(SpecialProductType.REFINERY_PRODUCTS);
                        CellReference crACD_DS1 = new CellReference("I45");

                        List<RefineryProductsQuantitiesOfSupplementalFieldDataSource> dataSources = new ArrayList<>();
                        dataSources.add(new RefineryProductsQuantitiesOfSupplementalFieldDataSource());
                        dataSources.getFirst().setDataSourceNo("0");
                        dataSources.add(new RefineryProductsQuantitiesOfSupplementalFieldDataSource());
                        dataSources.get(1).setDataSourceNo("1");
                        dataSources.add(new RefineryProductsQuantitiesOfSupplementalFieldDataSource());
                        dataSources.get(2).setDataSourceNo("2");

                        for (int j = 45; j <= 100; j ++) {
                            CellReference crCWTfunctionDS1 = new CellReference(j - 1 , crACD_DS1.getCol());
                            String CWTDS1Val = DigitizedMmpMigrationUtils.getStringValueOfCell(crCWTfunctionDS1, sheet, evaluator);

                            CellReference crCWTfunctionDS2 = new CellReference(j - 1, crACD_DS1.getCol() + 2);
                            String CWTDS2Val = DigitizedMmpMigrationUtils.getStringValueOfCell(crCWTfunctionDS2, sheet, evaluator);

                            CellReference crCWTfunctionDS3 = new CellReference(j - 1, crACD_DS1.getCol() + 4);
                            String CWTDS3Val = DigitizedMmpMigrationUtils.getStringValueOfCell(crCWTfunctionDS3, sheet, evaluator);

                            if (ObjectUtils.isEmpty(CWTDS1Val) && ObjectUtils.isEmpty(CWTDS2Val) && ObjectUtils.isEmpty(CWTDS3Val))
                                continue;

                            CellReference crCWTFunction = new CellReference(j - 1 , crCWTfunctionDS1.getCol() - 4);
                            String CWTVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crCWTFunction, sheet, evaluator);
                            AnnexIIPoint1 CWTFunction = AnnexIIPoint1.getByValue(CWTVal.trim());
                            refineryProductsSP.getRefineryProductsRelevantCWTFunctions().add(CWTFunction);


                            AnnexVIISection44 CWTDS1 = AnnexVIISection44.getByValue(CWTDS1Val);
                            dataSources.getFirst().getDetails().put(CWTFunction, CWTDS1);

                            AnnexVIISection44 CWTDS2 = AnnexVIISection44.getByValue(CWTDS2Val);
                            dataSources.get(1).getDetails().put(CWTFunction, CWTDS2);

                            AnnexVIISection44 CWTDS3 = AnnexVIISection44.getByValue(CWTDS3Val);
                            dataSources.get(2).getDetails().put(CWTFunction, CWTDS3);

                        }

                        dataSources.removeIf(dataSource -> dataSource.getDetails().values().stream().allMatch(Objects::isNull));
                        refineryProductsSP.setRefineryProductsDataSources(dataSources);

                        int RP_descriptionBaseRow = 106;
                        int RP_HO_flagAndReason = 110;
                        int RP_HO_details = 113;

                        refineryProductsSP.setMethodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_methodologyAppliedDescription_SP(sheet, "E" + RP_descriptionBaseRow));
                        refineryProductsSP.setHierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + RP_HO_flagAndReason, "K" + RP_HO_flagAndReason, "F" + RP_HO_details));
                        subInstallation.setSpecialProduct(refineryProductsSP);

                        boolean isOrdered = IntStream.range(0, dataSources.size())
                                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

                        if (!isOrdered)
                            throw new DigitizedMmpMigrationException("RefineryProductsQuantitiesOfSupplementalFieldDataSources are not ordered!");
                    }
                    case LIME -> {
                        LimeSP limeSP = new LimeSP();
                        limeSP.setSpecialProductType(SpecialProductType.LIME);
                        CellReference crLIME_DS1 = new CellReference("I129");

                        List<LimeDataSource> dataSources = new ArrayList<>();

                        for (int j = 0; j < 3; j++) {
                            CellReference crLIME_DS = j == 0 ? crLIME_DS1 : new CellReference(crLIME_DS1.getRow(), crLIME_DS1.getCol() + ( j * 2 ));
                            String LIME_DSVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crLIME_DS, sheet, evaluator);
                            if (Strings.isNotBlank(LIME_DSVal)) {
                                LimeDataSource limeDataSource = LimeDataSource.builder().dataSourceNo(String.valueOf(j))
                                        .detail(AnnexVIISection46.getByValue(LIME_DSVal)).build();
                                dataSources.add(limeDataSource);
                            }
                        }

                        int LIME_descriptionBaseRow = 135;
                        int LIME_HO_flagAndReason = 139;
                        int LIME_HO_details = 142;

                        limeSP.setMethodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_methodologyAppliedDescription_SP(sheet, "E" + LIME_descriptionBaseRow));
                        limeSP.setHierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + LIME_HO_flagAndReason, "K" + LIME_HO_flagAndReason, "F" + LIME_HO_details));
                        limeSP.setDataSources(dataSources);
                        subInstallation.setSpecialProduct(limeSP);

                        boolean isOrdered = IntStream.range(0, dataSources.size())
                                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

                        if (!isOrdered)
                            throw new DigitizedMmpMigrationException("LimeDataSources are not ordered!");
                    }
                    case DOLIME -> {
                        DolimeSP dolimeSP = new DolimeSP();
                        dolimeSP.setSpecialProductType(SpecialProductType.DOLIME);
                        CellReference crDOLIME_DS1 = new CellReference("I158");

                        List<DolimeDataSource> dataSources = new ArrayList<>();

                        for (int j = 0; j < 3; j++) {
                            CellReference crDOLIME_DS = j == 0 ? crDOLIME_DS1 : new CellReference(crDOLIME_DS1.getRow(), crDOLIME_DS1.getCol() + ( j * 2 ));
                            String DOLIME_DSVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crDOLIME_DS, sheet, evaluator);
                            if (Strings.isNotBlank(DOLIME_DSVal)) {
                                DolimeDataSource dolimeDataSource = DolimeDataSource.builder().dataSourceNo(String.valueOf(j))
                                        .detail(AnnexVIISection46.getByValue(DOLIME_DSVal)).build();
                                dataSources.add(dolimeDataSource);
                            }
                        }

                        int DOLIME_descriptionBaseRow = 164;
                        int DOLIME_HO_flagAndReason = 168;
                        int DOLIME_HO_details = 171;

                        dolimeSP.setMethodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_methodologyAppliedDescription_SP(sheet, "E" + DOLIME_descriptionBaseRow));
                        dolimeSP.setHierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + DOLIME_HO_flagAndReason, "K" + DOLIME_HO_flagAndReason, "F" + DOLIME_HO_details));
                        dolimeSP.setDataSources(dataSources);
                        subInstallation.setSpecialProduct(dolimeSP);

                        boolean isOrdered = IntStream.range(0, dataSources.size())
                                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

                        if (!isOrdered)
                            throw new DigitizedMmpMigrationException("DolimeDataSources are not ordered!");
                    }
                    case STEAM_CRACKING -> {
                        SteamCrackingSP streamCrackingSP = new SteamCrackingSP();
                        streamCrackingSP.setSpecialProductType(SpecialProductType.STEAM_CRACKING);
                        CellReference crSC_DS1 = new CellReference("I187");

                        List<SteamCrackingDataSource> dataSources = new ArrayList<>();

                        for (int j = 0; j < 3; j++) {
                            CellReference crSC_DS = j == 0 ? crSC_DS1 : new CellReference(crSC_DS1.getRow(), crSC_DS1.getCol() + ( j * 2 ));
                            String SC_DSVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crSC_DS, sheet, evaluator);
                            if (Strings.isNotBlank(SC_DSVal)) {
                                SteamCrackingDataSource steamCrackingDataSource = SteamCrackingDataSource.builder().dataSourceNo(String.valueOf(j))
                                        .detail(AnnexVIISection44.getByValue(SC_DSVal)).build();
                                dataSources.add(steamCrackingDataSource);
                            }
                        }

                        int DOLIME_descriptionBaseRow = 193;
                        int DOLIME_HO_flagAndReason = 197;
                        int DOLIME_HO_details = 200;

                        streamCrackingSP.setMethodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_methodologyAppliedDescription_SP(sheet, "E" + DOLIME_descriptionBaseRow));
                        streamCrackingSP.setHierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + DOLIME_HO_flagAndReason, "K" + DOLIME_HO_flagAndReason, "F" + DOLIME_HO_details));
                        streamCrackingSP.setDataSources(dataSources);
                        subInstallation.setSpecialProduct(streamCrackingSP);

                        boolean isOrdered = IntStream.range(0, dataSources.size())
                                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

                        if (!isOrdered)
                            throw new DigitizedMmpMigrationException("SteamCrackingDataSources are not ordered!");
                    }
                    case AROMATICS -> {
                        AromaticsSP aromaticsSP = new AromaticsSP();
                        aromaticsSP.setSpecialProductType(SpecialProductType.AROMATICS);
                        CellReference crAromatics_DS1 = new CellReference("I221");

                        List<AromaticsQuantitiesOfSupplementalFieldDataSource> dataSources = new ArrayList<>();
                        dataSources.add(new AromaticsQuantitiesOfSupplementalFieldDataSource());
                        dataSources.getFirst().setDataSourceNo("0");
                        dataSources.add(new AromaticsQuantitiesOfSupplementalFieldDataSource());
                        dataSources.get(1).setDataSourceNo("1");
                        dataSources.add(new AromaticsQuantitiesOfSupplementalFieldDataSource());
                        dataSources.get(2).setDataSourceNo("2");

                        for (int j = 221; j <= 228; j ++) {
                            CellReference crCWTfunctionDS1 = new CellReference(j - 1 , crAromatics_DS1.getCol());
                            String CWTDS1Val = DigitizedMmpMigrationUtils.getStringValueOfCell(crCWTfunctionDS1, sheet, evaluator);

                            CellReference crCWTfunctionDS2 = new CellReference(j - 1, crAromatics_DS1.getCol() + 2);
                            String CWTDS2Val = DigitizedMmpMigrationUtils.getStringValueOfCell(crCWTfunctionDS2, sheet, evaluator);

                            CellReference crCWTfunctionDS3 = new CellReference(j - 1, crAromatics_DS1.getCol() + 4);
                            String CWTDS3Val = DigitizedMmpMigrationUtils.getStringValueOfCell(crCWTfunctionDS3, sheet, evaluator);

                            if (ObjectUtils.isEmpty(CWTDS1Val) && ObjectUtils.isEmpty(CWTDS2Val) && ObjectUtils.isEmpty(CWTDS3Val))
                                continue;

                            CellReference crCWTFunction = new CellReference(j - 1 , crCWTfunctionDS1.getCol() - 4);
                            String CWTVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crCWTFunction, sheet, evaluator);
                            AnnexIIPoint2 CWTFunction = AnnexIIPoint2.getByValue(CWTVal.trim());
                            aromaticsSP.getRelevantCWTFunctions().add(CWTFunction);


                            AnnexVIISection44 CWTDS1 = AnnexVIISection44.getByValue(CWTDS1Val);
                            dataSources.getFirst().getDetails().put(CWTFunction, CWTDS1);

                            AnnexVIISection44 CWTDS2 = AnnexVIISection44.getByValue(CWTDS2Val);
                            dataSources.get(1).getDetails().put(CWTFunction, CWTDS2);

                            AnnexVIISection44 CWTDS3 = AnnexVIISection44.getByValue(CWTDS3Val);
                            dataSources.get(2).getDetails().put(CWTFunction, CWTDS3);

                        }

                        dataSources.removeIf(dataSource -> dataSource.getDetails().values().stream().allMatch(Objects::isNull));
                        aromaticsSP.setDataSources(dataSources);

                        int Aromatics_descriptionBaseRow = 234;
                        int Aromatics_HO_flagAndReason = 238;
                        int Aromatics_HO_details = 241;

                        aromaticsSP.setMethodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_methodologyAppliedDescription_SP(sheet, "E" + Aromatics_descriptionBaseRow));
                        aromaticsSP.setHierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + Aromatics_HO_flagAndReason, "K" + Aromatics_HO_flagAndReason, "F" + Aromatics_HO_details));
                        subInstallation.setSpecialProduct(aromaticsSP);

                        boolean isOrdered = IntStream.range(0, dataSources.size())
                                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

                        if (!isOrdered)
                            throw new DigitizedMmpMigrationException("RefineryProductsQuantitiesOfSupplementalFieldDataSources are not ordered!");
                    }
                    case HYDROGEN -> {
                        HydrogenSP hydrogenSP = new HydrogenSP();
                        hydrogenSP.setSpecialProductType(SpecialProductType.HYDROGEN);
                        CellReference Hydrogen_starting_CR = new CellReference("I257");

                        List<HydrogenDataSource> dataSources = new ArrayList<>();

                        for (int j = 0; j < 3; j++) {
                            Map.Entry<AnnexVIISection44, AnnexVIISection46> annexValues =
                                    getHydrogenSynthesisGasPairAnnexValues(Hydrogen_starting_CR, j, sheet, evaluator);

                            if (!ObjectUtils.isEmpty(annexValues.getKey()) || !ObjectUtils.isEmpty(annexValues.getValue())) {
                                HydrogenDataSource hydrogenDataSource = HydrogenDataSource.builder()
                                        .dataSourceNo(String.valueOf(j))
                                        .details(HydrogenDetails.builder()
                                                .totalProduction(annexValues.getKey())
                                                .volumeFraction(annexValues.getValue())
                                                .build())
                                        .build();
                                dataSources.add(hydrogenDataSource);
                            }
                        }

                        int HYDROGEN_descriptionBaseRow = 264;
                        int HYDROGEN_descriptionBaseRow_HO_flagAndReason = 268;
                        int HYDROGEN_descriptionBaseRow_HO_details = 271;

                        hydrogenSP.setMethodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_methodologyAppliedDescription_SP(sheet, "E" + HYDROGEN_descriptionBaseRow));
                        hydrogenSP.setHierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + HYDROGEN_descriptionBaseRow_HO_flagAndReason, "K" + HYDROGEN_descriptionBaseRow_HO_flagAndReason, "F" + HYDROGEN_descriptionBaseRow_HO_details));
                        hydrogenSP.setDataSources(dataSources);
                        subInstallation.setSpecialProduct(hydrogenSP);

                        boolean isOrdered = IntStream.range(0, dataSources.size())
                                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

                        if (!isOrdered)
                            throw new DigitizedMmpMigrationException("HydrogenDataSources are not ordered!");
                    }
                    case SYNTHESIS_GAS -> {
                        SynthesisGasSP synthesisGasSP = new SynthesisGasSP();
                        synthesisGasSP.setSpecialProductType(SpecialProductType.SYNTHESIS_GAS);
                        CellReference SG_starting_CR = new CellReference("I287");

                        List<SynthesisGasDataSource> dataSources = new ArrayList<>();

                        for (int j = 0; j < 3; j++) {
                            Map.Entry<AnnexVIISection44, AnnexVIISection46> annexValues =
                                    getHydrogenSynthesisGasPairAnnexValues(SG_starting_CR, j, sheet, evaluator);

                            if (!ObjectUtils.isEmpty(annexValues.getKey()) || !ObjectUtils.isEmpty(annexValues.getValue())) {
                                SynthesisGasDataSource synthesisGasDataSource = SynthesisGasDataSource.builder()
                                        .dataSourceNo(String.valueOf(j))
                                        .details(SynthesisGasDetails.builder()
                                                .totalProduction(annexValues.getKey())
                                                .compositionData(annexValues.getValue())
                                                .build())
                                        .build();
                                dataSources.add(synthesisGasDataSource);
                            }
                        }

                        int SG_descriptionBaseRow = 294;
                        int SG_descriptionBaseRow_HO_flagAndReason = 298;
                        int SG_descriptionBaseRow_HO_details = 301;

                        synthesisGasSP.setMethodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_methodologyAppliedDescription_SP(sheet, "E" + SG_descriptionBaseRow));
                        synthesisGasSP.setHierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + SG_descriptionBaseRow_HO_flagAndReason, "K" + SG_descriptionBaseRow_HO_flagAndReason, "F" + SG_descriptionBaseRow_HO_details));
                        synthesisGasSP.setDataSources(dataSources);
                        subInstallation.setSpecialProduct(synthesisGasSP);

                        boolean isOrdered = IntStream.range(0, dataSources.size())
                                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

                        if (!isOrdered)
                            throw new DigitizedMmpMigrationException("SynthesisGasDataSources are not ordered!");
                    }
                    case ETHYLENE_OXIDE_ETHYLENE_GLYCOLS -> {
                        EthyleneOxideEthyleneGlycolsSP ethyleneOxideEthyleneGlycolsSP = new EthyleneOxideEthyleneGlycolsSP();
                        ethyleneOxideEthyleneGlycolsSP.setSpecialProductType(SpecialProductType.ETHYLENE_OXIDE_ETHYLENE_GLYCOLS);
                        CellReference EOEG_starting_CR = new CellReference("I317");

                        List<EthyleneOxideEthyleneGlycolsDataSource> dataSources = new ArrayList<>();

                        for (int j = 0; j < 3; j++) {
                            CellReference ethyleneOxideCell = j == 0
                                    ? EOEG_starting_CR
                                    : new CellReference(EOEG_starting_CR.getRow(), EOEG_starting_CR.getCol() + (j * 2));

                            String ethyleneOxideValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(ethyleneOxideCell, sheet, evaluator);

                            CellReference monothyleneGlycolCell = new CellReference(ethyleneOxideCell.getRow() + 1, ethyleneOxideCell.getCol());
                            String monothyleneGlycolValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(monothyleneGlycolCell, sheet, evaluator);

                            CellReference diethyleneGlycolCell = new CellReference(ethyleneOxideCell.getRow() + 2, ethyleneOxideCell.getCol());
                            String diethyleneGlycolValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(diethyleneGlycolCell, sheet, evaluator);

                            CellReference triethyleneGlycolCell = new CellReference(ethyleneOxideCell.getRow() + 3, ethyleneOxideCell.getCol());
                            String triethyleneGlycolValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(triethyleneGlycolCell, sheet, evaluator);

                            if (Strings.isNotBlank(ethyleneOxideValueStr) || Strings.isNotBlank(monothyleneGlycolValueStr)
                                    || Strings.isNotBlank(diethyleneGlycolValueStr) || Strings.isNotBlank(triethyleneGlycolValueStr)) {
                                EthyleneOxideEthyleneGlycolsDataSource ethyleneOxideEthyleneGlycolsDataSource = EthyleneOxideEthyleneGlycolsDataSource.builder()
                                        .dataSourceNo(String.valueOf(j))
                                        .details(EthyleneOxideEthyleneGlycolsDetails.builder()
                                                .ethyleneOxide(AnnexVIISection44.getByValue(ethyleneOxideValueStr))
                                                .monothyleneGlycol(AnnexVIISection44.getByValue(monothyleneGlycolValueStr))
                                                .diethyleneGlycol(AnnexVIISection44.getByValue(diethyleneGlycolValueStr))
                                                .triethyleneGlycol(AnnexVIISection44.getByValue(triethyleneGlycolValueStr)).build()).build();
                                dataSources.add(ethyleneOxideEthyleneGlycolsDataSource);
                            }

                            int EOEG_descriptionBaseRow = 326;
                            int EOEG_descriptionBaseRow_HO_flagAndReason = 330;
                            int EOEG_descriptionBaseRow_HO_details = 333;

                            ethyleneOxideEthyleneGlycolsSP.setMethodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_methodologyAppliedDescription_SP(sheet, "E" + EOEG_descriptionBaseRow));
                            ethyleneOxideEthyleneGlycolsSP.setHierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + EOEG_descriptionBaseRow_HO_flagAndReason, "K" + EOEG_descriptionBaseRow_HO_flagAndReason, "F" + EOEG_descriptionBaseRow_HO_details));
                            ethyleneOxideEthyleneGlycolsSP.setDataSources(dataSources);
                            subInstallation.setSpecialProduct(ethyleneOxideEthyleneGlycolsSP);

                            boolean isOrdered = IntStream.range(0, dataSources.size())
                                    .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

                            if (!isOrdered)
                                throw new DigitizedMmpMigrationException("EthyleneOxideEthyleneGlycolsDataSources are not ordered!");
                        }

                    }
                    case VINYL_CHLORIDE_MONOMER -> {
                        VinylChlorideMonomerSP vinylChlorideMonomerSP = new VinylChlorideMonomerSP();
                        vinylChlorideMonomerSP.setSpecialProductType(SpecialProductType.VINYL_CHLORIDE_MONOMER);
                        CellReference VCM_starting_CR = new CellReference("I349");

                        List<VinylChlorideMonomerDataSource> dataSources = new ArrayList<>();

                        for (int j = 0; j < 3; j++) {
                            CellReference crVCM_DS = j == 0 ? VCM_starting_CR : new CellReference(VCM_starting_CR.getRow(), VCM_starting_CR.getCol() + ( j * 2 ));
                            String SC_DSVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crVCM_DS, sheet, evaluator);

                            if (Strings.isNotBlank(SC_DSVal)) {
                                VinylChlorideMonomerDataSource vinylChlorideMonomerDataSource = VinylChlorideMonomerDataSource.builder()
                                        .dataSourceNo(String.valueOf(j))
                                        .detail(AnnexVIISection45.getByValue(SC_DSVal))
                                        .build();
                                dataSources.add(vinylChlorideMonomerDataSource);
                            }
                        }

                        int SG_descriptionBaseRow = 355;
                        int SG_descriptionBaseRow_HO_flagAndReason = 359;
                        int SG_descriptionBaseRow_HO_details = 362;

                        vinylChlorideMonomerSP.setMethodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_methodologyAppliedDescription_SP(sheet, "E" + SG_descriptionBaseRow));
                        vinylChlorideMonomerSP.setHierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + SG_descriptionBaseRow_HO_flagAndReason, "K" + SG_descriptionBaseRow_HO_flagAndReason, "F" + SG_descriptionBaseRow_HO_details));
                        vinylChlorideMonomerSP.setDataSources(dataSources);
                        subInstallation.setSpecialProduct(vinylChlorideMonomerSP);

                        boolean isOrdered = IntStream.range(0, dataSources.size())
                                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

                        if (!isOrdered)
                            throw new DigitizedMmpMigrationException("VinylChlorideMonomerDataSources are not ordered!");
                    }
                }

            }
        } catch (DigitizedMmpMigrationException e) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                    .fileUuid(fileUuid).worksheetName(sheet.getSheetName()).errorReport(e.getMessage()).build()));
        }

        return results;

    }


}
