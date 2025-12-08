import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { WASTE_QDR_TASK_FORM, WasteQdrService } from '@tasks/waste-qdr/core';
import { WasteQdrTaskComponent } from '@tasks/waste-qdr/shared';

import { wasteQdrUploadReportFormProvider } from './upload-qdr-form.provider';

@Component({
  selector: 'app-waste-qdr-upload-qdr',
  standalone: true,
  imports: [RouterLink, SharedModule, WasteQdrTaskComponent],
  templateUrl: './upload-qdr.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [wasteQdrUploadReportFormProvider],
})
export class WasteQdrUploadQdrComponent {
  isEditable = this.wasteQdrService.isEditable;
  payload = this.wasteQdrService.payload;

  constructor(
    @Inject(WASTE_QDR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly wasteQdrService: WasteQdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  getDownloadUrl() {
    return this.wasteQdrService.getBaseFileDownloadUrl();
  }

  getDocumentDownloadUrl(uuid: string): string | string[] {
    return ['../../../..', 'file-download', uuid];
  }

  onSubmit() {
    const nextWizardStep = 'summary';
    const payload = this.payload();

    if (this.form.dirty) {
      this.wasteQdrService
        .postTaskSave(
          {
            report: this.form.controls.report.value?.uuid,
            supportingFiles: this.form.controls.supportingFiles.value?.map((file) => file.uuid),
            notes: this.form.controls.notes.value,
          },
          {
            ...payload?.wasteQDRAttachments,
            ...this.getWasteQdrAttachments(),
          },
          false,
          'qdr',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../', nextWizardStep], { relativeTo: this.route }));
    } else {
      this.router.navigate(['../', nextWizardStep], { relativeTo: this.route });
    }
  }

  private getWasteQdrAttachments() {
    const attachments =
      this.form.controls.supportingFiles.value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {}) ||
      {};

    const report = this.form.controls.report.value;
    if (report) {
      attachments[report.uuid] = report.file.name;
    }

    return attachments;
  }
}
