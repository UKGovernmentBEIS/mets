import { AfterViewInit, ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AirImprovementAll } from '@shared/air-shared/types/air-improvement-all.type';
import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';
import { operatorFollowupFormProvider } from '@tasks/air/comments-response/operator-followup/operator-followup-form.provider';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { AirService } from '@tasks/air/shared/services/air.service';

import {
  AirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  OperatorAirImprovementFollowUpResponse,
} from 'pmrv-api';

@Component({
  selector: 'app-operator-followup',
  templateUrl: './operator-followup.component.html',
  providers: [operatorFollowupFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorFollowupComponent implements AfterViewInit, PendingRequest {
  reference = this.route.snapshot.paramMap.get('id');
  isEditable$ = this.airService.isEditable$;
  airPayload$ = this.airService.payload$ as Observable<AirApplicationRespondToRegulatorCommentsRequestTaskPayload>;
  airImprovement = this.route.snapshot.data.airImprovement as AirImprovementAll;
  operatorAirImprovementResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses[this.reference] as OperatorAirImprovementResponseAll),
  );
  operatorFiles$ = this.operatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getOperatorDownloadUrlFiles(payload?.files) : [])),
  );
  regulatorAirImprovementResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.regulatorImprovementResponses[this.reference]),
  );
  regulatorFiles$ = this.regulatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getRegulatorDownloadUrlFiles(payload?.files) : [])),
  );
  today = new Date();

  constructor(
    @Inject(AIR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly airService: AirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngAfterViewInit(): void {
    this.form.markAsPristine();
  }

  onSubmit() {
    const nextRoute = `../summary`;
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      (this.airService.payload$ as Observable<AirApplicationRespondToRegulatorCommentsRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) =>
            this.airService.postAirRespondTaskSave(
              {
                reference: Number(this.reference),
                operatorImprovementFollowUpResponse: this.getFormData(),
                airRespondToRegulatorCommentsSectionsCompleted: {
                  ...payload?.airRespondToRegulatorCommentsSectionsCompleted,
                  [this.reference]: false,
                },
              },
              {
                ...payload?.airAttachments,
                ...this.getAirAttachments(),
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

  private getFormData(): OperatorAirImprovementFollowUpResponse {
    return {
      improvementCompleted: this.form.get('improvementCompleted').value,
      dateCompleted: this.form.get('improvementCompleted').value ? this.form.get('dateCompleted').value : null,
      reason: !this.form.get('improvementCompleted').value ? this.form.get('reason').value : null,
      files: this.form.get('files').value?.map((file) => file.uuid),
    };
  }

  private getAirAttachments() {
    return this.form.get('files').value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
