import { mmpEnergyFlowsStatus } from '@permit-application/mmp-energy-flows/mmp-energy-flows-status';
import { mmpInstallationDescriptionStatus } from '@permit-application/mmp-installation-description/mmp-installation-description-status';
import { mmpMethodsStatus } from '@permit-application/mmp-methods/mmp-methods';
import { mmpProceduresStatus } from '@permit-application/mmp-procedures/mmp-procedures-status';
import {
  mmpSubInstallationFallbackApproachStatus,
  mmpSubInstallationProductBenchmarkStatus,
  mmpSubInstallationStatus,
} from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';

import { monitoringApproachesStatus } from '../../approaches/approach-status';
import {
  categoryTierStatus as CalculationCategoryTierStatus,
  categoryTierSubtaskStatus as CalculationCategoryTierSubtaskStatus,
  planStatus as CalculationPlanStatus,
  status as CalculationStatus,
} from '../../approaches/calculation/calculation-status';
import {
  FALLBACKCategoryTierStatus,
  FALLBACKCategoryTierSubtaskStatus,
  FALLBACKStatus,
} from '../../approaches/fallback/fallback-status';
import { INHERENT_CO2Status } from '../../approaches/inherent-co2/inherent-co2-status';
import {
  MEASUREMENTCategoryTierStatus,
  MEASUREMENTCategoryTierSubtaskStatus,
  MEASUREMENTStatus,
} from '../../approaches/measurement/measurement-status';
import { N2OCategoryTierStatus, N2OCategoryTierSubtaskStatus, N2OStatus } from '../../approaches/n2o/n2o-status';
import {
  categoryTierStatus as PFCCategoryTierStatus,
  categoryTierSubtaskStatus as PFCCategoryTierSubtaskStatus,
  PFCTier2EmissionFactorSubtaskStatus,
  status as PFCStatus,
} from '../../approaches/pfc/pfc-status';
import {
  TRANSFERRED_CO2_PipelineStatus,
  TRANSFERRED_CO2Status,
} from '../../approaches/transferred-co2/transferred-co2-status';
import { uncertaintyAnalysisStatus } from '../../approaches/uncertainty-analysis/uncertainty-analysis-status';
import { emissionSummariesStatus } from '../../emission-summaries/emission-summaries-status';
import { PermitApplicationState } from '../../store/permit-application.state';
import { StatusKey } from '../types/permit-task.type';

export function resolvePermitSectionStatus(state: PermitApplicationState, key: StatusKey, index?: number) {
  if (!state.isRequestTask) {
    return 'complete';
  } else {
    switch (key) {
      case 'permitType':
        return state.permitType ? 'complete' : 'not started';
      case 'sourceStreams':
      case 'emissionPoints':
      case 'emissionSources':
      case 'measurementDevicesOrMethods':
        return state.permitSectionsCompleted[key]?.[0]
          ? 'complete'
          : state.permit[key]?.length > 0
            ? 'in progress'
            : 'not started';
      case 'emissionSummaries':
        return emissionSummariesStatus(state);
      case 'monitoringApproaches':
        return monitoringApproachesStatus(state);
      case 'CALCULATION_CO2':
        return CalculationStatus(state);
      case 'CALCULATION_CO2_Plan':
        return CalculationPlanStatus(state);
      case 'CALCULATION_CO2_Category_Tier':
        return CalculationCategoryTierStatus(state, index);
      case 'CALCULATION_CO2_Category':
      case 'CALCULATION_CO2_Emission_Factor':
      case 'CALCULATION_CO2_Calorific':
      case 'CALCULATION_CO2_Biomass_Fraction':
      case 'CALCULATION_CO2_Carbon_Content':
      case 'CALCULATION_CO2_Oxidation_Factor':
      case 'CALCULATION_CO2_Conversion_Factor':
      case 'CALCULATION_CO2_Activity_Data':
        return CalculationCategoryTierSubtaskStatus(state, key, index);
      case 'FALLBACK':
        return FALLBACKStatus(state);
      case 'FALLBACK_Category_Tier':
        return FALLBACKCategoryTierStatus(state, index);
      case 'FALLBACK_Category':
        return FALLBACKCategoryTierSubtaskStatus(state, index);
      case 'INHERENT_CO2':
        return INHERENT_CO2Status(state);
      case 'MEASUREMENT_CO2':
        return MEASUREMENTStatus(state);
      case 'MEASUREMENT_CO2_Category_Tier':
        return MEASUREMENTCategoryTierStatus(state, index);
      case 'MEASUREMENT_CO2_Category':
      case 'MEASUREMENT_CO2_Measured_Emissions':
      case 'MEASUREMENT_CO2_Applied_Standard':
      case 'MEASUREMENT_CO2_Biomass_Fraction':
        return MEASUREMENTCategoryTierSubtaskStatus(state, key, index);
      case 'MEASUREMENT_N2O':
        return N2OStatus(state);
      case 'MEASUREMENT_N2O_Category_Tier':
        return N2OCategoryTierStatus(state, index);
      case 'MEASUREMENT_N2O_Category':
      case 'MEASUREMENT_N2O_Measured_Emissions':
      case 'MEASUREMENT_N2O_Applied_Standard':
        return N2OCategoryTierSubtaskStatus(state, key, index);
      case 'CALCULATION_PFC':
        return PFCStatus(state);
      case 'CALCULATION_PFC_Category_Tier':
        return PFCCategoryTierStatus(state, index);
      case 'CALCULATION_PFC_Category':
      case 'CALCULATION_PFC_Activity_Data':
      case 'CALCULATION_PFC_Emission_Factor':
        return PFCCategoryTierSubtaskStatus(state, key, index);
      case 'CALCULATION_PFC_Tier2EmissionFactor':
        return PFCTier2EmissionFactorSubtaskStatus(state);
      case 'TRANSFERRED_CO2_N2O':
        return TRANSFERRED_CO2Status(state);
      case 'TRANSFERRED_CO2_N2O_Pipeline':
        return TRANSFERRED_CO2_PipelineStatus(state);
      case 'uncertaintyAnalysis':
        return uncertaintyAnalysisStatus(state);
      case 'mmpInstallationDescription':
        return mmpInstallationDescriptionStatus(state);
      case 'MMP_SUB_INSTALLATION':
        return mmpSubInstallationStatus(state);
      case 'MMP_SUB_INSTALLATION_Product_Benchmark':
        return mmpSubInstallationProductBenchmarkStatus(state, index);
      case 'MMP_SUB_INSTALLATION_Fallback_Approach':
        return mmpSubInstallationFallbackApproachStatus(state, index);
      case 'mmpMethods':
        return mmpMethodsStatus(state);
      case 'mmpProcedures':
        return mmpProceduresStatus(state);
      case 'mmpEnergyFlows':
        return mmpEnergyFlowsStatus(state);
      default:
        return state.permitSectionsCompleted[key]?.[0] ? 'complete' : 'not started';
    }
  }
}
