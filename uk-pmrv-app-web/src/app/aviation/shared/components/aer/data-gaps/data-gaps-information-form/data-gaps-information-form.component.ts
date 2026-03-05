import { Component, Input } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-data-gaps-information-form',
  templateUrl: './data-gaps-information-form.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  viewProviders: [existingControlContainer],
})
export class DataGapsInformationFormComponent {
  @Input() isCorsia = false;
}
