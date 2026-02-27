import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-aer-verifier-returned-template',
  imports: [SharedModule],
  templateUrl: './aer-verifier-returned-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerVerifierReturnedTemplateComponent {
  @Input() changesRequired: string;
  @Input() isEditable: boolean;
}
