import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectCurrentDomain } from '@core/store';
import { AuthStore } from '@core/store/auth/auth.store';

import { NonComplianceCivilPenaltyRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { NonComplianceService } from '../../core/non-compliance.service';
import { NON_COMPLIANCE_TASK_FORM } from '../../core/non-compliance-form.token';
import { uploadCivilPenaltyFormProvider } from './upload-civil-penalty-form.provider';

@Component({
  selector: 'app-upload-civil-penalty',
  templateUrl: './upload-civil-penalty.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [uploadCivilPenaltyFormProvider],
})
export class UploadCivilPenaltyComponent {
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  businessErrorService: any;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  nextRoute = '../summary';
  constructor(
    @Inject(NON_COMPLIANCE_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    readonly nonComplianceService: NonComplianceService,
    public readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  onSubmit() {
    if (!this.form.dirty) {
      this.router.navigate([this.nextRoute], { relativeTo: this.route }).then();
    } else {
      this.nonComplianceService.payload$
        .pipe(
          first(),
          switchMap((payload) => {
            const nonCompliance = payload as NonComplianceCivilPenaltyRequestTaskPayload;
            return this.nonComplianceService.saveCivilPenalty(
              {
                civilPenalty: this.form.controls.civilPenalty.value.uuid,
                penaltyAmount: this.form.controls.penaltyAmount?.value,
                dueDate: this.form.controls.dueDate?.value,
                comments: this.form.controls.comments?.value,
              },
              false,
              {
                ...nonCompliance?.nonComplianceAttachments,
                ...this.getAttachments(),
              },
            );
          }),
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([this.nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl(uuid: string): string | string[] {
    return ['../../..', 'file-download', uuid];
  }

  private getAttachments() {
    const file = this.form.controls.civilPenalty?.value;

    return file ? { [file.uuid]: file.file.name } : {};
  }
}
