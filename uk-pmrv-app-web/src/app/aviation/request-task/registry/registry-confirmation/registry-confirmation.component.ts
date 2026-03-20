import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BaseSuccessComponent } from '@shared/base-success/base-success.component';
import { SharedModule } from '@shared/shared.module';

export interface ConfirmationViewModel {
  requestId: string;
}

@Component({
  selector: 'app-registry-confirmation',
  imports: [SharedModule, ReturnToLinkComponent],
  templateUrl: './registry-confirmation.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegistryConfirmationComponent extends BaseSuccessComponent {
  constructor() {
    super();
  }
}
