import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import {
  PERMIT_AMEND_STATUS_PREFIX,
  PermitAmendGroup,
  permitAmendGroupReviewGroupsMap,
  PermitAmendGroupStatusKey,
} from '../shared/types/amend.permit.type';

export function constructAmendTaskStatusKey(amendGroup: PermitAmendGroup): PermitAmendGroupStatusKey {
  return PERMIT_AMEND_STATUS_PREFIX.concat(amendGroup) as PermitAmendGroupStatusKey;
}

export function findAmendedGroupsByReviewGroups(
  reviewGroups: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'][],
): PermitAmendGroup[] {
  const amendGroups: PermitAmendGroup[] = [];
  (Object.keys(permitAmendGroupReviewGroupsMap) as PermitAmendGroup[]).forEach((amendGroup) => {
    if (
      permitAmendGroupReviewGroupsMap[amendGroup].some(
        (group: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']) => reviewGroups.includes(group),
      )
    ) {
      amendGroups.push(amendGroup);
    }
  });
  return amendGroups;
}

export function findAmendedStatusKeysByReviewGroups(
  reviewGroups: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'][],
): PermitAmendGroupStatusKey[] {
  return findAmendedGroupsByReviewGroups(reviewGroups).map((amendGroup) => constructAmendTaskStatusKey(amendGroup));
}
