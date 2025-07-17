import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-emp-payment-not-completed',
  standalone: true,
  imports: [SharedModule],
  template: `
    <app-payment-not-completed></app-payment-not-completed>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentNotCompletedComponent implements OnInit, OnDestroy {
  constructor(private readonly backLinkService: BackLinkService) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
}
