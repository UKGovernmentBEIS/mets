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
    case 'EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED':
      return {
        showDecision: false,
        showVariationDecision: false,
        showVariationRegLedDecision: true,
        variationRegLedDecision: payload.reviewGroupDecisions[empReviewGroupMap[taskKey]],
      };

    case 'EMP_VARIATION_UKETS_APPLICATION_APPROVED':
    case 'EMP_VARIATION_CORSIA_APPLICATION_APPROVED':
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
  const prefix = isCorsia ? 'emp-corsia' : 'emp';

  return [
    [
      'EMP_VARIATION_UKETS_APPLICATION_SUBMITTED_PAYLOAD',
      'EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED_PAYLOAD',
      'EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD',
      'EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD',
      'EMP_VARIATION_UKETS_APPLICATION_APPROVED_PAYLOAD',
      'EMP_VARIATION_CORSIA_APPLICATION_APPROVED_PAYLOAD',
    ].includes(payload.payloadType)
      ? {
          title: 'Variation details',
          tasks: [
            {
              name: 'empVariationDetails',
              link: `${prefix}/submitted/variation-details`,
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
          link: `${prefix}/submitted/service-contact-details`,
        },
        {
          name: 'operatorDetails',
          linkText: empHeaderTaskMap['operatorDetails'],
          link: `${prefix}/submitted/operator-details`,
        },
      ],
    },
    {
      title: 'Emissions monitoring',
      tasks: [
        {
          name: 'flightAndAircraftProcedures',
          linkText: empHeaderTaskMap['flightAndAircraftProcedures'],
          link: `${prefix}/submitted/flight-procedures`,
        },
        {
          name: 'emissionsMonitoringApproach',
          linkText: empHeaderTaskMap['emissionsMonitoringApproach'],
          link: `${prefix}/submitted/monitoring-approach`,
        },
        ...(!isCorsia
          ? [
              {
                name: 'emissionsReductionClaim',
                linkText: empHeaderTaskMap['emissionsReductionClaim'],
                link: `${prefix}/submitted/emissions-reduction-claim`,
              },
            ]
          : []),
        {
          name: 'emissionSources',
          linkText: empHeaderTaskMap['emissionSources'],
          link: `${prefix}/submitted/emission-sources`,
        },
        payload.emissionsMonitoringPlan?.methodAProcedures
          ? {
              name: 'methodAProcedures',
              linkText: empHeaderTaskMap['methodAProcedures'],
              link: `${prefix}/submitted/method-a-procedures`,
            }
          : null,
        payload.emissionsMonitoringPlan?.methodBProcedures
          ? {
              name: 'methodBProcedures',
              linkText: empHeaderTaskMap['methodBProcedures'],
              link: `${prefix}/submitted/method-b-procedures`,
            }
          : null,
        payload.emissionsMonitoringPlan?.blockOnBlockOffMethodProcedures
          ? {
              name: 'blockOnBlockOffMethodProcedures',
              linkText: empHeaderTaskMap['blockOnBlockOffMethodProcedures'],
              link: `${prefix}/submitted/block-on-off-procedures`,
            }
          : null,
        payload.emissionsMonitoringPlan?.fuelUpliftMethodProcedures
          ? {
              name: 'fuelUpliftMethodProcedures',
              linkText: empHeaderTaskMap['fuelUpliftMethodProcedures'],
              link: `${prefix}/submitted/fuel-uplift-procedures`,
            }
          : null,
        payload.emissionsMonitoringPlan?.blockHourMethodProcedures
          ? {
              name: 'blockHourMethodProcedures',
              linkText: empHeaderTaskMap['blockHourMethodProcedures'],
              link: `${prefix}/submitted/block-hour-procedures`,
            }
          : null,
        (!isCorsia && isFUMM(payload)) || isCorsia
          ? {
              name: 'dataGaps',
              linkText: isCorsia ? 'Data Gaps' : empHeaderTaskMap['dataGaps'],
              link: `${prefix}/submitted/data-gaps`,
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
          link: `${prefix}/submitted/management-procedures`,
        },
      ],
    },
    {
      title: 'Additional information',
      tasks: [
        {
          name: 'abbreviations',
          linkText: empHeaderTaskMap['abbreviations'],
          link: `${prefix}/submitted/abbreviations`,
        },
        {
          name: 'additionalDocuments',
          linkText: empHeaderTaskMap['additionalDocuments'],
          link: `${prefix}/submitted/additional-docs`,
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
                link: `${prefix}/submitted/application-timeframe-apply`,
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
              link: `${prefix}/submitted/decision`,
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
