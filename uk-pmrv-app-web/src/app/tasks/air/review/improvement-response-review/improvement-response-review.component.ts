import { AfterViewInit, ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AirImprovementAll } from '@shared/air-shared/types/air-improvement-all.type';
import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';
import { improvementResponseReviewFormProvider } from '@tasks/air/review/improvement-response-review/improvement-response-review-form.provider';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { AirService } from '@tasks/air/shared/services/air.service';
import { AirImprovementResponseService } from '@tasks/air/shared/services/air-improvement-response.service';

import { AirApplicationReviewRequestTaskPayload, RegulatorAirImprovementResponse } from 'pmrv-api';

@Component({
  selector: 'app-improvement-response-review',
  templateUrl: './improvement-response-review.component.html',
  providers: [improvementResponseReviewFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImprovementResponseReviewComponent implements PendingRequest, AfterViewInit {
  reference = this.route.snapshot.paramMap.get('id');
  airImprovement = this.route.snapshot.data.airImprovement as AirImprovementAll;
  isEditable$ = this.airService.isEditable$;
  airPayload$ = this.airService.payload$ as Observable<AirApplicationReviewRequestTaskPayload>;
  operatorAirImprovementResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses[this.reference] as OperatorAirImprovementResponseAll),
  );
  documentFiles$ = this.operatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getOperatorDownloadUrlFiles(payload?.files) : [])),
  );
  resolvedChangeLink$ = this.operatorAirImprovementResponse$.pipe(
    map((payload) => '../' + this.airImprovementResponseService.mapResponseTypeToPath(payload.type)),
  );

  constructor(
    @Inject(AIR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly airService: AirService,
    private readonly airImprovementResponseService: AirImprovementResponseService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngAfterViewInit(): void {
    this.form.markAsPristine();
  }

  onSubmit() {
    const nextRoute = '../summary';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      (this.airService.payload$ as Observable<AirApplicationReviewRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) =>
            this.airService.postAirReviewTaskSave(
              {
                regulatorReviewResponse: {
                  ...payload?.regulatorReviewResponse,
                  regulatorImprovementResponses: {
                    ...payload?.regulatorReviewResponse?.regulatorImprovementResponses,
                    [this.reference]: this.getFormData(),
                  },
                },
                reviewSectionsCompleted: {
                  ...payload?.reviewSectionsCompleted,
                  [this.reference]: false,
                },
              },
              {
                ...payload?.reviewAttachments,
                ...this.getReviewAttachments(),
              },
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.airService.createBaseFileDownloadUrl();
  }

  private getFormData(): RegulatorAirImprovementResponse {
    return {
      improvementRequired: this.form.get('improvementRequired').value,
      improvementDeadline: this.form.get('improvementRequired').value
        ? this.form.get('improvementDeadline').value
        : null,
      officialResponse: this.form.get('officialResponse').value,
      comments: this.form.get('comments').value,
      files: this.form.get('files').value?.map((file) => file.uuid),
    };
  }

  private getReviewAttachments() {
    return this.form.get('files').value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
