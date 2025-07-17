import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { VirService } from '@tasks/vir/core/vir.service';

import { VirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class RegulatorImprovementResponseResolver {
  constructor(
    private readonly virService: VirService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<string> {
    return (this.virService.payload$ as Observable<VirApplicationRespondToRegulatorCommentsRequestTaskPayload>).pipe(
      map((payload) => {
        const index = route.paramMap.get('id');
        const regulatorImprovementResponse = payload?.regulatorImprovementResponses?.[index];
        if (regulatorImprovementResponse) {
          return index;
        } else {
          this.router.navigate(['error', '404']).then();
        }
      }),
    );
  }
}
