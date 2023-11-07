import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AttachedFile } from '@shared/types/attached-file.type';

import { DoalAuthorityResponse, PreliminaryAllocation } from 'pmrv-api';

export interface DoalSummaryAuthorityResponse extends DoalAuthorityResponse {
  preliminaryAllocations?: Array<PreliminaryAllocation>;
  totalAllocationsPerYear?: { [key: string]: number };
  documents?: Array<string>;
  decisionNotice?: string;
}

@Component({
  selector: 'app-doal-authority-decision-template',
  templateUrl: './authority-decision-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthorityDecisionTemplateComponent {
  @Input() data: DoalSummaryAuthorityResponse;
  @Input() documents: AttachedFile[];
  @Input() editable: boolean;
}
