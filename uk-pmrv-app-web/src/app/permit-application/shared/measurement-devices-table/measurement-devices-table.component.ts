import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PipesModule } from '@shared/pipes/pipes.module';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { MeasurementDeviceOrMethod } from 'pmrv-api';

import { SharedPermitModule } from '../shared-permit.module';

@Component({
  selector: 'app-measurement-devices-table',
  templateUrl: './measurement-devices-table.component.html',
  standalone: true,
  imports: [CommonModule, GovukComponentsModule, PipesModule, SharedPermitModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurementDevicesTableComponent {
  data = input.required<MeasurementDeviceOrMethod[]>();
  isEditable = input.required<boolean>();
  noBottomBorder = input.required<boolean>();

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference' },
    { field: 'type', header: 'Type' },
    { field: 'measurementRange', header: 'Measurement range' },
    { field: 'meteringRangeUnits', header: 'Metering range units' },
    { field: 'specifiedUncertaintyPercentage', header: 'Specified uncertainty' },
    { field: 'location', header: 'Location' },
    { field: 'change', header: '' },
    { field: 'delete', header: '' },
  ];
}
