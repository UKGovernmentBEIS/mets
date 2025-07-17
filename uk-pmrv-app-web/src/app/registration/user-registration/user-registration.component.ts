import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-user-registration',
  template: `
    <router-outlet appSkipLinkFocus></router-outlet>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserRegistrationComponent {}
