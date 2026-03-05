import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

@Component({
  selector: 'app-overall-decision-list',
  templateUrl: './overall-decision-reason-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionReasonListComponent implements OnInit {
  @Input() isEditable = false;
  @Input() list: string[] | null;
  @Input() queryParams = {};

  data: { reason: string }[];

  columns: GovukTableColumn[] = [
    { field: 'reason', header: 'Reason', widthClass: 'govuk-input--width-20' },
    { field: 'change', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];

  ngOnInit(): void {
    this.data = this.list ? this.list.map((reason) => ({ reason: reason })) : [];
  }
}
