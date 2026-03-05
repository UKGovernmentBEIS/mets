import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-inspection-action-container',
  template: `
    <router-outlet></router-outlet>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InspectionActionComponent {}
