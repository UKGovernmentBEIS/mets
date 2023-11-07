import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { UncorrectedNonConformities } from 'pmrv-api';

@Component({
  selector: 'app-non-conformities-previous-year-group',
  templateUrl: './non-conformities-previous-year-group.component.html',

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonConformitiesPreviousYearGroupComponent {
  @Input() isEditable = false;
  @Input() showGuardQuestion = true;
  @Input() headingLarge = true;
  @Input() showCaption = false;
  @Input() uncorrectedNonConformities: UncorrectedNonConformities;
  @Input() baseLink;

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'explanation', header: 'Explanation', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'change', header: '', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'delete', header: '', widthClass: 'govuk-!-width-one-quarter' },
  ];
}
