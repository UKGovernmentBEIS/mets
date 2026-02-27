import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { BehaviorSubject, combineLatest, filter, first, map, Observable, of, shareReplay, switchMap, tap } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { GovukValidators } from 'govuk-components';

import { InstallationAccountDTO, InstallationAccountPermitDTO, InstallationAccountUpdateService } from 'pmrv-api';

@Component({
  selector: 'app-commencement-date',
  imports: [SharedModule, RouterModule],
  templateUrl: './commencement-date.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommencementDateComponent implements OnInit {
  account$ = (
    this.route.data as Observable<{
      accountPermit: InstallationAccountPermitDTO;
    }>
  ).pipe(map((state) => state.accountPermit.account));
  isSummaryDisplayed = new BehaviorSubject<boolean>(false);

  form$ = this.account$.pipe(
    map((account) =>
      this.fb.group({
        commencementDate: [
          new Date((account as InstallationAccountDTO)?.commencementDate),
          [GovukValidators.required('Enter date of regulated activities')],
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
                .updateCommencementDate(account.id, {
                  commencementDate: form.get('commencementDate').value,
                })
                .pipe(
                  tap(
                    (this.route.snapshot.data.accountPermit.account.commencementDate =
                      form.get('commencementDate').value),
                  ),
                )
            : of(null),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
