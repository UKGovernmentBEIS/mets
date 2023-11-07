import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { UncorrectedMisstatements } from 'pmrv-api';

@Component({
  selector: 'app-misstatements-group',
  templateUrl: './misstatements-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisstatementsGroupComponent {
  @Input() isEditable = false;
  @Input() showGuardQuestion = true;
  @Input() headingLarge = true;
  @Input() showCaption = false;
  @Input() uncorrectedMisstatements: UncorrectedMisstatements;
  @Input() baseChangeLink;

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-input--width-20' },
    { field: 'explanation', header: 'Explanation', widthClass: 'govuk-input--width-20' },
    { field: 'impact', header: 'Impact', widthClass: 'govuk-input--width-20' },
    { field: 'change', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];
}
