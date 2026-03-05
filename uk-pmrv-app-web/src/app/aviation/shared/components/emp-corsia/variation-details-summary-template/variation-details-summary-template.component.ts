import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import {
  materialChanges,
  nonMaterialChanges,
  otherChanges,
} from '@aviation/shared/components/emp-corsia/variation-details-summary-template/util/variation-details';
import { VariationDetailsReasonTypePipe } from '@aviation/shared/pipes/variation-details-reason-type.pipe';

import { GovukComponentsModule } from 'govuk-components';

import { EmpVariationCorsiaDetails } from 'pmrv-api';

@Component({
  selector: 'app-corsia-variation-details-summary-template',
  standalone: true,
  imports: [GovukComponentsModule, NgIf, RouterLink, NgFor, VariationDetailsReasonTypePipe],
  templateUrl: './variation-details-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsSummaryTemplateComponent implements OnInit {
  @Input() isEditable = false;
  @Input() variationDetails: EmpVariationCorsiaDetails;
  @Input() variationRegulatorLedReason: string;
  @Input() changeUrlQueryParams: Params = {};

  materialChangesText = [];
  otherChangesText = [];
  nonMaterialChangesText = [];

  ngOnInit(): void {
    this.materialChangesText = this.variationDetails?.changes
      ?.map((change) => materialChanges[change])
      .filter((change) => !!change);
    this.otherChangesText = this.variationDetails?.changes
      ?.map((change) => otherChanges[change])
      .filter((change) => !!change);
    this.nonMaterialChangesText = this.variationDetails?.changes
      ?.map((change) => nonMaterialChanges[change])
      .filter((change) => !!change);
  }
}
