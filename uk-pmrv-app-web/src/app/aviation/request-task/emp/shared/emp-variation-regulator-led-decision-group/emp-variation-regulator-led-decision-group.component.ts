import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, filter, first, map, Observable, of, switchMap, takeUntil } from 'rxjs';

import { empQuery } from '@aviation/request-task/emp/shared/emp.selectors';
import { EmpVariationRegulatorLedDecisionGroupFormProvider } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group-form.provider';
import { EmpVariationRegulatorLedDecisionGroupFormComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/form/emp-variation-regulator-led-decision-group-form.component';
import { EmpReviewGroup, empReviewGroupMap } from '@aviation/request-task/emp/shared/util/emp.util';
import { EmpVariationRegulatorLedDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-regulator-led-decision-group-summary/emp-variation-regulator-led-decision-group-summary.component';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { EmpAcceptedVariationDecisionDetails } from 'pmrv-api';

import { EmpTaskKey, requestTaskQuery, RequestTaskStore, SectionsCompletedMap } from '../../../store';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-emp-variation-regulator-led-decision-group',
  standalone: true,
  imports: [
    SharedModule,
    EmpVariationRegulatorLedDecisionGroupFormComponent,
    EmpVariationRegulatorLedDecisionGroupSummaryComponent,
  ],
  templateUrl: './emp-variation-regulator-led-decision-group.component.html',
  providers: [DestroySubject],
})
export class EmpVariationRegulatorLedDecisionGroupComponent implements OnInit, OnDestroy {
  @Input() taskKey: EmpTaskKey;
  @Input() addValidator: ValidatorFn;

  form = this.formProvider.form;
  isEditable$ = this.store.pipe(requestTaskQuery.selectIsEditable);
  isOnEditState = false;
  canEdit$: Observable<boolean> = of(true);
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  decisionData$: Observable<EmpAcceptedVariationDecisionDetails>;

  private internalSet = false;
  private groupKey: EmpReviewGroup;

  constructor(
    public store: RequestTaskStore,
    private route: ActivatedRoute,
    private router: Router,
    private destroy$: DestroySubject,
    private pendingRequestService: PendingRequestService,
    readonly formProvider: EmpVariationRegulatorLedDecisionGroupFormProvider,
  ) {}

  ngOnInit(): void {
    this.groupKey = empReviewGroupMap[this.taskKey];

    this.decisionData$ = this.store.pipe(
      empQuery.selectVariationRegLedDecisions,
      map((reviewDecisions) => reviewDecisions[this.groupKey]),
    );

    this.canEdit$ = this.store.pipe(
      requestTaskQuery.selectTasksCompleted,
      map((tasksCompleted) => (tasksCompleted as SectionsCompletedMap)[this.taskKey]?.some((item) => !item) ?? false),
    );

    this.store
      .pipe(
        empQuery.selectVariationRegLedDecisions,
        filter((reviewDecisions) => !!reviewDecisions[this.groupKey]),
        takeUntil(this.destroy$),
      )
      .subscribe((reviewDecisions) => {
        if (!this.internalSet) {
          this.formProvider.setFormValue(reviewDecisions[this.groupKey]);
        }
      });

    if (this.addValidator) {
      this.form.addValidators(this.addValidator);
    }
  }

  ngOnDestroy(): void {
    this.formProvider.destroyForm();
  }

  onSubmit() {
    if (this.form.valid) {
      this.internalSet = true;

      this.store
        .pipe(
          first(),
          requestTaskQuery.selectRequestTaskType,
          switchMap((requestTaskType) =>
            this.store.empDelegate.saveEmpVariationRegLedDecision(this.getFormData(), this.taskKey, requestTaskType),
          ),
          this.pendingRequestService.trackRequest(),
        )
        .subscribe({
          next: () => {
            this.internalSet = false;
            this.isOnEditState = false;

            const path = this.router.url.split('/').at(-1) === 'summary' ? '../../../..' : '../../..';
            this.router.navigate([path], { relativeTo: this.route });
          },
          complete: () => (this.internalSet = false),
        });
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }

  private getFormData(): EmpAcceptedVariationDecisionDetails {
    return {
      notes: this.form.value.notes,
      variationScheduleItems:
        this.form.value.variationScheduleItems
          ?.filter((variationScheduleItem) => !!variationScheduleItem.item)
          .map((variationScheduleItem) => variationScheduleItem.item) ?? null,
    };
  }
}
