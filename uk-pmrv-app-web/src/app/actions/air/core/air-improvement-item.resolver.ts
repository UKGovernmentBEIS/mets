import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AirApplicationSubmittedRequestActionPayload, AirImprovement } from 'pmrv-api';

import { AirService } from './air.service';

@Injectable({
  providedIn: 'root',
})
export class AirImprovementItemResolver {
  constructor(
    private readonly airService: AirService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<AirImprovement> {
    return (this.airService.payload$ as Observable<AirApplicationSubmittedRequestActionPayload>).pipe(
      map((payload) => {
        const airImprovement = payload?.airImprovements[route.paramMap.get('id')];
        if (airImprovement) {
          return airImprovement;
        } else {
          this.router.navigate(['error', '404']).then();
        }
      }),
    );
  }
}
