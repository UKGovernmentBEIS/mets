import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { DoalProceedToAuthorityDetermination } from 'pmrv-api';

import { alrArticleReasonItemsLabelsMap } from '../alr-determination-proceed-authority.label.map';

@Component({
  selector: 'app-alr-determination-proceed-authority-summary-template',
  imports: [SharedModule, RouterLink],
  templateUrl: './determination-proceed-authority-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationProceedAuthoritySummaryTemplateComponent {
  alrArticleReasonItemsLabelsMap = alrArticleReasonItemsLabelsMap;
  @Input() determination: DoalProceedToAuthorityDetermination;
  @Input() editable: boolean;
}
