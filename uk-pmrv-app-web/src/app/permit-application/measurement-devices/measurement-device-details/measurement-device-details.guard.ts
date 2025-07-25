import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable()
export class MeasurementDeviceDetailsGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.getTask('measurementDevicesOrMethods').pipe(
      first(),
      map(
        (measurementDevicesOrMethods) =>
          measurementDevicesOrMethods.some(
            (measurementDevice) => measurementDevice.id === route.paramMap.get('deviceId'),
          ) ||
          this.router.parseUrl(
            `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/measurement-devices/summary`,
          ),
      ),
    );
  }
}
