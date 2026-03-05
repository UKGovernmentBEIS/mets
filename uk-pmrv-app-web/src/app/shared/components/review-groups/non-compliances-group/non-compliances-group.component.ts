import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { UncorrectedNonCompliances } from 'pmrv-api';

@Component({
  selector: 'app-non-compliances-group',
  templateUrl: './non-compliances-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonCompliancesGroupComponent {
  @Input() isEditable = false;
  @Input() showGuardQuestion = true;
  @Input() headingLarge = true;
  @Input() showCaption = false;
  @Input() uncorrectedNonCompliances: UncorrectedNonCompliances;
  @Input() baseLink;

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-input--width-20' },
    { field: 'explanation', header: 'Explanation', widthClass: 'govuk-input--width-20' },
    { field: 'impact', header: 'Impact', widthClass: 'govuk-input--width-20' },
    { field: 'change', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];
}
