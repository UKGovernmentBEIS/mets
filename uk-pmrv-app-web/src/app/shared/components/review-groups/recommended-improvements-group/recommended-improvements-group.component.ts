import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { RecommendedImprovements } from 'pmrv-api';

@Component({
  selector: 'app-recommended-improvements-group',
  templateUrl: './recommended-improvements-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsGroupComponent {
  @Input() isEditable = false;
  @Input() showGuardQuestion = true;
  @Input() headingLarge = true;
  @Input() showCaption = false;
  @Input() recommendedImprovements: RecommendedImprovements;
  @Input() baseLink;

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'explanation', header: 'Explanation', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'change', header: '', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'delete', header: '', widthClass: 'govuk-!-width-one-quarter' },
  ];
}
