import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { RequestActionStore } from '@aviation/request-action/store';
import { virQuery } from '@aviation/request-action/vir/vir.selectors';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { matchVerificationItem } from '@shared/vir-shared/utils/match-verification-item';

@Injectable({
  providedIn: 'root',
})
export class ReferenceItemResolver {
  constructor(private store: RequestActionStore, private readonly router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<VerificationDataItem> {
    return this.store.pipe(virQuery.selectVerificationData).pipe(
      map((verificationData) => {
        const verificationDataItem = matchVerificationItem(route.paramMap.get('id'), verificationData);
        if (verificationDataItem) {
          return verificationDataItem;
        } else {
          this.router.navigate(['error', '404']).then();
        }
      }),
    );
  }
}
