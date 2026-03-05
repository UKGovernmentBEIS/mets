import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { activityLevelReportFormProvider } from '@tasks/aer/submit/activity-level-report/activity-level-report-form.provider';

import { Aer, AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-activity-level-report',
  templateUrl: './activity-level-report.component.html',
  providers: [activityLevelReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityLevelReportComponent {
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const nextRoute = 'summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      (this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) =>
            this.aerService.postTaskSave(
              this.getFormData(),
              {
                ...payload?.aerAttachments,
                ...this.getAerAttachments(),
              },
              false,
              'activityLevelReport',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl(uuid: string): string | string[] {
    return ['../../..', 'file-download', uuid];
  }

  private getFormData(): Pick<Aer, 'activityLevelReport'> {
    const file = this.form.controls.file?.value;
    const freeAllocationOfAllowances = this.form.controls.freeAllocationOfAllowances?.value;

    return {
      activityLevelReport: {
        freeAllocationOfAllowances: freeAllocationOfAllowances,
        ...(freeAllocationOfAllowances && {
          file: file?.uuid,
        }),
      },
    };
  }

  private getAerAttachments() {
    const file = this.form.controls.file?.value;

    return file ? { [file.uuid]: file.file.name } : {};
  }
}
