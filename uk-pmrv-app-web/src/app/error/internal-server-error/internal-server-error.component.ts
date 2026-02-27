import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { GenericServiceErrorCode } from '@error/service-errors';

@Component({
  selector: 'app-internal-server-error',
  standalone: false,
  templateUrl: './internal-server-error.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InternalServerErrorComponent {
  errorCode = this.router.getCurrentNavigation().extras?.state?.errorCode;
  genericServiceErrorCode = GenericServiceErrorCode;

  constructor(private readonly router: Router) {}
}
