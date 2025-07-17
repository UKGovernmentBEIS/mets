import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-aer-container',
  template: `
    <router-outlet></router-outlet>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerComponent {}
