import { CalculationOfPFCMonitoringApproach } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { StatusKey } from '../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../store/permit-application.state';

/** Returns the status of the whole PFC monitoring approach task */
export function status(state: PermitApplicationState): TaskItemStatus {
  const tiers = (state.permit.monitoringApproaches?.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers;

  const tiersStatuses = tiers?.length > 0 ? tiers.map((_, index) => categoryTierStatus(state, index)) : ['not started'];

  return staticStatuses.every((status) => state.permitSectionsCompleted[status]?.[0]) &&
    tiersStatuses.every((status) => status === 'complete')
    ? 'complete'
    : tiersStatuses.some((status) => status === 'needs review')
      ? 'needs review'
      : staticStatuses.some((status) => state.permitSectionsCompleted[status]?.[0]) ||
          tiersStatuses.some((status) => status === 'in progress' || status === 'complete') ||
          PFCTier2EmissionFactorSubtaskStatus(state) === 'in progress'
        ? 'in progress'
        : 'not started';
}

export function PFCTier2EmissionFactorSubtaskStatus(state: PermitApplicationState) {
  return state.permitSectionsCompleted.CALCULATION_PFC_Tier2EmissionFactor?.[0]
    ? 'complete'
    : (state.permit.monitoringApproaches?.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)?.tier2EmissionFactor
      ? 'in progress'
      : 'not started';
}

/** Returns the status of source stream category applier tier with given index */
export function categoryTierStatus(state: PermitApplicationState, index: number): TaskItemStatus {
  return isCategoryValid(state, index)
    ? categoryTierStatuses.every((status) => state.permitSectionsCompleted[status]?.[index])
      ? 'complete'
      : categoryTierStatuses.some((status) => state.permitSectionsCompleted[status]?.[index])
        ? 'in progress'
        : 'not started'
    : 'needs review';
}

/** Returns the status of the given source stream category applied tier subtask */
export function categoryTierSubtaskStatus(
  state: PermitApplicationState,
  key: StatusKey,
  index: number,
): TaskItemStatus {
  switch (key) {
    case 'CALCULATION_PFC_Category':
      return state.permitSectionsCompleted[key]?.[index]
        ? isCategoryValid(state, index)
          ? 'complete'
          : 'needs review'
        : 'not started';
    case 'CALCULATION_PFC_Activity_Data':
      return categoryTierSubtaskStatus(state, 'CALCULATION_PFC_Category', index) === 'not started'
        ? 'cannot start yet'
        : activityDataExist(state, index)
          ? state.permitSectionsCompleted[key]?.[index]
            ? 'complete'
            : 'in progress'
          : 'not started';
    case 'CALCULATION_PFC_Emission_Factor':
      return categoryTierSubtaskStatus(state, 'CALCULATION_PFC_Category', index) === 'not started'
        ? 'cannot start yet'
        : isCategoryTierEmissionFactorExist(state, index)
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

/** Returns true if reference state is valid and all ids used in stream category exist */
function isCategoryValid(state: PermitApplicationState, index: number): boolean {
  return (
    !!state.permit.sourceStreams?.find(
      (stream) =>
        stream.id ===
        (state.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)
          .sourceStreamCategoryAppliedTiers[index]?.sourceStreamCategory?.sourceStream,
    ) &&
    (
      state.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach
    ).sourceStreamCategoryAppliedTiers[index]?.sourceStreamCategory?.emissionSources?.every((id) =>
      state.permit.emissionSources?.map((source) => source.id).includes(id),
    ) &&
    (
      state.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach
    ).sourceStreamCategoryAppliedTiers[index]?.sourceStreamCategory?.emissionPoints?.every((id) =>
      state.permit.emissionPoints?.map((point) => point.id).includes(id),
    )
  );
}

function activityDataExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers?.[index]?.activityData;
}

function isCategoryTierEmissionFactorExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers?.[index]?.emissionFactor;
}

export const categoryTierStatuses = [
  'CALCULATION_PFC_Category',
  'CALCULATION_PFC_Activity_Data',
  'CALCULATION_PFC_Emission_Factor',
] as const;

export const staticStatuses = [
  'CALCULATION_PFC_Description',
  'CALCULATION_PFC_Types',
  'CALCULATION_PFC_Efficiency',
  'CALCULATION_PFC_Tier2EmissionFactor',
] as const;

export type statuses =
  | 'CALCULATION_PFC_Category_Tier'
  | (typeof staticStatuses)[number]
  | (typeof categoryTierStatuses)[number];
