import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { EmissionFactorsSummaryTemplateComponent } from '@aviation/shared/components/emp/emission-sources/emission-factors-summary-template/emission-factors-summary-template.component';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-emission-factors-summary',
  imports: [CommonModule, RouterModule, SharedModule, EmissionFactorsSummaryTemplateComponent],
  template: `
    <div>
      <app-emission-factors-summary-template
        [fuelTypes]="fuelTypes"
        [isEditable]="editable"
        [changeUrlQueryParams]="{ change: true }"></app-emission-factors-summary-template>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionFactorsSummaryComponent {
  @Input() editable = true;
  @Input() fuelTypes: { id: string; key: string; value: string }[];
}
