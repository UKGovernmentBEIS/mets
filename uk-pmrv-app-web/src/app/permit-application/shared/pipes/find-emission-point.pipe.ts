import { Pipe, PipeTransform } from '@angular/core';

import { iif, map, Observable } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { EmissionPoint } from 'pmrv-api';

@Pipe({ name: 'findEmissionPoint' })
export class FindEmissionPointPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore<PermitApplicationState>) {}

  transform(emissionPoint?: string, currentPermit = true): Observable<EmissionPoint> {
    return iif(
      () => currentPermit,
      this.store.getTask('emissionPoints'),
      this.store.getOriginalTask('emissionPoints'),
    ).pipe(map((emissionPoints) => emissionPoints.find((stream) => stream.id === emissionPoint)));
  }
}
