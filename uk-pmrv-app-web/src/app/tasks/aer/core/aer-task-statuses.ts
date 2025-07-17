import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { MainTaskKey, StatusKey } from '@tasks/aer/core/aer-task.type';
import { fallbackStatus } from '@tasks/aer/submit/fallback/fallback-status';
import { inherentCo2Status } from '@tasks/aer/submit/inherent-co2/inherent-co2-status';

import {
  AerApplicationAmendsSubmitRequestTaskPayload,
  AerApplicationReviewRequestTaskPayload,
  AerApplicationSubmitRequestTaskPayload,
  AerApplicationVerificationSubmitRequestTaskPayload,
  AerSaveReviewGroupDecisionRequestTaskActionPayload,
  AerVerificationReport,
} from 'pmrv-api';

import { getSourceStreamsStatus } from '../shared/components/submit/emissions-status';
import { getMeasurementStatus } from '../submit/measurement/measurement-status';
import { sendReportStatus } from '../submit/send-report/send-report-status';
import { AerAmendTotalEmissionsGroup, amendTasksPerReviewGroup } from './aer.amend.types';

export function getAvailableSections(
  payload: AerApplicationSubmitRequestTaskPayload | AerApplicationAmendsSubmitRequestTaskPayload,
): Array<MainTaskKey> {
  const sections: Array<MainTaskKey> = [
    'aerMonitoringPlanDeviation',
    'abbreviations',
    'additionalDocuments',
    'confidentialityStatement',
    'sourceStreams',
    'emissionSources',
    'monitoringApproachEmissions',
    'emissionPoints',
    'regulatedActivities',
    'pollutantRegisterActivities',
    'naceCodes',
    'CALCULATION_CO2',
    'MEASUREMENT_CO2',
    'MEASUREMENT_N2O',
    'INHERENT_CO2',
    'FALLBACK',
  ];
  if (payload?.permitOriginatedData?.permitType === 'GHGE') {
    sections.push('activityLevelReport');
  }

  if (payload.payloadType === 'AER_APPLICATION_AMENDS_SUBMIT_PAYLOAD') {
    const reviewGroupDecisionsKeys = Object.keys(
      (payload as AerApplicationAmendsSubmitRequestTaskPayload)?.reviewGroupDecisions ?? [],
    );

    reviewGroupDecisionsKeys.forEach((key) => {
      Object.entries(amendTasksPerReviewGroup).forEach(([reviewGroup, tasks]) => {
        if (tasks.includes(key as AerAmendTotalEmissionsGroup)) {
          const amendSection = `AMEND_${reviewGroup}`;
          if (!sections.includes(amendSection as MainTaskKey)) {
            sections.push(`AMEND_${reviewGroup}` as MainTaskKey);
          }
        }
      });
    });
  }
  return sections;
}

export function getSectionStatus(key: StatusKey, payload: AerApplicationSubmitRequestTaskPayload): TaskItemStatus {
  switch (key) {
    case 'aerMonitoringPlanDeviation':
    case 'abbreviations':
    case 'additionalDocuments':
    case 'confidentialityStatement':
    case 'activityLevelReport':
      return payload.aerSectionsCompleted[key]?.[0] ? 'complete' : payload.aer[key] ? 'in progress' : 'not started';
    case 'sourceStreams':
    case 'emissionSources':
    case 'monitoringApproachEmissions':
    case 'emissionPoints':
    case 'regulatedActivities':
      return payload.aerSectionsCompleted[key]?.[0]
        ? 'complete'
        : (payload.aer[key]?.length ?? Object.keys(payload.aer[key] || {})?.length)
          ? 'in progress'
          : 'not started';
    case 'pollutantRegisterActivities':
      return payload.aerSectionsCompleted[key]?.[0]
        ? 'complete'
        : payload.aer[key]?.exist === false || payload.aer[key]?.activities?.length > 0
          ? 'in progress'
          : 'not started';
    case 'naceCodes':
      return payload.aerSectionsCompleted[key]?.[0]
        ? 'complete'
        : payload.aer[key]?.codes?.length > 0
          ? 'in progress'
          : 'not started';
    case 'CALCULATION_CO2':
    case 'CALCULATION_PFC':
      return getSourceStreamsStatus(key, payload);
    case 'MEASUREMENT_CO2':
    case 'MEASUREMENT_N2O':
      return getMeasurementStatus(key, payload);
    case 'INHERENT_CO2':
      return inherentCo2Status(payload);
    case 'FALLBACK': {
      return fallbackStatus(payload);
    }

    case 'sendReport':
      return sendReportStatus(payload);

    default:
      return payload?.aerSectionsCompleted[key]?.[0] ? 'complete' : 'not started';
  }
}

