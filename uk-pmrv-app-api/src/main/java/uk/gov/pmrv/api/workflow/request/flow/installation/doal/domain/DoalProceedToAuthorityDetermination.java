package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.DoalDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonGroupType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonItemType;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#hasWithholdingOfAllowances) == (#withholdingAllowancesNotice != null)}", message = "doal.determination.hasWithholdingOfAllowances")
@SpELExpression(
        expression = "{(T(uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonGroupType).valueOf(#articleReasonGroupType) == T(uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonGroupType).ARTICLE_5_REASONS) == (#articleReasonItems == null || #articleReasonItems.isEmpty())}",
message = "doal.determination.articleReasonItems"
        )
public class DoalProceedToAuthorityDetermination extends DoalDetermination {

    @NotNull
    private ArticleReasonGroupType articleReasonGroupType;

    @Builder.Default
    private Set<ArticleReasonItemType> articleReasonItems = new HashSet<>();

    @NotNull
    private Boolean hasWithholdingOfAllowances;

    @Valid
    private WithholdingAllowancesNotice withholdingAllowancesNotice;

    @NotNull
    private Boolean needsOfficialNotice;
}
