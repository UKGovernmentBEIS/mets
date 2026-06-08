import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { switchMap, take, tap } from 'rxjs';

import { AviationAccountsStore, selectAccount, selectUpsertFyro } from '@aviation/accounts/store';
import produce from 'immer';

@Component({
  selector: 'app-edit-fyro-summary',
  standalone: false,
  templateUrl: './edit-fyro-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditFyroSummaryComponent {
  private readonly router: Router = inject(Router);
  private readonly aviationAccountsStore: AviationAccountsStore = inject(AviationAccountsStore);
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  readonly currentState = toSignal(this.aviationAccountsStore.pipe(selectUpsertFyro));

  onContinue(): void {
    this.aviationAccountsStore
      .pipe(selectAccount, take(1))
      .pipe(
        tap((account) => {
          this.aviationAccountsStore.setCurrentAccount(
            produce(account, (updated) => {
              updated.aviationAccount = {
                ...account.aviationAccount,
                commencementDate: this.currentState().commencementDate,
              };
            }),
          );
        }),
        switchMap(() =>
          this.aviationAccountsStore.submitFirstYearOfReportingObligation({
            commencementDate: this.currentState().commencementDate,
            reason: this.currentState().reason,
          }),
        ),
      )
      .subscribe(async () => {
        await this.router.navigate(['../../'], { relativeTo: this.activatedRoute });
      });
  }
}
