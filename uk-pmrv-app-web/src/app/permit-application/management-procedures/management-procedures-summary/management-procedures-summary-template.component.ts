import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { map } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { ManagementProceduresDefinitionData } from '../management-procedures.interface';

@Component({
  selector: 'app-management-procedures-summary-template',
  templateUrl: './management-procedures-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresSummaryTemplateComponent {
  @Input() hasBottomBorder = true;
  @Input() taskKey: ManagementProceduresDefinitionData['permitTask'];
  @Input() showOriginal = false;

  permitAttachments$ = this.store.pipe(
    map((state) => {
      return this.showOriginal ? (state as any).originalPermitContainer.permitAttachments : state.permitAttachments;
    }),
  );

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>) {}

  getAttachmentUrl(uuid: string): string[] {
    return [this.store.createBaseFileAttachmentDownloadUrl(), uuid];
  }
}
