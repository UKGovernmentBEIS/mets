import { Component } from '@angular/core';

import { FuelUpliftSupplierRecordTypePipe } from '@aviation/shared/pipes/fuel-uplift-records-type.pipe';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-fuel-uplift-records-form',
  imports: [GovukComponentsModule, SharedModule, FuelUpliftSupplierRecordTypePipe],
  templateUrl: './fuel-uplift-records-form.component.html',
  viewProviders: [existingControlContainer],
})
export class FuelUpliftRecordsFormComponent {}
