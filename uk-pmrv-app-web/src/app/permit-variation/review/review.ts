import { PermitVariationRegulatorLedGrantDetermination, PermitVariationReviewDecision } from 'pmrv-api';

import { reviewGroupHeading } from '../../permit-application/review/utils/review.permit';
import { PermitVariationState } from '../store/permit-variation.state';

export function getVariationScheduleItems(state: PermitVariationState) {
  const aboutVariationScheduleItems: string[] =
    (state.permitVariationDetailsReviewDecision as any)?.details?.variationScheduleItems ||
    (state.permitVariationDetailsReviewDecision as any)?.variationScheduleItems ||
    [];

  const applicableReviewGroups = Object.keys(reviewGroupHeading).filter((reviewGroup) =>
    Object.keys(state.reviewGroupDecisions).includes(reviewGroup),
  );
  const reviewGroupVariationScheduleItems = applicableReviewGroups.reduce(
    (result, reviewGroup) => [
      ...result,
      ...((state.reviewGroupDecisions[reviewGroup]?.details?.variationScheduleItems ||
        state.reviewGroupDecisions[reviewGroup]?.variationScheduleItems) ??
        []),
    ],
    [],
  ) as string[];

  return [...aboutVariationScheduleItems, ...reviewGroupVariationScheduleItems];
}

export function isVariationDetailsReviewDecisionOfType(
  state: PermitVariationState,
  type: PermitVariationReviewDecision['type'],
): boolean {
  return (state.permitVariationDetailsReviewDecision as PermitVariationReviewDecision)?.type === type;
}

export function isVariationReasonTemplateCompleted(determination: PermitVariationRegulatorLedGrantDetermination) {
  return (
    !!determination?.reasonTemplate &&
    (determination.reasonTemplate !== 'OTHER' || !!determination.reasonTemplateOtherSummary)
  );
}
