import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap, tap } from 'rxjs';

import { uncorrectedMisstatementsQuery } from '@aviation/request-task/aer/shared/uncorrected-misstatements/uncorrected-misstatements.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerUncorrectedMisstatements } from 'pmrv-api';

import { UncorrectedMisstatementsFormProvider } from './uncorrected-misstatements-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  uncorrectedMisstatements: AviationAerUncorrectedMisstatements;
}

@Component({
  selector: 'app-uncorrected-misstatements',
  templateUrl: './uncorrected-misstatements.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UncorrectedMisstatementsComponent {
  private uncorrectedMisstatements: AviationAerUncorrectedMisstatements;
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(uncorrectedMisstatementsQuery.selectUncorrectedMisstatements),
  ]).pipe(
    map(([isEditable, uncorrectedMisstatements]) => ({
      pageHeader: 'Are there any misstatements that were not corrected before issuing this report?',
      isEditable,
      uncorrectedMisstatements,
    })),
    tap((data) => {
      this.uncorrectedMisstatements = data.uncorrectedMisstatements;
    }),
  );
  form = new FormGroup({
    exist: this.formProvider.existCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: UncorrectedMisstatementsFormProvider,
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
        .pipe(first(), uncorrectedMisstatementsQuery.selectUncorrectedMisstatements)
        .pipe(
          switchMap((uncorrectedMisstatements) => {
            const value = this.form.get('exist').value
              ? {
                  ...uncorrectedMisstatements,
                  ...this.form.value,
                }
              : ({
                  ...this.form.value,
                  uncorrectedMisstatements: [],
                } as AviationAerUncorrectedMisstatements);

            this.formProvider.setFormValue(value);
            return this.store.aerVerifyDelegate.saveAerVerify({ uncorrectedMisstatements: value }, 'in progress');
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
        if (this.uncorrectedMisstatements?.uncorrectedMisstatements?.length > 0) {
          return this.router.navigate(['list'], { relativeTo: this.route, queryParams: { change: true } });
        } else {
          return this.router.navigate(['list/0'], { relativeTo: this.route, queryParams: { change: true } });
        }
    }
  }
}
