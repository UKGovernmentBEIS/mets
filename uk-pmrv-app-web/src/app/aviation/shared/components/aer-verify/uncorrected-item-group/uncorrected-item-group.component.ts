import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { UncorrectedItem } from 'pmrv-api';

@Component({
  selector: 'app-uncorrected-item-group',
  templateUrl: './uncorrected-item-group.component.html',
  imports: [GovukComponentsModule, SharedModule, RouterModule],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UncorrectedItemGroupComponent implements OnInit {
  @Input() isEditable = false;
  @Input() uncorrectedItems: UncorrectedItem[];
  @Input() queryParams = {};
  @Input() baseUrl = './';
  @Input() explanationHeader = 'Explanation';

  columns: GovukTableColumn[];

  ngOnInit(): void {
    this.columns = [
      { field: 'reference', header: 'Reference', widthClass: 'govuk-input--width-20' },
      { field: 'explanation', header: this.explanationHeader, widthClass: 'govuk-input--width-20' },
      { field: 'impact', header: 'Impact', widthClass: 'govuk-input--width-20' },
      { field: 'change', header: '', widthClass: 'govuk-input--width-20' },
      { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
    ];
  }
}
