import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { WasteQdrService } from '@tasks/waste-qdr/core';
import { WasteQdrTaskComponent } from '@tasks/waste-qdr/shared';

import { WasteQDR, WasteQDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  hideSubmit: boolean;
  qdr: WasteQDR;
  reportFile: AttachedFile;
  supportingFiles: AttachedFile[];
}

@Component({
  selector: 'app-waste-qdr-summary',
  standalone: true,
  imports: [RouterLink, SharedModule, WasteQdrTaskComponent],
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteQdrSummaryComponent {
  isEditable = this.wasteQdrService.isEditable;
  payload = this.wasteQdrService.payload as Signal<WasteQDRApplicationSubmitRequestTaskPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.payload();
    const qdr = payload.qdr;
    const hideSubmit = !isEditable || payload.wasteQDRSectionsCompleted['qdr'];
    const reportFile = this.wasteQdrService.getDownloadUrlFile(qdr.report);
    const supportingFiles = this.wasteQdrService.getDownloadUrlFiles(qdr.supportingFiles);

    return { isEditable, hideSubmit, qdr, reportFile, supportingFiles };
  });

  constructor(
    private readonly wasteQdrService: WasteQdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit() {
    this.wasteQdrService
      .postTaskSave({}, {}, true, 'qdr')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
  }
}
