import { Pipe, PipeTransform } from '@angular/core';

import { MonitoringApproachDescriptionPipe } from '@shared/pipes/monitoring-approach-description.pipe';

import { PermitMonitoringApproachSection } from 'pmrv-api';

@Pipe({ name: 'monitoringApproachEmissionDescription' })
export class MonitoringApproachEmissionDescriptionPipe implements PipeTransform {
  transform(value: PermitMonitoringApproachSection['type']): string {
    const approachDescriptionPipe = new MonitoringApproachDescriptionPipe();
    return value === 'INHERENT_CO2'
      ? approachDescriptionPipe.transform(value)
      : approachDescriptionPipe.transform(value) + ' emissions';
  }
}