export function getVerificationSectionStatus(
  key: StatusKey,
  payload: AerApplicationVerificationSubmitRequestTaskPayload,
): TaskItemStatus {
  if (key === 'sendReport') {
    return verificationSubmitSendReportStatus(payload as AerApplicationVerificationSubmitRequestTaskPayload);
  }
  return payload?.verificationSectionsCompleted[key]?.[0]
    ? 'complete'
    : payload?.verificationSectionsCompleted[key]?.[0] === false
      ? 'in progress'
      : 'not started';
}

export function verificationSubmitSendReportStatus(
  payload: AerApplicationVerificationSubmitRequestTaskPayload,
): TaskItemStatus {
  return getAvailableVerificationSubmitSections(payload).every(
    (key) => payload?.verificationSectionsCompleted[key]?.[0],
  )
    ? 'not started'
    : 'cannot start yet';
}

function getAvailableVerificationSubmitSections(
  payload: AerApplicationVerificationSubmitRequestTaskPayload,
): Array<keyof AerVerificationReport> {
  const sections: Array<keyof AerVerificationReport> = [
    'verificationTeamDetails',
    'opinionStatement',
    'etsComplianceRules',
    'complianceMonitoringReporting',
    'overallAssessment',
    'uncorrectedMisstatements',
    'uncorrectedNonConformities',
    'uncorrectedNonCompliances',
    'recommendedImprovements',
    'methodologiesToCloseDataGaps',
    'materialityLevel',
    'summaryOfConditions',
  ];
  if (payload?.permitOriginatedData?.permitType === 'GHGE') {
    sections.push('activityLevelReport');
  }

  return sections;
}

export function getReviewSectionStatus(
  key: StatusKey,
  payload: any,
): TaskItemStatus | 'accepted' | 'operator to amend' {
  return payload?.reviewSectionsCompleted?.[key]
    ? payload.reviewGroupDecisions?.[key]?.type === 'ACCEPTED'
      ? 'accepted'
      : 'operator to amend'
    : 'undecided';
}

export function isEverySectionAccepted(payload: AerApplicationReviewRequestTaskPayload): boolean {
  const aerDataStatuses = getAerDataSections(payload);
  const verificationReportDataStatuses = payload?.verificationReport ? getVerificationReportDataSections() : [];

  return [...aerDataStatuses, ...verificationReportDataStatuses].every(
    (key) => getReviewSectionStatus(key, payload) === 'accepted',
  );
}
export function atLeastOneSectionForAmendsExists(payload: AerApplicationReviewRequestTaskPayload): boolean {
  const aerDataSections = getAerDataSections(payload);

  return aerDataSections.some((key) => getReviewSectionStatus(key, payload) === 'operator to amend');
}

function getAerDataSections(
  payload: AerApplicationReviewRequestTaskPayload,
): Array<AerSaveReviewGroupDecisionRequestTaskActionPayload['group']> {
  const sections: AerSaveReviewGroupDecisionRequestTaskActionPayload['group'][] = [
    'INSTALLATION_DETAILS',
    'FUELS_AND_EQUIPMENT',
    'ADDITIONAL_INFORMATION',
    'EMISSIONS_SUMMARY',
    ...(Object.keys(
      payload.aer.monitoringApproachEmissions,
    ) as AerSaveReviewGroupDecisionRequestTaskActionPayload['group'][]),
  ];

  if (payload?.permitOriginatedData?.permitType === 'GHGE') {
    sections.push('ACTIVITY_LEVEL_REPORT');
  }

  return sections;
}

function getVerificationReportDataSections(): Array<AerSaveReviewGroupDecisionRequestTaskActionPayload['group']> {
  return [
    'VERIFIER_DETAILS',
    'OPINION_STATEMENT',
    'ETS_COMPLIANCE_RULES',
    'COMPLIANCE_MONITORING_REPORTING',
    'OVERALL_DECISION',
    'UNCORRECTED_MISSTATEMENTS',
    'UNCORRECTED_NON_CONFORMITIES',
    'UNCORRECTED_NON_COMPLIANCES',
    'RECOMMENDED_IMPROVEMENTS',
    'CLOSE_DATA_GAPS_METHODOLOGIES',
    'MATERIALITY_LEVEL',
    'SUMMARY_OF_CONDITIONS',
  ];
}
