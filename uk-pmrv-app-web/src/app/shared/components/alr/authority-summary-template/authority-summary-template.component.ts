import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { ALRAuthorityResponse, ALRPreliminaryAllocation } from 'pmrv-api';

import { AllocationListTemplateComponent } from '../allocation-list-template/allocation-list-template.component';

export interface AlrSummaryAuthorityResponse extends ALRAuthorityResponse {
  preliminaryAllocations?: Array<ALRPreliminaryAllocation>;
  totalAllocationsPerYear?: { [key: string]: number };
  documents?: Array<string>;
  decisionNotice?: string;
}

@Component({
  selector: 'app-alr-authority-summary-template',
  templateUrl: './authority-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink, AllocationListTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAuthoritySummaryTemplateComponent {
  @Input() data: AlrSummaryAuthorityResponse;
  @Input() documents: AttachedFile[];
  @Input() editable: boolean;
}
