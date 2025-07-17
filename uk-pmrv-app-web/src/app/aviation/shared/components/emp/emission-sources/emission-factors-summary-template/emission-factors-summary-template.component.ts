import { NgForOf, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-emission-factors-summary-template',
  standalone: true,
  imports: [GovukComponentsModule, RouterLinkWithHref, NgForOf, NgIf],
  template: `
    <h2 class="govuk-heading-m">Emission factors to be applied</h2>
    <dl govuk-summary-list>
      <div govukSummaryListRow *ngFor="let ft of fuelTypes">
        <dt govukSummaryListRowKey>{{ ft.key }}</dt>
        <dd govukSummaryListRowValue class="pre-wrap">{{ ft.value }}</dd>
        <dd govukSummaryListRowActions>
          <a
            govukLink
            *ngIf="ft.id === 'OTHER' && isEditable"
            [routerLink]="['../other-fuel']"
            [queryParams]="changeUrlQueryParams">
            Change
          </a>
        </dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionFactorsSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() fuelTypes: { id: string; key: string; value: string }[];
  @Input() changeUrlQueryParams: Params = {};
}
