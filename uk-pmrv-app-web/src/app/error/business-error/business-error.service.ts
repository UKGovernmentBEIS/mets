import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { BehaviorSubject, first, from, ignoreElements, Observable, switchMap, tap } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store';
import { cloneDeep } from 'lodash-es';

import { BusinessError } from './business-error';

@Injectable({ providedIn: 'root' })
export class BusinessErrorService {
  private readonly errorSubject = new BehaviorSubject<BusinessError>(null);
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain);
  readonly error$ = this.errorSubject.asObservable();

  constructor(
    private readonly router: Router,
    private readonly authStore: AuthStore,
  ) {}

  showError(error: BusinessError): Observable<boolean> {
    return this.currentDomain$.pipe(
      first(),
      tap((currentDomain) => {
        this.handleUpdatedError(error, currentDomain);
      }),
      switchMap(() => this.navigateAfterError()),
    );
  }

  // this method is used to bypass the PendingRequestGuard when the error has to be caught before the
  // PendingRequestService tracks the request
  showErrorForceNavigation(error: BusinessError): Observable<boolean> {
    return this.currentDomain$.pipe(
      first(),
      tap((currentDomain) => {
        this.handleUpdatedError(error, currentDomain);
      }),
      switchMap(() => this.navigateAfterError(true)),
    );
  }

  clear(): void {
    this.errorSubject.next(null);
  }

  private handleUpdatedError(error: BusinessError, currentDomain: 'INSTALLATION' | 'AVIATION') {
    const newError = cloneDeep(error);

    newError.link[0] = currentDomain === 'AVIATION' ? '/aviation' + newError.link[0] : newError.link[0];

    this.errorSubject.next(newError);
  }

  private navigateAfterError(forceNavigation = false) {
    return from(
      this.router.navigate(['/error/business'], { state: { forceNavigation }, skipLocationChange: true }),
    ).pipe(ignoreElements());
  }
}
