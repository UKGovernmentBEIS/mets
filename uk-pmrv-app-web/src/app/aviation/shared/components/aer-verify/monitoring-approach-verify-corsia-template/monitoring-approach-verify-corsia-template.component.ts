import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { FUEL_TYPES_CORSIA } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { MonitoringApproachVerifyCorsiaTypePipe } from '@aviation/shared/pipes/monitoring-approach-verify-corsia-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaOpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-monitoring-approach-verify-corsia-template',
  templateUrl: './monitoring-approach-verify-corsia-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink, MonitoringApproachVerifyCorsiaTypePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachVerifyCorsiaTemplateComponent implements OnInit {
  @Input() opinionStatement: AviationAerCorsiaOpinionStatement;
  @Input() totalEmissionsProvided: string;
  @Input() totalOffsetEmissionsProvided: string;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};

  fuelTypes = [];

  ngOnInit(): void {
    this.fuelTypes =
      this.opinionStatement?.fuelTypes?.map((fuel) => FUEL_TYPES_CORSIA.find((type) => type.value === fuel)) ?? [];
  }
}
