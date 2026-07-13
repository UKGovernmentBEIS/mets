import { KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, model } from '@angular/core';

import { MessageValidationErrors } from './message-validation-errors';

@Component({
  selector: 'govuk-error-message',
  imports: [KeyValuePipe],
  templateUrl: './error-message.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorMessageComponent {
  readonly identifier = model<string>();
  readonly errors = model<MessageValidationErrors>();
}
