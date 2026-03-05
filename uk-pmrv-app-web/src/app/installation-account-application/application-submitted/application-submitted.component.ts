import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BaseSuccessComponent } from '@shared/base-success/base-success.component';

@Component({
  selector: 'app-application-submitted',
  templateUrl: './application-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApplicationSubmittedComponent extends BaseSuccessComponent {}
