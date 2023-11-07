import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { matchVerificationItem } from '@shared/vir-shared/utils/match-verification-item';
import { VirService } from '@tasks/vir/core/vir.service';

@Injectable({
  providedIn: 'root',
})
export class ResponseItemResolver {
  constructor(private readonly virService: VirService, private readonly router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<VerificationDataItem> {
    return this.virService.payload$.pipe(
      map((payload) => {
        const verificationDataItem = matchVerificationItem(route.paramMap.get('id'), payload?.verificationData);
        if (verificationDataItem) {
          return verificationDataItem;
        } else {
          this.router.navigate(['error', '404']).then();
        }
      }),
    );
  }
}
