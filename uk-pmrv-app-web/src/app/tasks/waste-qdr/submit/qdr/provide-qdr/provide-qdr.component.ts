import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { WASTE_QDR_TASK_FORM, WasteQdrService } from '@tasks/waste-qdr/core';
import { WasteQdrTaskComponent } from '@tasks/waste-qdr/shared';

import { WasteQDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { provideQdrFormProvider } from './provide-qdr-form.provider';

@Component({
  selector: 'app-waste-qdr-provide-qdr',
  standalone: true,
  imports: [SharedModule, WasteQdrTaskComponent],
  templateUrl: './provide-qdr.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideQdrFormProvider],
})
export class WasteQdrProvideQdrComponent {
  isEditable = this.wasteQdrService.isEditable;
  requestMetadata = this.wasteQdrService.requestMetadata;

  constructor(
    @Inject(WASTE_QDR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly wasteQdrService: WasteQdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit() {
    const { reportProvided, reasonForUnprovided } = this.form
      .value as WasteQDRApplicationSubmitRequestTaskPayload['qdr'];
    const nextWizardStep = reportProvided ? 'upload' : 'summary';

    if (this.form.dirty) {
      this.wasteQdrService
        .postTaskSave(
          {
            reportProvided,
            reasonForUnprovided: reasonForUnprovided ?? null,
            ...(reportProvided ? {} : { report: null, supportingFiles: [], notes: null }),
          },
          {},
          false,
          'qdr',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextWizardStep], { relativeTo: this.route }));
    } else {
      this.router.navigate([nextWizardStep], { relativeTo: this.route });
    }
  }
}
