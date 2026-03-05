import { Pipe, PipeTransform } from '@angular/core';

import { iif, Observable } from 'rxjs';

import { PermitMonitoringApproachSection } from 'pmrv-api';

import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

@Pipe({ name: 'monitoringApproachTask' })
export class ApproachTaskPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore<PermitApplicationState>) {}

  transform(type: PermitMonitoringApproachSection['type'], currentPermit = true): Observable<any> {
    return iif(
      () => currentPermit,
      this.store.findTask(`monitoringApproaches.${type}`),
      this.store.findOriginalTask(`monitoringApproaches.${type}`),
    );
  }
}
