import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'govuk-cookies-pop-up',
  templateUrl: './cookies-pop-up.component.html',
  styleUrls: ['./cookies-pop-up.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CookiesPopUpComponent {
  @Input()
  cookiesExpirationTime: string;
  @Input()
  cookiesAccepted: boolean;
  @Input()
  areBrowserCookiesEnabled: boolean;

  @Output() readonly cookiesAcceptedEmitter = new EventEmitter<string>();

  show = false;

  cookiesNotAccepted() {
    return this.cookiesAccepted === false;
  }

  acceptCookies() {
    this.show = true;
    this.cookiesAcceptedEmitter.emit(this.cookiesExpirationTime);
  }

  hideCookieMessage() {
    this.show = false;
  }

  goToSetPreferences() {
    location.href = '/cookies';
  }
}
