package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleReasonItemType {
    ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5(ArticleReasonGroupType.ARTICLE_6A_REASONS),
    SETTING_ALLOCATION_UNDER_ARTICLE_3A(ArticleReasonGroupType.ARTICLE_6A_REASONS),
    SETTING_HAL_AND_ALLOCATION_UNDER_ARTICLE_3A(ArticleReasonGroupType.ARTICLE_6A_REASONS),
    ADJUSTMENT_OF_PARAMETERS_OTHER_THAN_ACTIVITY_LEVEL(ArticleReasonGroupType.ARTICLE_6A_REASONS),

    ERROR_IN_BASELINE_DATA_REPORT(ArticleReasonGroupType.ARTICLE_34H_REASONS),
    ERROR_IN_NEW_ENTRANT_DATA_REPORT(ArticleReasonGroupType.ARTICLE_34H_REASONS),
    ERROR_IN_ACTIVITY_LEVEL_REPORT(ArticleReasonGroupType.ARTICLE_34H_REASONS),
    ERROR_MADE_BY_REGULATOR_OR_AUTHORITY(ArticleReasonGroupType.ARTICLE_34H_REASONS);

    private final ArticleReasonGroupType groupType;
}
