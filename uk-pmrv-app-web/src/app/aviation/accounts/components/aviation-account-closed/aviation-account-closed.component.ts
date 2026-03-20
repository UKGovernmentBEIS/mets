import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AviationAccountDetails } from '../../store';

@Component({
  selector: 'app-aviation-account-closed',
  standalone: false,
  templateUrl: './aviation-account-closed.component.html',
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AviationAccountClosedComponent {
  @Input() accountInfo: AviationAccountDetails;
}
