import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { UncorrectedNonConformities } from 'pmrv-api';

@Component({
  selector: 'app-non-conformities-group',
  template: `
    <app-non-conformities-per-plan-group
      [isEditable]="isEditable"
      [uncorrectedNonConformities]="uncorrectedNonConformities"
      baseLink=".."
    ></app-non-conformities-per-plan-group>
    <app-non-conformities-previous-year-group
      [isEditable]="isEditable"
      [uncorrectedNonConformities]="uncorrectedNonConformities"
      baseLink="../previous-year"
    ></app-non-conformities-previous-year-group>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonConformitiesGroupComponent {
  @Input() isEditable = false;
  @Input() uncorrectedNonConformities: UncorrectedNonConformities;
}
