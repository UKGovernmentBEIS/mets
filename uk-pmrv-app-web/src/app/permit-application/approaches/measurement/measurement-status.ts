import { MeasurementOfCO2MonitoringApproach } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { StatusKey } from '../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../store/permit-application.state';

export function MEASUREMENTStatus(state: PermitApplicationState): TaskItemStatus {
  const tiers = (state.permit.monitoringApproaches?.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
    ?.emissionPointCategoryAppliedTiers;

  const tiersStatuses =
    tiers?.length > 0 ? tiers.map((_, index) => MEASUREMENTCategoryTierStatus(state, index)) : ['not started'];

  return measurementStaticStatuses.every((status) => state.permitSectionsCompleted[status]?.[0]) &&
    tiersStatuses.every((status) => status === 'complete')
    ? 'complete'
    : tiersStatuses.some((status) => status === 'needs review')
      ? 'needs review'
      : measurementStaticStatuses.some((status) => state.permitSectionsCompleted[status]?.[0]) ||
          tiersStatuses.some((status) => status === 'in progress' || status === 'complete')
        ? 'in progress'
        : 'not started';
}

function isCategoryTierBiomassFractionExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
    ?.emissionPointCategoryAppliedTiers?.[index]?.biomassFraction;
}

/** Returns the status of emission point category applier tier */
export function MEASUREMENTCategoryTierStatus(state: PermitApplicationState, index: number): TaskItemStatus {
  let measurementCategoryTierStatusesForWaste: readonly MeasurementCategoryTierStatus[] =
    measurementCategoryTierStatuses;

  // Check if the waste permit feature is enabled
  // and add the biomass fraction status if it is
  if (state.features?.wastePermitEnabled) {
    measurementCategoryTierStatusesForWaste = [
      ...measurementCategoryTierStatuses,
      ...measurementCategoryTierStatusesBiomassFraction,
    ];
  }

  return isMeasurementCategoryValid(state, index) &&
    MEASUREMENTCategoryTierSubtaskStatus(state, 'MEASUREMENT_CO2_Measured_Emissions', index) !== 'needs review'
    ? measurementCategoryTierStatusesForWaste.every((status) => state.permitSectionsCompleted[status]?.[index])
      ? 'complete'
      : measurementCategoryTierStatusesForWaste.some((status) => state.permitSectionsCompleted[status]?.[index])
        ? 'in progress'
        : 'not started'
    : 'needs review';
}

/** Returns the status of emission point category applied tier subtask */
export function MEASUREMENTCategoryTierSubtaskStatus(
  state: PermitApplicationState,
  key: StatusKey,
  index: number,
): TaskItemStatus {
  switch (key) {
    case 'MEASUREMENT_CO2_Category':
      return isSourceStreamCategoryExist(state, index)
        ? state.permitSectionsCompleted[key]?.[index]
          ? isMeasurementCategoryValid(state, index)
            ? 'complete'
            : 'needs review'
          : 'in progress'
        : 'not started';
    case 'MEASUREMENT_CO2_Measured_Emissions': {
      return !doMeasMeasuredEmissionsExist(state, index)
        ? !areMeasMeasuredEmissionsPrerequisitesMet(state, index)
          ? 'cannot start yet'
          : 'not started'
        : !areMeasMeasuredEmissionsDevicesValid(state, index)
          ? 'needs review'
          : !state.permitSectionsCompleted[key]?.[index]
            ? 'in progress'
            : 'complete';
    }
    case 'MEASUREMENT_CO2_Applied_Standard':
      return state.permitSectionsCompleted[key]?.[index]
        ? 'complete'
        : MEASUREMENTCategoryTierSubtaskStatus(state, 'MEASUREMENT_CO2_Category', index) === 'complete'
          ? 'not started'
          : 'cannot start yet';

    case 'MEASUREMENT_CO2_Biomass_Fraction':
      return MEASUREMENTCategoryTierSubtaskStatus(state, 'MEASUREMENT_CO2_Category', index) === 'not started'
        ? 'cannot start yet'
        : isCategoryTierBiomassFractionExist(state, index)
          ? state.permitSectionsCompleted[key]?.[index]
            ? 'complete'
            : 'in progress'
          : 'not started';
    default:
      return state.permitSectionsCompleted[key]?.[index] ? 'complete' : 'not started';
  }
}

