import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { getInstallationActivityLabelByValue, NaceCode } from '@tasks/aer/submit/nace-codes/nace-code-types';

import { GovukTableColumn } from 'govuk-components';

import { NaceCodes } from 'pmrv-api';

@Component({
  selector: 'app-nace-codes-summary-template',
  templateUrl: './nace-codes-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NaceCodesSummaryTemplateComponent implements OnInit {
  @Input() noBottomBorder: boolean;
  @Input() isEditable: boolean;
  @Input() naceCodes: NaceCodes;

  data: { code: NaceCode; label: string }[];

  columns: GovukTableColumn[] = [
    { field: 'mainActivity', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'label', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];

  ngOnInit(): void {
    this.data =
      this.naceCodes?.codes?.map((val) => ({ code: val, label: getInstallationActivityLabelByValue(val) })) ?? [];
  }
}
