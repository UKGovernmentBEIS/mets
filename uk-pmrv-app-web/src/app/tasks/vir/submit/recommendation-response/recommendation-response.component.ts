import { AfterViewInit, ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirService } from '@tasks/vir/core/vir.service';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';
import { recommendationResponseFormProvider } from '@tasks/vir/submit/recommendation-response/recommendation-response-form.provider';

import { OperatorImprovementResponse, VirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-recommendation-response',
  template: `
    <app-vir-task [heading]="heading" returnToLink="../..">
      <app-reference-item-form
        [formGroup]="form"
        [verificationDataItem]="verificationDataItem"
        [isEditable]="isEditable$ | async"
        (formSubmit)="onSubmit()"></app-reference-item-form>
    </app-vir-task>
  `,
  providers: [recommendationResponseFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendationResponseComponent implements PendingRequest, AfterViewInit {
  verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;
  reference = this.verificationDataItem.reference;
  heading = `Respond to ${this.reference}`;
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
    const nextRoute = `../../${this.reference}/upload-evidence-question`;

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.virService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.virService.postVirTaskSave({
              operatorImprovementResponses: {
                ...payload?.operatorImprovementResponses,
                [this.reference]: this.getFormData(payload),
              },
              virSectionsCompleted: {
                ...payload?.virSectionsCompleted,
                [this.reference]: false,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  private getFormData(payload: VirApplicationSubmitRequestTaskPayload): OperatorImprovementResponse {
    return {
      ...payload?.operatorImprovementResponses[this.reference],
      isAddressed: this.form.get('isAddressed').value,
      addressedDescription: this.form.get('isAddressed').value
        ? this.form.get('addressedDescription').value
        : this.form.get('addressedDescription2').value,
      addressedDate: this.form.get('isAddressed').value ? this.form.get('addressedDate').value : null,
    };
  }
}
