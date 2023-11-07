import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, iif, map, Observable, of, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AirImprovementAll } from '@shared/air-shared/types/air-improvement-all.type';
import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';
import { improvementResponseReviewFormProvider } from '@tasks/air/review/improvement-response-review/improvement-response-review-form.provider';
import { regulatorImprovementResponseComplete } from '@tasks/air/review/review.wizard';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { AirService } from '@tasks/air/shared/services/air.service';
import { AirImprovementResponseService } from '@tasks/air/shared/services/air-improvement-response.service';

import { AirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  providers: [improvementResponseReviewFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  reference = this.route.snapshot.paramMap.get('id');
  airImprovement = this.route.snapshot.data.airImprovement as AirImprovementAll;
  isEditable$ = this.airService.isEditable$;
  airPayload$ = this.airService.payload$ as Observable<AirApplicationReviewRequestTaskPayload>;
  operatorAirImprovementResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses[this.reference] as OperatorAirImprovementResponseAll),
  );
  operatorFiles$ = this.operatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getOperatorDownloadUrlFiles(payload?.files) : [])),
  );
  resolvedChangeLink$ = this.operatorAirImprovementResponse$.pipe(
    map((payload) => '../' + this.airImprovementResponseService.mapResponseTypeToPath(payload.type)),
  );
  regulatorAirImprovementResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.regulatorReviewResponse?.regulatorImprovementResponses[this.reference]),
  );
  regulatorFiles$ = this.regulatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getRegulatorDownloadUrlFiles(payload?.files) : [])),
  );
  isSummaryDisplayed$ = of(null).pipe();
  errors$ = this.getErrors$();

  constructor(
    @Inject(AIR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly airService: AirService,
    private readonly airImprovementResponseService: AirImprovementResponseService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    (this.airService.payload$ as Observable<AirApplicationReviewRequestTaskPayload>)
      .pipe(
        first(),
        switchMap((payload) =>
          iif(
            () => regulatorImprovementResponseComplete(this.reference, payload?.regulatorReviewResponse),
            this.airService.postAirReviewTaskSave({
              regulatorReviewResponse: payload?.regulatorReviewResponse,
              reviewSectionsCompleted: {
                ...payload?.reviewSectionsCompleted,
                [this.reference]: true,
              },
            }),
            of(false),
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((value) => {
        if (value !== false) {
          this.router.navigate(['../..'], { relativeTo: this.route }).then();
        }
      });
  }

  /**
   * Get errors only when it is not actually complete.
   * Validation does not handle hidden fields, so 'form.valid' is not a valid control mechanism.
   * @private
   */
  private getErrors$(): Observable<ValidationErrors> {
    return this.airPayload$.pipe(
      map((payload) => {
        return regulatorImprovementResponseComplete(this.reference, payload?.regulatorReviewResponse)
          ? null
          : Object.values(this.form.controls)
              .filter((control) => control?.errors)
              .reduce((errors, control) => {
                return {
                  ...errors,
                  ...control.errors,
                };
              }, {});
      }),
    );
  }

  getDownloadUrl() {
    return this.airService.createBaseFileDownloadUrl();
  }
}
