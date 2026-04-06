import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { NerDetailsSummaryTemplateComponent } from '@shared/components/ner';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { NerService } from '@tasks/ner/core';
import { NerTaskComponent } from '@tasks/ner/shared';

import { NER, NerApplicationSubmitRequestTaskPayload, RequestTaskDTO } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  hideSubmit: boolean;
  requestTaskType: RequestTaskDTO['type'];
  ner: NER;
  nerFile: AttachedFile;
  nerSupportingFiles: AttachedFile[];
  mmpFile: AttachedFile;
  mmpSupportingFiles: AttachedFile[];
}

@Component({
  selector: 'app-ner-details-summary',
  imports: [SharedModule, NerTaskComponent, NerDetailsSummaryTemplateComponent],
  template: `
    @let vm = this.vm();

    <app-ner-task
      heading="Check your answers"
      caption="New entrant reserve"
      [taskType]="vm.requestTaskType"
      [returnLinkLevelsUp]="2">
      <app-ner-details-summary-template
        [isEditable]="vm.isEditable"
        [ner]="vm.ner"
        [nerFile]="vm.nerFile"
        [nerSupportingFiles]="vm.nerSupportingFiles"
        [mmpFile]="vm.mmpFile"
        [mmpSupportingFiles]="vm.mmpSupportingFiles"></app-ner-details-summary-template>

      <div *ngIf="!vm.hideSubmit" class="govuk-button-group">
        <button appPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
      </div>
    </app-ner-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerDetailsSummaryComponent {
  private readonly nerService = inject(NerService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly pendingRequest = inject(PendingRequestService);
  private readonly payload = this.nerService.payload as Signal<NerApplicationSubmitRequestTaskPayload>;
  private readonly requestTaskType = this.nerService.requestTaskType;
  private readonly isEditable = this.nerService.isEditable;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const ner = payload.ner;
    const requestTaskType = this.requestTaskType();
    const isEditable = this.isEditable();
    const hideSubmit = !isEditable || payload.nerSectionsCompleted['details'];
    const nerFile = this.nerService.getOperatorDownloadUrlFile(ner.nerFiles.file);
    const nerSupportingFiles = this.nerService.getOperatorDownloadUrlFiles(ner.nerFiles.supportingFiles);
    const mmpFile = this.nerService.getOperatorDownloadUrlFile(ner.mmpFiles.file);
    const mmpSupportingFiles = this.nerService.getOperatorDownloadUrlFiles(ner.mmpFiles.supportingFiles);

    return { isEditable, hideSubmit, requestTaskType, ner, nerFile, nerSupportingFiles, mmpFile, mmpSupportingFiles };
  });

  onSubmit() {
    this.nerService
      .postTaskSave({}, {}, true, 'details')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
  }
}
