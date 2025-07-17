import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-doal-action-container',
  template: `
    <router-outlet></router-outlet>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DoalActionComponent {}
