import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { HseTiActionService } from '@actions/hseti/core/hseti.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { AuthStore, selectUserRoleType, UserState } from '@core/store';
import { DetailsSummaryTemplateComponent } from '@shared/components/hseti/details-summary-template/details-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  HSETIApplicationSubmittedRequestActionPayload,
  HSETICompletedRequestActionPayload,
  HSETIRegulatorReviewDecision,
} from 'pmrv-api';

interface ViewModel {
  header: string;
  hseti: HSETIApplicationSubmittedRequestActionPayload['hseti'];
  regulatorData: HSETIRegulatorReviewDecision;
  hsetiFile: AttachedFile;
  files: AttachedFile[];
  roleType: UserState['roleType'];
}

@Component({
  selector: 'app-hseti-details-submitted',
  standalone: true,
  imports: [ActionSharedModule, DetailsSummaryTemplateComponent, NgIf, SharedModule],
  templateUrl: './details-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HsetiDetailsSubmittedComponent {
  payload = this.hsetiActionService?.payload as Signal<HSETIApplicationSubmittedRequestActionPayload>;
  allocationPeriod = this.hsetiActionService?.allocationPeriod;
  roleType = toSignal(this.authStore.pipe(selectUserRoleType));

  vm: Signal<ViewModel> = computed(() => {
    const hseti = this.payload().hseti;
    const regulatorData = (this.payload() as HSETICompletedRequestActionPayload)?.regulatorReviewGroupDecisions?.HSETI;
    const header = `${this.allocationPeriod()} HSE target increase details`;
    const roleType = this.roleType();

    return {
      header,
      hseti,
      regulatorData,
      hsetiFile: hseti?.hsetiFile ? this.hsetiActionService.getOperatorDownloadUrlHseTiFile(hseti?.hsetiFile) : null,
      files: hseti?.files ? this.hsetiActionService.getOperatorDownloadUrlFiles(hseti?.files) : [],
      roleType,
    };
  });

  constructor(
    private readonly hsetiActionService: HseTiActionService,
    private readonly authStore: AuthStore,
  ) {}
}
