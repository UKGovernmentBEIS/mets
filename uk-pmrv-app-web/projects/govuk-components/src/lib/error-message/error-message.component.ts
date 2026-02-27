import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { MessageValidationErrors } from './message-validation-errors';

@Component({
  selector: 'govuk-error-message',
  standalone: false,
  templateUrl: './error-message.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorMessageComponent {
  @Input() identifier: string;
  @Input() errors: MessageValidationErrors;
}
