import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { Observable } from 'rxjs';

import { RequestInfoDTO, RequestTaskDTO } from 'pmrv-api';

@Component({
  selector: 'app-make-payment-help',
  templateUrl: './make-payment-help.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MakePaymentHelpComponent {
  @Input() competentAuthority$: Observable<RequestInfoDTO['competentAuthority']>;
  @Input() requestType$: Observable<RequestInfoDTO['type']>;
  @Input() requestTaskType$: Observable<RequestTaskDTO['type']>;

  default: string;
  @Input() set defaultHelp(defaultHelp: string) {
    this.default = defaultHelp;
  }
}
