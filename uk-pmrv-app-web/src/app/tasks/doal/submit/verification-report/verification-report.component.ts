import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { DoalService } from '../../core/doal.service';
import { DOAL_TASK_FORM } from '../../core/doal-task-form.token';
import { verificationReportFormProvider } from './verification-report-form.provider';

@Component({
  selector: 'app-verification-report',
  templateUrl: './verification-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [verificationReportFormProvider],
})
export class VerificationReportComponent {
  constructor(
    @Inject(DOAL_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['summary'], { relativeTo: this.route });
    } else {
      const documentUuid = this.form.controls.document.value.uuid;
      this.doalService
        .saveDoal(
          {
            verificationReportOfTheActivityLevelReport: {
              ...this.form.value,
              document: documentUuid,
            },
          },
          'verificationReportOfTheActivityLevelReport',
          false,
          { [documentUuid]: this.form.controls.document.value.file.name },
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
    }
  }

  getDownloadUrl(uuid: string): string | string[] {
    return ['../../..', 'file-download', uuid];
  }
}
