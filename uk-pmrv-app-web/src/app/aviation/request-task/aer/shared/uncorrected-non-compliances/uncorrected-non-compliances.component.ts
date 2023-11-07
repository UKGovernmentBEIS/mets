import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap, tap } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { uncorrectedNonCompliancesQuery } from '@aviation/request-task/aer/shared/uncorrected-non-compliances/uncorrected-non-compliances.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerUncorrectedNonCompliances } from 'pmrv-api';

import { UncorrectedNonCompliancesFormProvider } from './uncorrected-non-compliances-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  uncorrectedNonCompliances: AviationAerUncorrectedNonCompliances;
}

@Component({
  selector: 'app-uncorrected-non-compliances',
  templateUrl: './uncorrected-non-compliances.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UncorrectedNonCompliancesComponent {
  protected uncorrectedNonCompliances: AviationAerUncorrectedNonCompliances;
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectIsCorsia),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(uncorrectedNonCompliancesQuery.selectUncorrectedNonCompliances),
  ]).pipe(
    map(([isCorsia, isEditable, uncorrectedNonCompliances]) => ({
      pageHeader: isCorsia
        ? 'Have there been any uncorrected non-compliances with the Air Navigation Order?'
        : 'Have there been any uncorrected non-compliances with the monitoring and reporting regulations?',
      isEditable,
      uncorrectedNonCompliances,
    })),
    tap((data) => {
      this.uncorrectedNonCompliances = data.uncorrectedNonCompliances;
    }),
  );
  form = new FormGroup({
    exist: this.formProvider.existCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: UncorrectedNonCompliancesFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.nextUrl();
    } else {
      this.store
        .pipe(first(), uncorrectedNonCompliancesQuery.selectUncorrectedNonCompliances)
        .pipe(
          switchMap((uncorrectedNonCompliances) => {
            const value = this.form.get('exist').value
              ? {
                  ...uncorrectedNonCompliances,
                  ...this.form.value,
                }
              : ({
                  ...this.form.value,
                  uncorrectedNonCompliances: [],
                } as AviationAerUncorrectedNonCompliances);

            this.formProvider.setFormValue(value);
            return this.store.aerVerifyDelegate.saveAerVerify({ uncorrectedNonCompliances: value }, 'in progress');
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.nextUrl());
    }
  }

  private nextUrl() {
    switch (this.form.get('exist').value) {
      case false:
        return this.router.navigate(['summary'], { relativeTo: this.route, queryParams: { change: true } });
      case true:
        if (this.uncorrectedNonCompliances?.uncorrectedNonCompliances?.length > 0) {
          return this.router.navigate(['list'], { relativeTo: this.route, queryParams: { change: true } });
        } else {
          return this.router.navigate(['list/0'], { relativeTo: this.route, queryParams: { change: true } });
        }
    }
  }
}
