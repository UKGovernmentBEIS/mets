import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRClosedDetermination, DoalProceedToAuthorityDetermination } from 'pmrv-api';

import { DeterminationCloseSummaryTemplateComponent } from '../determination-close-summary-template/determination-close-summary-template.component';
import { DeterminationProceedAuthoritySummaryTemplateComponent } from '../determination-proceed-authority-summary-template/determination-proceed-authority-summary-template.component';

@Component({
  selector: 'app-alr-determination-summary-template',
  templateUrl: './determination-summary-template.component.html',
  standalone: true,
  imports: [
    SharedModule,
    AlrTaskSharedModule,
    DeterminationProceedAuthoritySummaryTemplateComponent,
    DeterminationCloseSummaryTemplateComponent,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrDeterminationSummaryTemplateComponent {
  @Input() determination: DoalProceedToAuthorityDetermination | ALRClosedDetermination;
  @Input() editable: boolean;
  @Input() alrFile: AttachedFile;
  @Input() files: AttachedFile[];
}
