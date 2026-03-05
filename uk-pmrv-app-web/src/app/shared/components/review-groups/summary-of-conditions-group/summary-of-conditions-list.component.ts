import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { VerifierComment } from 'pmrv-api';

@Component({
  selector: 'app-summary-of-conditions-list',
  templateUrl: './summary-of-conditions-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryOfConditionsListComponent {
  @Input() isEditable = false;
  @Input() list: VerifierComment[];
  @Input() baseChangeLink;

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-input--width-20' },
    { field: 'explanation', header: 'Explanation', widthClass: 'govuk-input--width-20' },
    { field: 'change', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];
}
