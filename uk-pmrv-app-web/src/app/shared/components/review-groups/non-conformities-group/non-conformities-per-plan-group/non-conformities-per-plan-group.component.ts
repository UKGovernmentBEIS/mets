import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { UncorrectedNonConformities } from 'pmrv-api';

@Component({
  selector: 'app-non-conformities-per-plan-group',
  templateUrl: './non-conformities-per-plan-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonConformitiesPerPlanGroupComponent {
  @Input() isEditable = false;
  @Input() showGuardQuestion = true;
  @Input() headingLarge = true;
  @Input() showCaption = false;
  @Input() uncorrectedNonConformities: UncorrectedNonConformities;
  @Input() baseLink;

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-input--width-20' },
    { field: 'explanation', header: 'Explanation', widthClass: 'govuk-input--width-20' },
    { field: 'impact', header: 'Impact', widthClass: 'govuk-input--width-20' },
    { field: 'change', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];
}
