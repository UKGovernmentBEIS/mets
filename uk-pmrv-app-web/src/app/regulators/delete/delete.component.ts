import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, Observable, switchMap, tap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore, selectCurrentDomain, selectUserId } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { ApplicationUserDTO, RegulatorAuthoritiesService, RegulatorUserDTO } from 'pmrv-api';

import { saveNotFoundRegulatorError } from '../errors/business-error';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  regulator$: Observable<any> = (this.route.data as Observable<{ user: ApplicationUserDTO | RegulatorUserDTO }>).pipe(
    map(({ user }) => user),
  );
  isConfirmationDisplayed$ = new BehaviorSubject<boolean>(false);
  currentDomain$ = this.authStore.pipe(selectCurrentDomain);
  domainUrlPrefix$ = this.currentDomain$.pipe(map((domain) => (domain === 'AVIATION' ? '/aviation' : '')));

  constructor(
    private readonly authStore: AuthStore,
    private readonly authService: AuthService,
    private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  deleteRegulator(): void {
    combineLatest([this.authStore.pipe(selectUserId), this.route.paramMap])
      .pipe(
        first(),
        switchMap(([userId, paramMap]) =>
          userId === paramMap.get('userId')
            ? this.regulatorAuthoritiesService
                .deleteCurrentRegulatorUserByCompetentAuthority()
                .pipe(tap(() => this.authService.logout()))
            : this.regulatorAuthoritiesService.deleteRegulatorUserByCompetentAuthority(paramMap.get('userId')),
        ),
        catchBadRequest(ErrorCodes.AUTHORITY1003, () =>
          this.businessErrorService.showError(saveNotFoundRegulatorError),
        ),
      )
      .subscribe(() => this.isConfirmationDisplayed$.next(true));
  }
}
