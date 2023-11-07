import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

import { VirApplicationReviewedRequestActionPayload, VirVerificationData } from 'pmrv-api';

import { VirService } from './vir.service';

@Injectable({
  providedIn: 'root',
})
export class ResponseItemResolver implements Resolve<VerificationDataItem> {
  constructor(private readonly virService: VirService, private readonly router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<VerificationDataItem> {
    return (this.virService.payload$ as Observable<VirApplicationReviewedRequestActionPayload>).pipe(
      map((payload) => {
        const verificationDataItem = this.matchVerificationItem(route.paramMap.get('id'), payload?.verificationData);
        if (verificationDataItem) {
          return verificationDataItem;
        } else {
          this.router.navigate(['error', '404']).then();
        }
      }),
    );
  }

  private matchVerificationItem(id: string, verificationData?: VirVerificationData): VerificationDataItem {
    if (verificationData) {
      const filteredGroup = Object.values(verificationData).find((item) => item?.[id] !== undefined);
      return filteredGroup?.[id] ?? null;
    }
    return null;
  }
}
