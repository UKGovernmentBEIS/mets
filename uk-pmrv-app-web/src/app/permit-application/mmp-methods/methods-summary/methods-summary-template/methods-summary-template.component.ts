import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { PhysicalPartsTableComponent } from '@permit-application/mmp-methods/shared/physical-parts-table/physical-parts-table.component';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-mmp-methods-summary-template',
  standalone: true,
  imports: [PhysicalPartsTableComponent, SharedPermitModule, SharedModule, RouterLink],
  templateUrl: './methods-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MethodsSummaryTemplateComponent {
  @Input() showOriginal = false;
  @Input() hasTwoOrMoreSubInstallationsCompleted: boolean;
  @Input() form: UntypedFormGroup;
  @Input() isEditable: boolean;
  @Input() hasBottomBorder = true;
}
