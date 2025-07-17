import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { ProcedureFormSummaryComponent } from '@aviation/shared/components/procedure-form-summary';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { FuelUpliftSupplierRecordTypePipe } from '@aviation/shared/pipes/fuel-uplift-records-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpBlockHourMethodProcedures } from 'pmrv-api';

@Component({
  selector: 'app-block-hour-summary-template',
  templateUrl: './block-hour-summary-template.component.html',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    ReturnToLinkComponent,
    ProcedureFormSummaryComponent,
    RouterLinkWithHref,
    FuelUpliftSupplierRecordTypePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BlockHourSummaryTemplateComponent {
  @Input() data: EmpBlockHourMethodProcedures | null;
  @Input() isEditable = false;
  @Input() isCorsia = false;
}
