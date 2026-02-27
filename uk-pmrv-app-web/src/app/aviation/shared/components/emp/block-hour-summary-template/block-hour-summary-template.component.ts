import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { ProcedureFormSummaryComponent } from '@aviation/shared/components/procedure-form-summary';
import { FuelUpliftSupplierRecordTypePipe } from '@aviation/shared/pipes/fuel-uplift-records-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpBlockHourMethodProcedures } from 'pmrv-api';

@Component({
  selector: 'app-block-hour-summary-template',
  imports: [
    SharedModule,
    GovukComponentsModule,
    ProcedureFormSummaryComponent,
    RouterLinkWithHref,
    FuelUpliftSupplierRecordTypePipe,
  ],
  templateUrl: './block-hour-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BlockHourSummaryTemplateComponent {
  @Input() data: EmpBlockHourMethodProcedures | null;
  @Input() isEditable = false;
  @Input() isCorsia = false;
}
