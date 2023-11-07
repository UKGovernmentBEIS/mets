import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { empQuery } from '@aviation/request-task/emp/shared/emp.selectors';
import {
  isAllCorsiaSectionsApproved,
  isAllSectionsApproved,
  isEvenOneCorsiaSectionRejected,
  isEvenOneSectionRejected,
} from '@aviation/request-task/emp/shared/util/emp.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { CorsiaRequestTypes } from '@aviation/request-task/util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { EmpReviewDeterminationTypePipe } from '@aviation/shared/pipes/review-determination-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpIssuanceDetermination, EmpVariationDetermination } from 'pmrv-api';

import { empCorsiaQuery } from '../../emp-corsia.selectors';
import { OverallDecisionFormProvider } from '../overall-decision-form.provider';

@Component({
  selector: 'app-overall-decision-page',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    EmpReviewDeterminationTypePipe,
  ],
  templateUrl: './overall-decision-action.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionActionComponent implements OnInit {
  form = this.formProvider.form;
  isEditable$ = this.store.pipe(requestTaskQuery.selectIsEditable);
  requestType = this.store.getState().requestTaskItem?.requestTask?.type;

  isApprovedDisplayed$: Observable<boolean>;
  isRejectDisplayed$: Observable<boolean>;

  summaryIsPreviousPage = this.route.snapshot.queryParamMap.get('change') === 'true';

  private determinationTypeStateValue: EmpIssuanceDetermination['type'] | EmpVariationDetermination['type'];
  private isCorsia = CorsiaRequestTypes.includes(this.store.getState().requestTaskItem.requestInfo.type);

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OverallDecisionFormProvider,
    public store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    if (this.isCorsia) {
      this.isApprovedDisplayed$ = this.store.pipe(
        empCorsiaQuery.selectPayload,
        tap((payload) => (this.determinationTypeStateValue = payload.determination.type)),
        map((payload) => isAllCorsiaSectionsApproved(payload, this.requestType)),
      );

      this.isRejectDisplayed$ = this.store.pipe(
        empCorsiaQuery.selectPayload,
        tap((payload) => (this.determinationTypeStateValue = payload.determination.type)),
        map((payload) => isEvenOneCorsiaSectionRejected(payload)),
      );
    } else {
      this.isApprovedDisplayed$ = this.store.pipe(
        empQuery.selectPayload,
        tap((payload) => (this.determinationTypeStateValue = payload.determination.type)),
        map((payload) => isAllSectionsApproved(payload, this.requestType)),
      );

      this.isRejectDisplayed$ = this.store.pipe(
        empQuery.selectPayload,
        tap((payload) => (this.determinationTypeStateValue = payload.determination.type)),
        map((payload) => isEvenOneSectionRejected(payload)),
      );
    }
  }

  onContinue(type: EmpIssuanceDetermination['type'] | EmpVariationDetermination['type']): void {
    this.form.patchValue({ type });

    const page =
      this.summaryIsPreviousPage &&
      (this.form.value.type === 'APPROVED' || this.form.value.type === 'REJECTED') &&
      !!this.form.value.reason
        ? 'summary'
        : 'reason';

    if (this.determinationTypeStateValue === type) {
      this.router.navigate([page], { relativeTo: this.route });
    } else {
      this.store.empDelegate
        .saveEmpOverallDecision(this.form.value as EmpIssuanceDetermination & EmpVariationDetermination, false)
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => this.router.navigate([page], { relativeTo: this.route }));
    }
  }
}
