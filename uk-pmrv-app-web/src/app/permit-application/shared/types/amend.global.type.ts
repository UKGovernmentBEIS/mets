import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { AboutVariationAmendGroup, AboutVariationGroupKey } from '../../../permit-variation/variation-types';
import { PermitAmendGroup, permitAmendGroupHeading } from './amend.permit.type';

export type AmendGroup = PermitAmendGroup | AboutVariationAmendGroup;

export interface GroupKeyAndAmendDecision {
  groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'] | AboutVariationGroupKey;
  data: any; //PermitIssuanceReviewDecision | PermitVariationReviewDecision
}

export const amendGroupAllHeading: Record<PermitAmendGroup, string> | Record<AboutVariationAmendGroup, string> = {
  ...permitAmendGroupHeading,
  ...({ 'about-variation': 'about the variation' } as Record<AboutVariationAmendGroup, string>),
};
