import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';

import { map, Observable } from 'rxjs';

import { HseTiService } from '@tasks/hseti/core';
import { interpolate } from '@tasks/hseti/utils/interpolate';

@Injectable()
export class PageTitleResolver implements Resolve<string> {
  constructor(private hseTiService: HseTiService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<string> {
    const template = route.data['pageTitleTemplate'] as string;
    const decision = route.paramMap.get('decision') || 'decision';

    return this.hseTiService.allocationPeriod$.pipe(
      map((allocationPeriod) => {
        const params = {
          allocationPeriod,
          decision,
        };
        return interpolate(template, params);
      }),
    );
  }
}