export function areCategoryTierPrerequisitesMet(state: PermitApplicationState): boolean {
  return (
    state.permitSectionsCompleted.sourceStreams?.[0] &&
    state.permitSectionsCompleted.emissionPoints?.[0] &&
    state.permitSectionsCompleted.emissionSources?.[0]
  );
}

function isSourceStreamCategoryExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
    .emissionPointCategoryAppliedTiers?.[index]?.emissionPointCategory;
}

function doMeasMeasuredEmissionsExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
    .emissionPointCategoryAppliedTiers?.[index]?.measuredEmissions;
}

export function areMeasMeasuredEmissionsPrerequisitesMet(state: PermitApplicationState, index: number): boolean {
  return (
    (!!(state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
      .emissionPointCategoryAppliedTiers?.[index]?.measuredEmissions &&
      !!state.permit?.measurementDevicesOrMethods) ||
    (!!(state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
      .emissionPointCategoryAppliedTiers?.[index]?.emissionPointCategory &&
      state.permitSectionsCompleted.measurementDevicesOrMethods?.[0])
  );
}

export function areMeasMeasuredEmissionsDevicesValid(state: PermitApplicationState, index: number): boolean {
  const measuredEmissions = (state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
    .emissionPointCategoryAppliedTiers?.[index]?.measuredEmissions;

  return measuredEmissions?.measurementDevicesOrMethods.every((id) =>
    state.permit.measurementDevicesOrMethods.map((item) => item.id).includes(id),
  );
}

export const measurementCategoryTierStatuses = [
  'MEASUREMENT_CO2_Category',
  'MEASUREMENT_CO2_Measured_Emissions',
  'MEASUREMENT_CO2_Applied_Standard',
] as const;

export const measurementCategoryTierStatusesBiomassFraction = ['MEASUREMENT_CO2_Biomass_Fraction'] as const;

export const measurementStaticStatuses = [
  'MEASUREMENT_CO2_Description',
  'MEASUREMENT_CO2_Emission',
  'MEASUREMENT_CO2_Reference',
  'MEASUREMENT_CO2_Gasflow',
  'MEASUREMENT_CO2_Biomass',
  'MEASUREMENT_CO2_Corroborating',
] as const;

export type MeasurementStatuses =
  | 'MEASUREMENT_CO2_Category_Tier'
  | (typeof measurementStaticStatuses)[number]
  | (typeof measurementCategoryTierStatuses)[number]
  | (typeof measurementCategoryTierStatusesBiomassFraction)[number];

type MeasurementCategoryTierStatus =
  | (typeof measurementCategoryTierStatuses)[number]
  | (typeof measurementCategoryTierStatusesBiomassFraction)[number];

/** Returns true if reference state is valid and all ids used in stream category exist */
function isMeasurementCategoryValid(state: PermitApplicationState, index: number): boolean {
  return (
    !!state.permit.emissionPoints?.find(
      (stream) =>
        stream.id ===
        (state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
          .emissionPointCategoryAppliedTiers[index]?.emissionPointCategory?.emissionPoint,
    ) &&
    (
      state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
    ).emissionPointCategoryAppliedTiers[index]?.emissionPointCategory?.sourceStreams?.every((id) =>
      state.permit.sourceStreams?.map((source) => source.id).includes(id),
    ) &&
    (
      state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
    ).emissionPointCategoryAppliedTiers[index]?.emissionPointCategory?.emissionSources?.every((id) =>
      state.permit.emissionSources?.map((source) => source.id).includes(id),
    )
  );
}
