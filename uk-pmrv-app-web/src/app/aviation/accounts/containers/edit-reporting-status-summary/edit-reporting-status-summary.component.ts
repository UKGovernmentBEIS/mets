import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { take } from 'rxjs';

import { AviationAccountsStore, selectReportingStatus } from '@aviation/accounts/store';

@Component({
  selector: 'app-edit-reporting-status-summary',
  standalone: false,
  templateUrl: './edit-reporting-status-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditReportingStatusSummaryComponent {
  private readonly router: Router = inject(Router);
  private readonly aviationAccountsStore: AviationAccountsStore = inject(AviationAccountsStore);
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  readonly currentState = toSignal(this.aviationAccountsStore.pipe(selectReportingStatus));

  reportingYear = computed(() => {
    const currentStateObj = this.currentState();
    return currentStateObj?.upsertStatus?.year + '';
  });

  onContinue(): void {
    this.aviationAccountsStore
      .submitReportingStatus(this.reportingYear(), this.currentState().upsertStatus)
      .pipe(take(1))
      .subscribe(() => {
        this.router.navigate(['../../../'], { relativeTo: this.activatedRoute });
      });
  }
}
