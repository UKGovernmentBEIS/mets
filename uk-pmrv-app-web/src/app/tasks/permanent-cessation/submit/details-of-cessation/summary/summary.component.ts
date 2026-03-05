import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PermanentCessationDetailsSummaryTemplateComponent } from '@shared/components/permanent-cessation/permanent-cessation-details-summary-template/permanent-cessation-details-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { PermanentCessationService } from '@tasks/permanent-cessation/shared';
import { PermanentCessationTaskComponent } from '@tasks/permanent-cessation/shared/components/permanent-cessation-task/permanent-cessation-task.component';

import { PermanentCessationApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-permanent-cessation-details-summary',
  standalone: true,
  imports: [PermanentCessationDetailsSummaryTemplateComponent, SharedModule, PermanentCessationTaskComponent],
  template: `
    <app-permanent-cessation-task returnToLink="../../">
      <app-page-heading caption="Permanent cessation">Check your answers</app-page-heading>

      <app-permanent-cessation-details-summary-template
        [isEditable]="isEditable()"
        [data]="payload().permanentCessation"
        [files]="files()"></app-permanent-cessation-details-summary-template>

      <div *ngIf="showSubmit()" class="govuk-button-group">
        <button appPendingButton govukButton type="button" (click)="onConfirm()">Confirm and complete</button>
      </div>
    </app-permanent-cessation-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermanentCessationDetailsSummaryComponent {
  isEditable: Signal<boolean> = this.permanentCessationService.isEditable;
  payload: Signal<PermanentCessationApplicationSubmitRequestTaskPayload> = this.permanentCessationService.payload;

  files: Signal<AttachedFile[]> = computed(() => {
    const payload = this.payload();
    const files = payload?.permanentCessation?.files;

    return files ? this.permanentCessationService.getDownloadUrlFiles(files) : [];
  });

  showSubmit: Signal<boolean> = computed(() => {
    const payload = this.payload();
    const isEditable = this.isEditable();

    return isEditable && !payload.permanentCessationSectionsCompleted.details;
  });

  constructor(
    private readonly permanentCessationService: PermanentCessationService,
    private readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    const payload = this.payload();
    const permanentCessation = payload.permanentCessation;

    this.permanentCessationService
      .savePermanentCessation(permanentCessation, payload.permanentCessationAttachments, true, 'details')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
  }
}
