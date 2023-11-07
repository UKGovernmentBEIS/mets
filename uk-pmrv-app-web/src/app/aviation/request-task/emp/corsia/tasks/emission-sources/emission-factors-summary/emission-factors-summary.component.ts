import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { EmissionFactorsSummaryTemplateComponent } from '@aviation/shared/components/emp/emission-sources/emission-factors-summary-template/emission-factors-summary-template.component';
import { SharedModule } from '@shared/shared.module';


@Component({
  selector: 'app-emission-factors-summary',
  template: `
    <div>
      <app-emission-factors-summary-template
        [fuelTypes]="fuelTypes"
        [isEditable]="editable"
        [changeUrlQueryParams]="{ change: true }"
      ></app-emission-factors-summary-template>
    </div>
  `,
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterModule, SharedModule, EmissionFactorsSummaryTemplateComponent],
})
export class EmissionFactorsSummaryComponent {
  @Input() editable = true;
  @Input() fuelTypes: { id: string; key: string; value: string }[];
}
