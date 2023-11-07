import { AfterViewInit, ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { matchVerificationItem } from '@shared/vir-shared/utils/match-verification-item';
import { operatorFollowupFormProvider } from '@tasks/vir/comments-response/operator-followup/operator-followup-form.provider';
import { VirService } from '@tasks/vir/core/vir.service';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';

import { VirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-operator-followup',
  template: `
    <app-vir-task [heading]="heading" returnToLink="../..">
      <app-respond-item-form
        [verificationDataItem]="verificationDataItem$ | async"
        [operatorImprovementResponse]="operatorImprovementResponse$ | async"
        [documentFiles]="documentFiles$ | async"
        [regulatorImprovementResponse]="regulatorImprovementResponse$ | async"
        [formGroup]="form"
        [isEditable]="isEditable$ | async"
        (formSubmit)="onSubmit()"
      ></app-respond-item-form>
    </app-vir-task>
  `,
  providers: [operatorFollowupFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorFollowupComponent implements AfterViewInit, PendingRequest {
  reference = this.route.snapshot.data.reference;
  heading = `Respond to ${this.reference}`;
  virPayload$ = this.virService.payload$ as Observable<VirApplicationRespondToRegulatorCommentsRequestTaskPayload>;

  verificationDataItem$ = this.virPayload$.pipe(
    map((payload) => matchVerificationItem(this.reference, payload?.verificationData)),
  );
  operatorImprovementResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses?.[this.reference]),
  );
  regulatorImprovementResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.regulatorImprovementResponses?.[this.reference]),
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
      (this.virService.payload$ as Observable<VirApplicationRespondToRegulatorCommentsRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) =>
            this.virService.postVirRespondTaskSave({
              operatorImprovementFollowUpResponse: this.form.value,
              reference: this.reference,
              virRespondToRegulatorCommentsSectionsCompleted: {
                ...payload?.virRespondToRegulatorCommentsSectionsCompleted,
                [this.reference]: false,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
