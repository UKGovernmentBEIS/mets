import { EmpRequestActionPayload } from '@aviation/request-action/store';
import { empHeaderTaskMap, empReviewGroupMap } from '@aviation/request-task/emp/shared/util/emp.util';
import { EmpTaskKey } from '@aviation/request-task/store';
import { isFUMM } from '@aviation/shared/components/emp/emission-sources/isFUMM';
import { TaskSection } from '@shared/task-list/task-list.interface';

import {
  EmpAcceptedVariationDecisionDetails,
  EmpIssuanceReviewDecision,
  EmpVariationReviewDecision,
  RequestActionDTO,
} from 'pmrv-api';

export interface EmpSubmittedViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  reviewDecision?: EmpIssuanceReviewDecision;
  reviewAttachments?: { [key: string]: string };
  downloadBaseUrl?: string;
  variationDecision?: EmpVariationReviewDecision;
  variationRegLedDecision?: EmpAcceptedVariationDecisionDetails;
  showDiff: boolean;
  originalData: any;
}

export function getEmpSubmittedViewModelData(
  requestActionType: RequestActionDTO['type'],
  payload: EmpRequestActionPayload,
  regulatorViewer: boolean,
  downloadBaseUrl: string,
  taskKey: EmpTaskKey,
): EmpSubmittedViewModel {
  return {
    requestActionType: requestActionType,
    pageHeader: empHeaderTaskMap[taskKey],
    showDiff: !!payload.originalEmpContainer,
    originalData: payload.originalEmpContainer?.emissionsMonitoringPlan[taskKey],
    ...getReviewDecisionsData(requestActionType, payload, regulatorViewer, downloadBaseUrl, taskKey),
  };
}

function getReviewDecisionsData(
  requestActionType: RequestActionDTO['type'],
  payload: EmpRequestActionPayload,
  regulatorViewer: boolean,
  downloadBaseUrl: string,
  taskKey: EmpTaskKey,
) {
  switch (requestActionType) {
    case 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED':
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED':
      return regulatorViewer
        ? {
            showDecision: true,
            showVariationDecision: false,
            showVariationRegLedDecision: false,
            reviewDecision: payload.reviewGroupDecisions[empReviewGroupMap[taskKey]],
            reviewAttachments: payload.reviewAttachments,
            downloadBaseUrl: downloadBaseUrl,
          }
        : {
            showDecision: false,
            showVariationDecision: false,
            showVariationRegLedDecision: false,
          };

    case 'EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED':
      return {
        showDecision: false,
        showVariationDecision: false,
        showVariationRegLedDecision: true,
        variationRegLedDecision: payload.reviewGroupDecisions[empReviewGroupMap[taskKey]],
      };

    case 'EMP_VARIATION_UKETS_APPLICATION_APPROVED':
      return regulatorViewer
        ? {
            showDecision: false,
            showVariationDecision: true,
            showVariationRegLedDecision: false,
            variationDecision: payload.reviewGroupDecisions[empReviewGroupMap[taskKey]],
            reviewAttachments: payload.reviewAttachments,
            downloadBaseUrl: downloadBaseUrl,
          }
        : {
            showDecision: false,
            showVariationDecision: false,
            showVariationRegLedDecision: false,
          };

    default:
      return {
        showDecision: false,
        showVariationDecision: false,
        showVariationRegLedDecision: false,
      };
  }
}

