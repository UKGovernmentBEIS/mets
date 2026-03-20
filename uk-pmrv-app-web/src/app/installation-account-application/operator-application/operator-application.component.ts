import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-operator-application',
  standalone: false,
  template: `
    <router-outlet></router-outlet>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorApplicationComponent {}
