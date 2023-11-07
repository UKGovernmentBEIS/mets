import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { AboutVariationGroupKey } from '../../../permit-variation/variation-types';
import { reviewGroupHeading } from './review.permit';

export const reviewGroupAllHeading:
  | Record<PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'], string>
  | Record<AboutVariationGroupKey, string> = {
  ...reviewGroupHeading,
  ...({ ABOUT_VARIATION: 'About variation' } as Record<AboutVariationGroupKey, string>),
};
