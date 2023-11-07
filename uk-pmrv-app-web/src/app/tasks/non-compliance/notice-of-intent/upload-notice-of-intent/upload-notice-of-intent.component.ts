import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectCurrentDomain } from '@core/store';
import { AuthStore } from '@core/store/auth/auth.store';

import { NonComplianceNoticeOfIntentRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { NonComplianceService } from '../../core/non-compliance.service';
import { NON_COMPLIANCE_TASK_FORM } from '../../core/non-compliance-form.token';
import { uploadNoticeOfIntentFormProvider } from './upload-notice-of-intent-form.provider';

@Component({
  selector: 'app-upload-notice-of-intent',
  templateUrl: './upload-notice-of-intent.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [uploadNoticeOfIntentFormProvider],
})
export class UploadNoticeOfIntentComponent {
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
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
            const nonCompliance = payload as NonComplianceNoticeOfIntentRequestTaskPayload;

            return this.nonComplianceService.saveNoticeOfIntent(
              {
                noticeOfIntent: this.form.controls.noticeOfIntent.value.uuid,
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
    const file = this.form.controls.noticeOfIntent?.value;

    return file ? { [file.uuid]: file.file.name } : {};
  }
}
