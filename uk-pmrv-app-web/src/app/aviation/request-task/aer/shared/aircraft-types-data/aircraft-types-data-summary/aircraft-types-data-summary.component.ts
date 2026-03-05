import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkWithHref } from '@angular/router';

import { combineLatest, filter, map, Observable, startWith } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { AircraftTypesDataTableComponent } from '@aviation/shared/components/aer/aircraft-types-table/aircraft-types-data-table.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerAircraftData } from 'pmrv-api';

import { AircraftTypesDataFormProvider } from '../aircraft-types-data-form.provider';

interface ViewModel {
  data: AviationAerAircraftData;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-aircraft-types-data-summary',
  templateUrl: './aircraft-types-data-summary.component.html',
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    AircraftTypesDataTableComponent,
    RouterLink,
    AerReviewDecisionGroupComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AircraftTypesDataSummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('aviationAerAircraftData')),
    this.form.get('aviationAerAircraftDataDetails').statusChanges.pipe(
      startWith(this.form.get('aviationAerAircraftDataDetails').status),
      filter((status) => status === 'VALID' || status === 'INVALID'),
    ),
  ]).pipe(
    map(([type, isEditable, taskStatus, formStatus]) => {
      const isFormValid = formStatus === 'VALID';
      return {
        data: isFormValid ? this.formProvider.getFormValue() : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'aviationAerAircraftData'),
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: AircraftTypesDataFormProvider,
    private store: RequestTaskStore,
    private backLinkService: BackLinkService,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (this.form?.valid) {
      const payload = {
        aviationAerAircraftData: {
          aviationAerAircraftDataDetails: this.form.get('aviationAerAircraftDataDetails').value,
        },
      };
      this.store.aerDelegate
        .saveAer(payload, 'complete')
        .pipe(map(() => this.pendingRequestService.trackRequest()))
        .subscribe(() => {
          this.router.navigate(['../../..'], { relativeTo: this.route });
        });
    }
  }
}
