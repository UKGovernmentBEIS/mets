import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SummaryTemplateComponent } from '@shared/components/waste-qdr/summary-template/summary-template.component';
import { QuarterNamePipe } from '@shared/pipes/quarter-name.pipe';
import { AttachedFile } from '@shared/types/attached-file.type';

import { WasteQDRApplicationSubmittedRequestActionPayload, WasteQDRRequestMetaData } from 'pmrv-api';

import { WasteQdrActionService } from '../core/waste-qdr.service';

interface ViewModel {
  header: string;
  quartelyTitle: string;
  qdrReport: AttachedFile | null;
  supportingFiles: AttachedFile[];
  reportProvided: boolean;
  notes?: string;
  reasonForUnprovided?: string;
}

@Component({
  selector: 'app-waste-qdr-action-submitted',
  standalone: true,
  imports: [ActionSharedModule, NgIf, SummaryTemplateComponent],
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteQdrSubmittedComponent {
  payload = this.wasteQdrActionService?.payload as Signal<WasteQDRApplicationSubmittedRequestActionPayload | undefined>;
  reportProvided = computed(() => this.payload().qdr.reportProvided);
  requestAction = this.wasteQdrActionService.requestAction();
  private readonly quarterNamePipe = new QuarterNamePipe();

  requestActionType = this.wasteQdrActionService.requestActionType;

  vm: Signal<ViewModel> = computed(() => {
    const [, year, quarter] = this.requestAction?.requestId?.split('-') || [];
    const quartelyTitle = `Will you provide a quarterly data report for ${this.quarterNamePipe.transform(quarter as WasteQDRRequestMetaData['quarter'])} ${year}`;

    const header = 'Quarterly data report submitted to regulator';
    const wasteQdr = this.payload().qdr;
    const reportProvided = this.reportProvided();

    return {
      header,
      quartelyTitle,
      qdrReport:
        wasteQdr?.reportProvided && wasteQdr?.report
          ? this.wasteQdrActionService.getOperatorDownloadUrlFiles([wasteQdr?.report])[0]
          : null,
      supportingFiles: wasteQdr?.supportingFiles
        ? this.wasteQdrActionService.getOperatorDownloadUrlFiles(wasteQdr?.supportingFiles)
        : [],
      reportProvided,
      notes: wasteQdr?.notes,
      reasonForUnprovided: wasteQdr?.reasonForUnprovided,
    };
  });

  constructor(private readonly wasteQdrActionService: WasteQdrActionService) {}
}
