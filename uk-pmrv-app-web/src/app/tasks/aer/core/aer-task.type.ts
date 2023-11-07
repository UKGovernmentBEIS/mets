import { Aer, AerSaveReviewGroupDecisionRequestTaskActionPayload, AerVerificationReport } from 'pmrv-api';

import { AerAmendGroupStatusKey } from './aer.amend.types';

export type MainTaskKey =
  | keyof Aer
  | 'CALCULATION_CO2'
  | 'CALCULATION_PFC'
  | 'MEASUREMENT_CO2'
  | 'MEASUREMENT_N2O'
  | 'INHERENT_CO2'
  | 'FALLBACK'
  | 'sendReport';
export type VerificationTaskKey = keyof AerVerificationReport;
export type StatusKey =
  | MainTaskKey
  | VerificationTaskKey
  | AerSaveReviewGroupDecisionRequestTaskActionPayload['group']
  | AerAmendGroupStatusKey;
