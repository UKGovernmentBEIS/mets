import { mmpStatuses } from '@permit-application/monitoring-methodology-plan/mmp-status';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { findAmendedStatusKeysByReviewGroups } from '../../amend/amend';
import { PermitApplicationState } from '../../store/permit-application.state';
import { amendRequestTaskTypes } from './permit';

export const getAvailableSections = (state: PermitApplicationState): string[] => {
  return [
    ...getTasks(),
    ...(state.permit?.monitoringMethodologyPlans?.exist && state?.features?.['digitized-mmp'] ? [...mmpStatuses] : []),
    ...Object.keys(state.permit?.monitoringApproaches ?? {}),
    ...(amendRequestTaskTypes.includes(state.requestTaskType)
      ? findAmendedStatusKeysByReviewGroups(
          Object.keys(
            state.reviewGroupDecisions,
          ) as PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'][],
        )
      : []),
  ];
};

const getTasks = (): string[] => {
  const tasks = [
    'abbreviations',
    'additionalDocuments',
    'confidentialityStatement',
    'emissionPoints',
    'emissionSources',
    'emissionSummaries',
    'environmentalPermitsAndLicences',
    'estimatedAnnualEmissions',
    'installationDescription',
    'measurementDevicesOrMethods',
    'monitoringApproaches',
    'monitoringMethodologyPlans',
    'regulatedActivities',
    'siteDiagrams',
    'sourceStreams',
    'monitoringApproachesPrepare',
    'uncertaintyAnalysis',
    'monitoringReporting',
    'assignmentOfResponsibilities',
    'monitoringPlanAppropriateness',
    'dataFlowActivities',
    'qaDataFlowActivities',
    'reviewAndValidationOfData',
    'assessAndControlRisk',
    'qaMeteringAndMeasuringEquipment',
    'correctionsAndCorrectiveActions',
    'controlOfOutsourcedActivities',
    'recordKeepingAndDocumentation',
    'environmentalManagementSystem',
  ];

  return tasks;
};
