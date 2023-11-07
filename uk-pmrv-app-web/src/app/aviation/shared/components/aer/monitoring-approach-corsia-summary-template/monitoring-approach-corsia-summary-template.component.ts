import { Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { AircraftFuelBurnRatioTableComponent } from '@aviation/shared/components/aer/aircraft-fuel-burn-ratio-table/aircraft-fuel-burn-ratio-table.component';
import { CertDetailsFlightTypePipe } from '@aviation/shared/pipes/cert-details-flight-type.pipe';
import { FuelDensityTypePipe } from '@aviation/shared/pipes/fuel-density-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerCorsiaMonitoringApproach } from 'pmrv-api';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-monitoring-approach-corsia-summary-template',
  templateUrl: './monitoring-approach-corsia-summary-template.component.html',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    RouterLinkWithHref,
    CertDetailsFlightTypePipe,
    FuelDensityTypePipe,
    AircraftFuelBurnRatioTableComponent,
  ],
})
export class MonitoringApproachCorsiaSummaryTemplateComponent {
  @Input() data: AviationAerCorsiaMonitoringApproach | null;
  @Input() isEditable = false;
  @Input() changeUrlQueryParams: Params = {};
}
