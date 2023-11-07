import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

export const PERMIT_AMEND_STATUS_PREFIX = 'AMEND_';

export type PermitAmendGroup =
  | 'permit-type'
  | 'details'
  | 'fuels'
  | 'monitoring-approaches'
  | 'management-procedures'
  | 'monitoring-methodology-plan'
  | 'additional-info'
  | 'confidentiality';

export type PermitAmendGroupStatusKey = `${typeof PERMIT_AMEND_STATUS_PREFIX}${PermitAmendGroup}`;

export const permitAmendGroupReviewGroupsMap: Record<
  PermitAmendGroup,
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'][]
> = {
  'permit-type': ['PERMIT_TYPE'],
  details: ['INSTALLATION_DETAILS'],
  fuels: ['FUELS_AND_EQUIPMENT'],
  'monitoring-approaches': [
    'DEFINE_MONITORING_APPROACHES',
    'CALCULATION_CO2',
    'MEASUREMENT_CO2',
    'FALLBACK',
    'MEASUREMENT_N2O',
    'CALCULATION_PFC',
    'INHERENT_CO2',
    'TRANSFERRED_CO2_N2O',
    'UNCERTAINTY_ANALYSIS',
  ],
  'management-procedures': ['MANAGEMENT_PROCEDURES'],
  'monitoring-methodology-plan': ['MONITORING_METHODOLOGY_PLAN'],
  'additional-info': ['ADDITIONAL_INFORMATION'],
  confidentiality: ['CONFIDENTIALITY_STATEMENT'],
};

export const permitAmendGroupHeading: Record<PermitAmendGroup, string> = {
  'permit-type': 'permit type',
  details: 'installation details',
  fuels: 'fuels and equipment inventory',
  'monitoring-approaches': 'monitoring approaches',
  'management-procedures': 'management procedures',
  'monitoring-methodology-plan': 'monitoring methodology plan',
  'additional-info': 'additional information',
  confidentiality: 'confidentiality statement',
};
