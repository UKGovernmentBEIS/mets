import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AccreditationReferenceDocumentNamePipe } from '@aviation/request-task/aer/ukets/aer-verify/tasks/materiality-level/pipes/accreditation-reference-document-name.pipe';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerMaterialityLevel } from 'pmrv-api';

@Component({
  selector: 'app-aer-verify-materiality-level-group',
  templateUrl: './materiality-level-group.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, RouterModule, AccreditationReferenceDocumentNamePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerVerifyMaterialityLevelGroupComponent {
  @Input() isEditable = false;
  @Input() materialityLevel: AviationAerMaterialityLevel;
  @Input() queryParams = {};
}
