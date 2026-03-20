import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-user-registration',
  standalone: false,
  template: `
    <router-outlet></router-outlet>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserRegistrationComponent {}
