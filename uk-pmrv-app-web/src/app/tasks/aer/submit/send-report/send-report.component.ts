import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { sendReportFormProvider } from '@tasks/aer/submit/send-report/send-report-form.provider';
import { sendReportStatus } from '@tasks/aer/submit/send-report/send-report-status';

@Component({
  selector: 'app-send-report',
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [sendReportFormProvider],
})
export class SendReportComponent {
  isSendReportAvailable$ = this.aerService
    .getPayload()
    .pipe(map((payload) => sendReportStatus(payload) !== 'cannot start yet'));

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (this.form.get('option').value) {
      this.router.navigate(['verification'], { relativeTo: this.route });
    } else {
      this.router.navigate(['regulator'], { relativeTo: this.route });
    }
  }
}
