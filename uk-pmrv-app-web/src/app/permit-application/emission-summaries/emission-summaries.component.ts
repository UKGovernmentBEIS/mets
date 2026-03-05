import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, switchMap, tap } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { editColumns, emissionSummariesColumns } from './emission-summaries';
import { emisionSummariesFormFactory } from './emission-summaries-form.provider';

@Component({
  selector: 'app-emission-summaries',
  templateUrl: './emission-summaries.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emisionSummariesFormFactory],
  styleUrl: './emission-summaries.component.scss',
})
export class EmissionSummariesComponent extends SectionComponent implements PendingRequest {
  displayErrorSummary$ = new BehaviorSubject(false);
  isSummaryDisplayed$ = combineLatest([this.displayErrorSummary$, this.store]).pipe(
    tap(() => this.form.updateValueAndValidity()),
    map(
      ([displaySummary, state]) =>
        displaySummary ||
        this.form.errors?.sourceStreamNotExist ||
        this.form.errors?.emissionSourceNotExist ||
        this.form.errors?.emissionPointNotExist ||
        this.form.errors?.regulatedActivityNotExist ||
        (state.permitSectionsCompleted?.emissionSummaries?.[0] &&
          (this.form.errors?.emissionPointsNotUsed ||
            this.form.errors?.emissionSourcesNotUsed ||
            this.form.errors?.sourceStreamsNotUsed ||
            this.form.errors?.regulatedActivitiesNotUsed)),
    ),
  );

  emissionSummariesColumns: GovukTableColumn[] = [...emissionSummariesColumns, ...editColumns];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  onSubmit(): void {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
      return;
    }

    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postStatus('emissionSummaries', true, data.permitTask)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateSubmitSection('summary', 'fuels'));
  }
}
