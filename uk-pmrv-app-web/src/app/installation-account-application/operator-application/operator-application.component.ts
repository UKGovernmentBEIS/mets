import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-operator-application',
  template: ` <router-outlet appSkipLinkFocus></router-outlet> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorApplicationComponent {}
