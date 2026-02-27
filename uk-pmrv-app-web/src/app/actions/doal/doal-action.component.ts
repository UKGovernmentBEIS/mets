import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-doal-action-container',
  standalone: false,
  template: `
    <router-outlet></router-outlet>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DoalActionComponent {}
