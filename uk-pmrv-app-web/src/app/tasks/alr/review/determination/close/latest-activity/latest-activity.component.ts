import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ActivitySummaryTemplateComponent } from '@shared/components/alr';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, ALRClosedDetermination } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  alrFile: AttachedFile;
  files: AttachedFile[];
}

@Component({
  selector: 'app-alr-latest-activity',
  standalone: true,
  imports: [AlrTaskSharedModule, ActivitySummaryTemplateComponent, NgIf, SharedModule],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-alr-task-common
        [breadcrumb]="true"
        heading="Upload the latest activity level report file (optional)"
        caption="Provide the latest activity level report">
        <app-alr-activity-summary-template
          [isEditable]="vm.isEditable"
          [alrFile]="vm.alrFile"
          [files]="vm.files"
          [changeLink]="['..', 'upload-latest-activity']"></app-alr-activity-summary-template>

        <div class="govuk-button-group">
          <button *ngIf="vm.isEditable" (click)="onContinue()" govukButton title="Continue" type="button">
            Continue
          </button>
        </div>
      </app-alr-task-common>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrLatestActivityComponent {
  isEditable = this.alrService.isEditable;
  payload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.payload();
    const determination = payload.regulatorReviewOutcome.determination as ALRClosedDetermination;
    const isOperatorsAlrFile = determination.alrFile === payload.alr?.alrFile;
    const alrFile = determination.alrFile
      ? isOperatorsAlrFile
        ? this.alrService.getOperatorDownloadUrlAlrFile(determination.alrFile)
        : this.alrService.getRegulatorDownloadUrlAlrFile(determination.alrFile)
      : this.alrService.getOperatorDownloadUrlAlrFile(payload.alr?.alrFile);
    const files = determination.files
      ? this.alrService.getRegulatorDownloadUrlFiles(determination.files)
      : this.alrService.getOperatorDownloadUrlFiles(payload.alr?.files);

    return { isEditable, alrFile, files };
  });

  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue() {
    this.router.navigate(['../summary'], { relativeTo: this.route });
  }
}
