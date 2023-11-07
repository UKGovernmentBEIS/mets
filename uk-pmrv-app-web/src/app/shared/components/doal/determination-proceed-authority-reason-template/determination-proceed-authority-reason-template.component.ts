import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { DoalProceedToAuthorityDetermination } from 'pmrv-api';

import { articleReasonItemsLabelsMap } from '../determination-proceed-authority.label.map';

@Component({
  selector: 'app-determination-proceed-authority-reason-template',
  templateUrl: './determination-proceed-authority-reason-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationProceedAuthorityReasonTemplateComponent {
  articleReasonItemsLabelsMap = articleReasonItemsLabelsMap;

  @Input() determination: DoalProceedToAuthorityDetermination;
  @Input() editable: boolean;
}
