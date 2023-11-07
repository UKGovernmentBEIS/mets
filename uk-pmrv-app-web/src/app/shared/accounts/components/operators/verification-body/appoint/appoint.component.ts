import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  BehaviorSubject,
  filter,
  first,
  map,
  Observable,
  shareReplay,
  switchMap,
  takeUntil,
  tap,
  withLatestFrom,
} from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, catchElseRethrow, ErrorCodes } from '@error/business-errors';
import { HttpStatuses } from '@error/http-status';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { GovukSelectOption, GovukValidators } from 'govuk-components';

import { AccountVerificationBodyService, VerificationBodyNameInfoDTO } from 'pmrv-api';

import {
  appointedVerificationBodyError,
  changeNotAllowedVerificationBodyError,
  saveNotFoundVerificationBodyError,
  savePartiallyNotFoundOperatorError,
} from '../../errors/business-error';

@Component({
  selector: 'app-appoint',
  templateUrl: './appoint.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AppointComponent implements OnInit {
  form = this.fb.group({
    verificationBodyId: [null, GovukValidators.required('Select a verification body')],
  });
  activeBodies$: Observable<GovukSelectOption<number>[]>;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  appointedAccount$: Observable<any>;
  currentVerificationBody$: Observable<VerificationBodyNameInfoDTO> = (
    this.route.data as Observable<{
      verificationBody: VerificationBodyNameInfoDTO;
    }>
  ).pipe(map((state) => state.verificationBody));

  private accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain);
  domainUrlPrefix$ = this.currentDomain$.pipe(map((domain) => (domain === 'AVIATION' ? '/aviation' : '')));

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly destroy$: DestroySubject,
    private readonly route: ActivatedRoute,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly backLinkService: BackLinkService,
    private readonly authStore: AuthStore,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.activeBodies$ = this.accountId$.pipe(
      switchMap((accountId) => this.accountVerificationBodyService.getActiveVerificationBodies(accountId)),
      map((bodies) => bodies.map((body) => ({ text: body.name, value: body.id }))),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.currentVerificationBody$
      .pipe(
        takeUntil(this.destroy$),
        filter((value) => !!value),
      )
      .subscribe((verificationBody) => {
        this.form.get('verificationBodyId').setValue(verificationBody.id);
        this.form
          .get('verificationBodyId')
          .setValidators([
            GovukValidators.required('Select a verification body'),
            GovukValidators.builder(
              'This verification body is already appointed. Please select another one.',
              (control: AbstractControl) => (control.value === verificationBody.id ? { duplicate: true } : null),
            ),
          ]);
      });
  }

  onSubmit(): void {
    if (this.form.valid) {
      const value = this.form.value;

      this.appointedAccount$ = this.accountId$.pipe(
        first(),
        withLatestFrom(this.currentVerificationBody$),
        switchMap(([accountId, currentVerificationBody]) =>
          currentVerificationBody
            ? this.accountVerificationBodyService.replaceVerificationBodyToAccount(accountId, value)
            : this.accountVerificationBodyService.appointVerificationBodyToAccount(accountId, value),
        ),
        switchMap(() => this.activeBodies$),
        map((bodies) => bodies.find((body) => body.value === value.verificationBodyId).text),
        tap((name) => {
          if (name) {
            this.backLinkService.hide();
          }
        }),
        catchBadRequest(ErrorCodes.ACCOUNT1006, () =>
          this.accountId$.pipe(
            first(),
            withLatestFrom(this.domainUrlPrefix$),
            switchMap(([accountId, domain]) =>
              this.businessErrorService.showError(appointedVerificationBodyError(accountId, domain)),
            ),
          ),
        ),
        catchBadRequest(ErrorCodes.ACCOUNT1007, () =>
          this.accountId$.pipe(
            first(),
            switchMap((accountId) =>
              this.businessErrorService.showError(savePartiallyNotFoundOperatorError(accountId)),
            ),
          ),
        ),
        catchBadRequest(ErrorCodes.ACCOUNT1010, () =>
          this.accountId$.pipe(
            first(),
            switchMap((accountId) =>
              this.businessErrorService.showError(changeNotAllowedVerificationBodyError(accountId)),
            ),
          ),
        ),
        catchElseRethrow(
          (res: HttpErrorResponse) => res.status === HttpStatuses.NotFound,
          () =>
            this.accountId$.pipe(
              first(),
              withLatestFrom(this.currentVerificationBody$),
              switchMap(([accountId, currentVerificationBody]) =>
                this.businessErrorService.showError(
                  currentVerificationBody
                    ? savePartiallyNotFoundOperatorError(accountId)
                    : saveNotFoundVerificationBodyError(accountId),
                ),
              ),
            ),
        ),
      );
    } else {
      this.isSummaryDisplayed$.next(true);
      this.form.markAllAsTouched();
    }
  }
}
