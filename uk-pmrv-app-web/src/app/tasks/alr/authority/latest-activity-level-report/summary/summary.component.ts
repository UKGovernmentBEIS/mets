import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ActivitySummaryTemplateComponent } from '@shared/components/alr';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { authorityActivityComplete } from '@tasks/alr/utils';

import { ALRAuthorityResponseSubmitRequestTaskPayload } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  alrFile: AttachedFile;
  files: AttachedFile[];
  isSubmitDisplayed: boolean;
}

@Component({
  selector: 'app-alr-authority-upload-letest-alr-summary',
  imports: [SharedModule, AlrTaskSharedModule, ActivitySummaryTemplateComponent],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-alr-task-common
        [breadcrumb]="true"
        returnLink="../../"
        heading="Check your answers"
        caption="Provide the activity level report">
        <app-alr-activity-summary-template
          [isEditable]="vm.isEditable"
          [alrFile]="vm.alrFile"
          [files]="vm.files"
          [changeLink]="['..', 'upload-latest-activity']"></app-alr-activity-summary-template>

        <div class="govuk-button-group" *ngIf="vm.isSubmitDisplayed">
          <button (click)="onConfirm()" appPendingButton govukButton type="button">Confirm and complete</button>
        </div>
      </app-alr-task-common>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAuthorityuploadLatestAlrSummaryComponent {
  isEditable = this.alrService.isEditable;
  payload = this.alrService.payload as Signal<ALRAuthorityResponseSubmitRequestTaskPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.payload();
    const determinationSectionsCompleted = authorityActivityComplete(payload);
    const alr = payload.authorityReviewOutcome.alr;
    const alrFile = this.alrService.getRegulatorDownloadUrlAlrFile(alr.alrFile);
    const files = this.alrService.getRegulatorDownloadUrlFiles(alr?.files);

    return { isEditable, alrFile, files, isSubmitDisplayed: !determinationSectionsCompleted && isEditable };
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    this.alrService
      .postAlrAuthority({}, 'upload', true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
  }
}
