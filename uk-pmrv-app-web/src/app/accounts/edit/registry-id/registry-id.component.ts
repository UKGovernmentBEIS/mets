import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, filter, first, map, Observable, of, shareReplay, switchMap, tap } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { InstallationAccountDTO, InstallationAccountPermitDTO, InstallationAccountUpdateService } from 'pmrv-api';

@Component({
  selector: 'app-registry-id',
  template: `
    <app-page-heading size="l">Edit UK ETS Registry ID</app-page-heading>

    <form (ngSubmit)="onSubmit()" *ngIf="form$ | async as form" [formGroup]="form">
      <govuk-error-summary *ngIf="isSummaryDisplayed | async" [form]="form"></govuk-error-summary>
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-one-half">
          <div formControlName="registryId" govuk-text-input></div>
        </div>
      </div>
      <button appPendingButton govukButton type="submit">Confirm and complete</button>
    </form>

    <a govukLink routerLink="../..">Return to: Installation details</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegistryIdComponent implements OnInit {
  isSummaryDisplayed = new BehaviorSubject<boolean>(false);
  account$ = (
    this.route.data as Observable<{
      accountPermit: InstallationAccountPermitDTO;
    }>
  ).pipe(map((state) => state.accountPermit.account));

  form$ = this.account$.pipe(
    map((account) =>
      this.fb.group({
        registryId: [(account as InstallationAccountDTO)?.registryId, [this.registryIdValidator()]],
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

  ngOnInit(): void {
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
                .updateInstallationAccountRegistryId(account.id, {
                  registryId: form.get('registryId').value,
                })
                .pipe(tap((this.route.snapshot.data.accountPermit.account.registryId = form.get('registryId').value)))
            : of(null),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }

  private registryIdValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: string } | null => {
      if (!control.value) {
        return null;
      }
      return control.value < 1000000 || control.value > 9999999 || isNaN(control.value)
        ? { invalidValue: `Enter a 7 digit number` }
        : null;
    };
  }
}
