import {
  DecisionNotification,
  PermitIssuanceApplicationReviewRequestTaskPayload,
  RequestActionDTO,
  RequestActionUserInfo,
  RequestInfoDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
} from 'pmrv-api';

export interface PermitApplicationState extends PermitIssuanceApplicationReviewRequestTaskPayload {
  requestTaskId?: number;
  actionId?: number;
  requestTaskType: RequestTaskDTO['type'];
  requestActionType: RequestActionDTO['type'];
  requestId: string;
  requestType: RequestInfoDTO['type'];
  allowedRequestTaskActions?: RequestTaskItemDTO['allowedRequestTaskActions'];
  isRequestTask: boolean; //true if request task, false if request action
  daysRemaining?: number;
  requestActionCreationDate?: string;
  requestActionSubmitter?: string;

  competentAuthority: RequestInfoDTO['competentAuthority'];
  accountId: RequestInfoDTO['accountId'];
  paymentCompleted?: boolean;

  isEditable: boolean;

  assignee: Pick<RequestTaskDTO, 'assigneeUserId' | 'assigneeFullName'>;
  userAssignCapable: boolean;
  assignable?: boolean;

  // permit determination/decision
  reviewGroupDecisions?: {
    [key: string]: any; // PermitIssuanceReviewDecision | PermitVariationReviewDecision | PermitAcceptedVariationDecisionDetails
  };
  determination?: any; // PermitIssuanceGrantDetermination | PermitIssuanceRejectDetermination | PermitIssuanceDeemedWithdrawnDetermination | PermitVariationRegulatorLedGrantDetermination
  decisionNotification?: DecisionNotification;
  usersInfo?: { [key: string]: RequestActionUserInfo };

  userViewRole?: 'OPERATOR' | 'REGULATOR' | 'VERIFIER'; //TODO remove me from here, not part of permit application state

  returnUrl?: string;
}

export const initialState: PermitApplicationState = {
  installationOperatorDetails: undefined,
  assignee: undefined,
  competentAuthority: undefined,
  accountId: undefined,
  daysRemaining: undefined,
  permit: {
    abbreviations: undefined,
    additionalDocuments: undefined,
    assessAndControlRisk: undefined,
    assignmentOfResponsibilities: undefined,
    confidentialityStatement: undefined,
    controlOfOutsourcedActivities: undefined,
    correctionsAndCorrectiveActions: undefined,
    dataFlowActivities: undefined,
    emissionPoints: [],
    emissionSources: [],
    emissionSummaries: [],
    environmentalManagementSystem: undefined,
    environmentalPermitsAndLicences: undefined,
    estimatedAnnualEmissions: undefined,
    installationDescription: undefined,
    measurementDevicesOrMethods: [],
    monitoringApproaches: undefined,
    monitoringMethodologyPlans: undefined,
    monitoringPlanAppropriateness: undefined,
    monitoringReporting: undefined,
    qaDataFlowActivities: undefined,
    qaMeteringAndMeasuringEquipment: undefined,
    recordKeepingAndDocumentation: undefined,
    regulatedActivities: [],
    reviewAndValidationOfData: undefined,
    siteDiagrams: undefined,
    sourceStreams: [],
    uncertaintyAnalysis: undefined,
  },
  permitSectionsCompleted: {},
  reviewSectionsCompleted: {},
  reviewGroupDecisions: {},
  permitAttachments: {},
  permitType: undefined,
  userAssignCapable: undefined,
  isEditable: undefined,
  requestTaskType: undefined,
  requestActionType: undefined,
  requestId: undefined,
  requestType: undefined,
  isRequestTask: undefined,
  returnUrl: undefined,
};