// TODO this method already exist here
// src\app\aviation\request-task\emp\shared\util\emp.util.ts
// please reuse
export function getEmpApplicationSubmittedTasks(
  payload: EmpRequestActionPayload,
  regulatorViewer: boolean,
  isCorsia: boolean,
): TaskSection<any>[] {
  return [
    [
      'EMP_VARIATION_UKETS_APPLICATION_SUBMITTED_PAYLOAD',
      'EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD',
      'EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD',
      'EMP_VARIATION_UKETS_APPLICATION_APPROVED_PAYLOAD',
    ].includes(payload.payloadType)
      ? {
          title: 'Variation details',
          tasks: [
            {
              name: 'empVariationDetails',
              link: `emp/submitted/variation-details`,
              linkText: empHeaderTaskMap['empVariationDetails'],
            },
          ],
        }
      : null,
    {
      title: 'Account details',
      tasks: [
        {
          name: 'serviceContactDetails',
          linkText: empHeaderTaskMap['serviceContactDetails'],
          link: `emp/submitted/service-contact-details`,
        },
        {
          name: 'operatorDetails',
          linkText: empHeaderTaskMap['operatorDetails'],
          link: `emp/submitted/operator-details`,
        },
      ],
    },
    {
      title: 'Emissions monitoring',
      tasks: [
        {
          name: 'flightAndAircraftProcedures',
          linkText: empHeaderTaskMap['flightAndAircraftProcedures'],
          link: `emp/submitted/flight-procedures`,
        },
        {
          name: 'emissionsMonitoringApproach',
          linkText: empHeaderTaskMap['emissionsMonitoringApproach'],
          link: `emp/submitted/monitoring-approach`,
        },
        ...(!isCorsia
          ? [
              {
                name: 'emissionsReductionClaim',
                linkText: empHeaderTaskMap['emissionsReductionClaim'],
                link: `emp/submitted/emissions-reduction-claim`,
              },
            ]
          : []),
        {
          name: 'emissionSources',
          linkText: empHeaderTaskMap['emissionSources'],
          link: `emp/submitted/emission-sources`,
        },
        payload.emissionsMonitoringPlan?.methodAProcedures
          ? {
              name: 'methodAProcedures',
              linkText: empHeaderTaskMap['methodAProcedures'],
              link: `emp/submitted/method-a-procedures`,
            }
          : null,
        payload.emissionsMonitoringPlan?.methodBProcedures
          ? {
              name: 'methodBProcedures',
              linkText: empHeaderTaskMap['methodBProcedures'],
              link: `emp/submitted/method-b-procedures`,
            }
          : null,
        payload.emissionsMonitoringPlan?.blockOnBlockOffMethodProcedures
          ? {
              name: 'blockOnBlockOffMethodProcedures',
              linkText: empHeaderTaskMap['blockOnBlockOffMethodProcedures'],
              link: `emp/submitted/block-on-off-procedures`,
            }
          : null,
        payload.emissionsMonitoringPlan?.fuelUpliftMethodProcedures
          ? {
              name: 'fuelUpliftMethodProcedures',
              linkText: empHeaderTaskMap['fuelUpliftMethodProcedures'],
              link: `emp/submitted/fuel-uplift-procedures`,
            }
          : null,
        payload.emissionsMonitoringPlan?.blockHourMethodProcedures
          ? {
              name: 'blockHourMethodProcedures',
              linkText: empHeaderTaskMap['blockHourMethodProcedures'],
              link: `emp/submitted/block-hour-procedures`,
            }
          : null,
        isFUMM(payload)
          ? {
              name: 'dataGaps',
              linkText: isCorsia ? 'Data Gaps' : empHeaderTaskMap['dataGaps'],
              link: `emp/submitted/data-gaps`,
            }
          : null,
      ],
    },
    {
      title: 'Management procedures',
      tasks: [
        {
          name: 'managementProcedures',
          linkText: empHeaderTaskMap['managementProcedures'],
          link: `emp/submitted/management-procedures`,
        },
      ],
    },
    {
      title: 'Additional information',
      tasks: [
        {
          name: 'abbreviations',
          linkText: empHeaderTaskMap['abbreviations'],
          link: `emp/submitted/abbreviations`,
        },
        {
          name: 'additionalDocuments',
          linkText: empHeaderTaskMap['additionalDocuments'],
          link: `emp/submitted/additional-docs`,
        },
      ],
    },
    ...(!isCorsia
      ? [
          {
            title: 'Application timeframe',
            tasks: [
              {
                name: 'applicationTimeframeInfo',
                linkText: empHeaderTaskMap['applicationTimeframeInfo'],
                link: `emp/submitted/application-timeframe-apply`,
              },
            ],
          },
        ]
      : []),

    ['EMP_ISSUANCE_UKETS_APPLICATION_APPROVED_PAYLOAD', 'EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED_PAYLOAD'].includes(
      payload.payloadType,
    ) && regulatorViewer
      ? {
          title: 'Decision',
          tasks: [
            {
              name: 'decision',
              link: `emp/submitted/decision`,
              linkText: empHeaderTaskMap['decision'],
            },
          ],
        }
      : null,
  ]
    .filter((section) => !!section)
    .map((section) => ({
      ...section,
      tasks: section.tasks.filter((task) => !!task),
    }));
}
