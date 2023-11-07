import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, Observable, switchMap, tap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore, selectUserState } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { ApplicationUserDTO, VerifierAuthoritiesService, VerifierUserDTO } from 'pmrv-api';

import { deleteUniqueActiveVerifierError, saveNotFoundVerifierError } from '../errors/business-error';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  verifier$: Observable<any> = (this.route.data as Observable<{ user: ApplicationUserDTO | VerifierUserDTO }>).pipe(
    map((data) => data.user),
  );
  isConfirmationDisplayed$ = new BehaviorSubject<boolean>(false);

  constructor(
    readonly location: Location,
    private readonly authStore: AuthStore,
    private readonly authService: AuthService,
    private readonly verifierAuthoritiesService: VerifierAuthoritiesService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  delete(): void {
    combineLatest([this.authStore.pipe(selectUserState), this.route.paramMap])
      .pipe(
        first(),
        switchMap(([userState, paramMap]) =>
          userState.userId === paramMap.get('userId')
            ? this.verifierAuthoritiesService
                .deleteCurrentVerifierAuthority()
                .pipe(tap(() => this.authService.logout()))
            : this.verifierAuthoritiesService.deleteVerifierAuthority(paramMap.get('userId')),
        ),
        catchBadRequest([ErrorCodes.AUTHORITY1006, ErrorCodes.AUTHORITY1007], (res) => {
          switch (res.error.code) {
            case ErrorCodes.AUTHORITY1006:
              return this.businessErrorService.showError(saveNotFoundVerifierError);
            case ErrorCodes.AUTHORITY1007:
              return this.businessErrorService.showError(deleteUniqueActiveVerifierError);
          }
        }),
      )
      .subscribe(() => this.isConfirmationDisplayed$.next(true));
  }
}
