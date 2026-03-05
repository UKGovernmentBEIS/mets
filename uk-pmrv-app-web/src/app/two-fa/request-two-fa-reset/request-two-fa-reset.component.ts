import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BackLinkService } from '@shared/back-link/back-link.service';

@Component({
  selector: 'app-request-two-fa-reset',
  templateUrl: './request-two-fa-reset.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestTwoFaResetComponent implements OnInit {
  constructor(private readonly backLinkService: BackLinkService) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
