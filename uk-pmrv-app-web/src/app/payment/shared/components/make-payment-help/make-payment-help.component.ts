import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-make-payment-help',
  standalone: false,
  templateUrl: './make-payment-help.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MakePaymentHelpComponent {
  default: string;
  @Input() set defaultHelp(defaultHelp: string) {
    this.default = defaultHelp;
  }
}
