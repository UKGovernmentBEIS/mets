import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { PERMANENT_CESSATION_TASK_FORM } from '@tasks/permanent-cessation/core/permanent-cessation-task-form.token';
import { PermanentCessationService } from '@tasks/permanent-cessation/shared';
import { PermanentCessationTaskComponent } from '@tasks/permanent-cessation/shared/components/permanent-cessation-task/permanent-cessation-task.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { PermanentCessationApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { detailsOfPermanentCessationFormProvider } from './details-of-cessation-form.provider';

@Component({
  selector: 'app-details-of-cessation',
  imports: [SharedModule, TaskSharedModule, PermanentCessationTaskComponent],
  templateUrl: './details-of-cessation.component.html',
  providers: [detailsOfPermanentCessationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsOfCessationComponent {
  permanentCessationPayload: Signal<PermanentCessationApplicationSubmitRequestTaskPayload> =
    this.permanentCessationService.payload;

  constructor(
    @Inject(PERMANENT_CESSATION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly permanentCessationService: PermanentCessationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  getDownloadUrl() {
    return this.permanentCessationService.getBaseFileDownloadUrl();
  }

  onSubmit(): void {
    const nextRoute = 'summary';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      const permanentCessationAttachments =
        this.form.controls.files.value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {}) || {};

      this.permanentCessationService
        .savePermanentCessation(
          {
            ...this.form.value,
            files: this.form.controls.files.value?.map((file) => file.uuid),
          },
          permanentCessationAttachments,
          false,
          'details',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
