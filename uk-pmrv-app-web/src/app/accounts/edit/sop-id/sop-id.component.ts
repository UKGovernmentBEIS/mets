import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, filter, first, map, Observable, of, shareReplay, switchMap, tap } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { GovukValidators } from 'govuk-components';

import { InstallationAccountDTO, InstallationAccountPermitDTO, InstallationAccountUpdateService } from 'pmrv-api';

@Component({
  selector: 'app-sop-id',
  template: `
    <app-page-heading size="l">Edit SOP ID</app-page-heading>

    <form (ngSubmit)="onSubmit()" *ngIf="form$ | async as form" [formGroup]="form">
      <govuk-error-summary *ngIf="isSummaryDisplayed | async" [form]="form"></govuk-error-summary>
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-one-half">
          <div formControlName="sopId" govuk-text-input inputType="number"></div>
        </div>
      </div>
      <button appPendingButton govukButton type="submit">Confirm and complete</button>
    </form>

    <a govukLink routerLink="../..">Return to: Installation details</a>
  `,

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SopIdComponent implements OnInit {
  isSummaryDisplayed = new BehaviorSubject<boolean>(false);
  account$ = (
    this.route.data as Observable<{
      accountPermit: InstallationAccountPermitDTO;
    }>
  ).pipe(map((state) => state.accountPermit.account));

  form$ = this.account$.pipe(
    map((account) =>
      this.fb.group({
        sopId: [
          (account as InstallationAccountDTO)?.sopId,
          [
            GovukValidators.max(9999999999, 'The ID should contain 10 numbers maximum'),
            GovukValidators.naturalNumber(),
          ],
        ],
      }),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly accountUpdateService: InstallationAccountUpdateService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit() {
    this.backLinkService.show();
  }

  onSubmit(): void {
    combineLatest([this.form$, this.account$])
      .pipe(
        first(),
        tap(([form]) => {
          if (!form.valid) {
            this.isSummaryDisplayed.next(true);
          }
        }),
        filter(([form]) => form.valid),
        switchMap(([form, account]) =>
          form.dirty
            ? this.accountUpdateService
                .updateInstallationAccountSopId(account.id, {
                  sopId: form.get('sopId').value,
                })
                .pipe(tap((this.route.snapshot.data.accountPermit.account.sopId = form.get('sopId').value)))
            : of(null),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
