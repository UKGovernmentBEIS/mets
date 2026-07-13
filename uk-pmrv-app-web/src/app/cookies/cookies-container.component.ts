import { ChangeDetectionStrategy, Component } from '@angular/core';

import { toggleAnalytics } from '@core/analytics';

import { CookiesService } from './cookies.service';

@Component({
  selector: 'app-cookies-container',
  standalone: false,
  template: `
    <govuk-cookies-pop-up
      cookiesExpirationTime="1"
      [areBrowserCookiesEnabled]="cookiesEnabled"
      [cookiesAccepted]="cookiesAccepted$ | async"
      (cookiesRejectedEmitter)="rejectCookies($event)"
      (cookiesAcceptedEmitter)="acceptCookies($event)" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CookiesContainerComponent {
  constructor(private cookiesService: CookiesService) {}
  cookiesEnabled = this.cookiesService.cookiesEnabled();
  cookiesAccepted$ = this.cookiesService.accepted$;

  acceptCookies(expired: string) {
    this.cookiesService.acceptAllCookies(+expired);
    toggleAnalytics(true);
  }

  rejectCookies(expired: string) {
    this.cookiesService.rejectAllCookies(+expired);
  }
}
