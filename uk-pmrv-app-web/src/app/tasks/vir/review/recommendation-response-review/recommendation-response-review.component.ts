import { AfterViewInit, ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirService } from '@tasks/vir/core/vir.service';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';
import { recommendationResponseReviewFormProvider } from '@tasks/vir/review/recommendation-response-review/recommendation-response-review-form.provider';

import { RegulatorImprovementResponse, VirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-recommendation-response-review',
  template: `
    <app-vir-task [heading]="heading" returnToLink="../..">
      <app-recommendation-response-item-form
        [formGroup]="form"
        [verificationDataItem]="verificationDataItem"
        [operatorImprovementResponse]="operatorImprovementResponse$ | async"
        [attachedFiles]="documentFiles$ | async"
        [isEditable]="isEditable$ | async"
        (formSubmit)="onSubmit()"
      ></app-recommendation-response-item-form>
    </app-vir-task>
  `,
  providers: [recommendationResponseReviewFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendationResponseReviewComponent implements PendingRequest, AfterViewInit {
  verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;
  private reference = this.verificationDataItem.reference;
  private virPayload$ = this.virService.payload$ as Observable<VirApplicationReviewRequestTaskPayload>;

  heading = `Respond to ${this.reference}`;
  operatorImprovementResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses?.[this.reference]),
  );
  documentFiles$ = this.virPayload$.pipe(
    map((payload) =>
      payload?.operatorImprovementResponses?.[this.reference]?.files
        ? this.virService.getDownloadUrlFiles(payload?.operatorImprovementResponses?.[this.reference]?.files)
        : [],
    ),
  );
  isEditable$ = this.virService.isEditable$;

  constructor(
    @Inject(VIR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly virService: VirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngAfterViewInit(): void {
    this.form.markAsPristine();
  }

  onSubmit() {
    const nextRoute = `../../${this.reference}/summary`;
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      (this.virService.payload$ as Observable<VirApplicationReviewRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) =>
            this.virService.postVirReviewTaskSave({
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
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  private getFormData(): RegulatorImprovementResponse {
    return {
      improvementRequired: this.form.get('improvementRequired').value,
      improvementDeadline: this.form.get('improvementRequired').value
        ? this.form.get('improvementDeadline').value
        : null,
      improvementComments: this.form.get('improvementComments').value,
      operatorActions: this.form.get('operatorActions').value,
    };
  }
}
