import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectCurrentDomain } from '@core/store';
import { AuthStore } from '@core/store/auth/auth.store';

import { NonComplianceDailyPenaltyNoticeRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { NonComplianceService } from '../../core/non-compliance.service';
import { NON_COMPLIANCE_TASK_FORM } from '../../core/non-compliance-form.token';
import { uploadInitialNoticeFormProvider } from './upload-initial-notice-form.provider';

@Component({
  selector: 'app-upload-initial-notice',
  templateUrl: './upload-initial-notice.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [uploadInitialNoticeFormProvider],
})
export class UploadInitialNoticeComponent {
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  businessErrorService: any;
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
            const nonCompliance = payload as NonComplianceDailyPenaltyNoticeRequestTaskPayload;

            return this.nonComplianceService.saveDailyPenaltyNotice(
              {
                dailyPenaltyNotice: this.form.controls.dailyPenaltyNotice.value.uuid,
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
    const file = this.form.controls.dailyPenaltyNotice?.value;

    return file ? { [file.uuid]: file.file.name } : {};
  }
}
