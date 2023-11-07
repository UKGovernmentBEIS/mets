import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  EventEmitter,
  Inject,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import { FormArray, UntypedFormGroup } from '@angular/forms';

import { BehaviorSubject, map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';

import {
  PermitAcceptedVariationDecisionDetails,
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'pmrv-api';

import { PermitVariationStore } from '../../../../store/permit-variation.store';
import { AboutVariationGroupKey } from '../../../../variation-types';
import { createAnotherVariationScheduleItem } from '../review-group-decision-form-utils';
import { REVIEW_GROUP_DECISION_FORM, reviewGroupDecisionFormProvider } from './review-group-decision-form.provider';

@Component({
  selector: 'app-variation-regulator-led-review-group-decision',
  templateUrl: './review-group-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, reviewGroupDecisionFormProvider],
})
export class ReviewGroupDecisionComponent implements PendingRequest {
  @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'] | AboutVariationGroupKey;
  @Input() canEdit = true;
  @Output() readonly notification = new EventEmitter<boolean>();
  @ViewChild('conditionalHeader') header: ElementRef;

  get variationScheduleItems(): FormArray {
    return this.form.get('variationScheduleItems') as FormArray;
  }

  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  isEditable$ = this.store.pipe(map((state) => state.isEditable));
  isOnEditState = false;
  isOnEditByDefaultState$ = this.store.pipe(
    map((state) => {
      const decision = (
        this.groupKey === 'ABOUT_VARIATION'
          ? state.permitVariationDetailsReviewDecision
          : state.reviewGroupDecisions?.[this.groupKey]
      ) as PermitAcceptedVariationDecisionDetails;
      return !decision?.notes && (!decision?.variationScheduleItems || decision?.variationScheduleItems?.length === 0);
    }),
  );

  constructor(
    @Inject(REVIEW_GROUP_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitVariationStore,
  ) {}

  onSubmit(): void {
    if (this.form.valid) {
      const notes = this.form.controls.notes.value;
      const variationScheduleItems = this.form.controls.variationScheduleItems.value.map(
        (variationScheduleItem) => variationScheduleItem.item,
      );

      if (this.groupKey === 'ABOUT_VARIATION') {
        this.store
          .postReviewGroupDecisionAboutVariationRegulatorLed({
            notes,
            variationScheduleItems,
          })
          .pipe(this.pendingRequest.trackRequest())
          .subscribe(() => {
            this.postSaveActions();
          });
      } else {
        this.store
          .postReviewGroupDecisionVariationRegulatorLed(
            this.groupKey as PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
            {
              notes,
              variationScheduleItems,
            },
          )
          .pipe(this.pendingRequest.trackRequest())
          .subscribe(() => {
            this.postSaveActions();
          });
      }
    } else {
      this.isErrorSummaryDisplayed$.next(true);
    }
  }

  addOtherVariationScheduleItem(): void {
    this.variationScheduleItems.push(createAnotherVariationScheduleItem(null));
  }

  private postSaveActions() {
    this.isOnEditState = false;
    this.notification.emit(true);
  }
}
