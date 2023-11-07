import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import {
  nonSignificantChanges,
  significantChanges,
} from '@aviation/shared/components/emp/variation-details-summary-template/util/variation-details';
import { VariationDetailsReasonTypePipe } from '@aviation/shared/pipes/variation-details-reason-type.pipe';

import { GovukComponentsModule } from 'govuk-components';

import { EmpVariationUkEtsDetails, EmpVariationUkEtsRegulatorLedReason } from 'pmrv-api';

@Component({
  selector: 'app-variation-details-summary-template',
  standalone: true,
  imports: [GovukComponentsModule, NgIf, RouterLink, NgFor, VariationDetailsReasonTypePipe],
  templateUrl: './variation-details-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsSummaryTemplateComponent implements OnInit {
  @Input() isEditable = false;
  @Input() variationDetails: EmpVariationUkEtsDetails;
  @Input() variationRegulatorLedReason: EmpVariationUkEtsRegulatorLedReason;
  @Input() changeUrlQueryParams: Params = {};

  significantChangesText = [];
  nonSignificantChangesText = [];

  ngOnInit(): void {
    this.significantChangesText = this.variationDetails?.changes
      ?.map((change) => significantChanges[change])
      .filter((change) => !!change);
    this.nonSignificantChangesText = this.variationDetails?.changes
      ?.map((change) => nonSignificantChanges[change])
      .filter((change) => !!change);
  }
}
