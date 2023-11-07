import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-email-sent',
  templateUrl: './email-sent.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailSentComponent {
  @Input() email: string;
  @Output() readonly retry = new EventEmitter();
}
