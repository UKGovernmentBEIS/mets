import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { ProcedureFormSummaryComponent } from '@aviation/shared/components/procedure-form-summary';
import { FuelUpliftSupplierRecordTypePipe } from '@aviation/shared/pipes/fuel-uplift-records-type.pipe';

import { GovukComponentsModule } from 'govuk-components';

import { EmpFuelUpliftMethodProcedures } from 'pmrv-api';

@Component({
  selector: 'app-fuel-uplift-summary-template',
  templateUrl: './fuel-uplift-summary-template.component.html',
  standalone: true,
  imports: [
    GovukComponentsModule,
    ProcedureFormSummaryComponent,
    RouterLinkWithHref,
    FuelUpliftSupplierRecordTypePipe,
    NgIf,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelUpliftSummaryTemplateComponent {
  @Input() data: EmpFuelUpliftMethodProcedures | null;
  @Input() isEditable = false;
  @Input() isCorsia = false;
}
