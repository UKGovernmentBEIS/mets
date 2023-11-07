import { DoalProceedToAuthorityDetermination } from 'pmrv-api';

export const articleReasonGroupTypeLabelsMap: Record<
  DoalProceedToAuthorityDetermination['articleReasonGroupType'],
  string
> = {
  ARTICLE_6A_REASONS: 'Article 6a reasons',
  ARTICLE_34H_REASONS: 'Article 34H reasons',
};

export const articleReasonItemsLabelsMap: Record<
  DoalProceedToAuthorityDetermination['articleReasonItems'][number],
  string
> = {
  ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5:
    'Article 6a of the Activity Level Changes Regulation (allocation adjustment under Article 5)',
  SETTING_ALLOCATION_UNDER_ARTICLE_3A:
    'Article 6a of the Activity Level Changes Regulation (setting allocation under Article 3a - for year in which start of normal operation occurred only of new sub-installation)',
  SETTING_HAL_AND_ALLOCATION_UNDER_ARTICLE_3A:
    'Article 6a of the Activity Level Changes Regulation (setting HAL and allocation under Article 3a - after first full calendar year operation of new sub-installation)',
  ADJUSTMENT_OF_PARAMETERS_OTHER_THAN_ACTIVITY_LEVEL:
    'Article 6a of the Activity Level Changes Regulation (adjustment of parameters other than activity level)',

  ERROR_IN_BASELINE_DATA_REPORT: 'Article 34H of the Order - error in baseline data report',
  ERROR_IN_NEW_ENTRANT_DATA_REPORT: 'Article 34H of the Order - error in new entrant data report',
  ERROR_IN_ACTIVITY_LEVEL_REPORT: 'Article 34H of the Order - error in activity level report',
  ERROR_MADE_BY_REGULATOR_OR_AUTHORITY: 'Article 34H of the Order - error made by regulator or authority',
};
