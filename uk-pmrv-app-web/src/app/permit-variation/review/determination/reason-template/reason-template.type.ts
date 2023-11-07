import { PermitVariationRegulatorLedGrantDetermination } from 'pmrv-api';

export const reasonTemplatesMap: Partial<
  Record<PermitVariationRegulatorLedGrantDetermination['reasonTemplate'], string>
> = {
  WHERE_OPERATOR_FAILED_TO_APPLY_IN_ACCORDANCE_WITH_CONDITIONS:
    'Where the operator failed to apply in accordance with conditions',
  FOLLOWING_IMPROVEMENT_REPORT_BY_OPERATOR: 'Following an improvement report submitted by an operator',
  HSE_SITUATIONS_RESPONSE: 'In response to various HSE situations. For example, a change to emissions target',
  PERMIT_FORMAL_REVIEW_RESPONSE: 'In response to a formal review of the permit',
  VARIATION_POWERS_FREE_ALLOCATION_PROVISIONS:
    'In relation to variation powers as part of the free allocation provisions',
};
