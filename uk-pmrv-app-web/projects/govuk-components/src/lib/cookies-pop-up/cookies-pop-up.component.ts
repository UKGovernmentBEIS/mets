import { APP_BASE_HREF } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input, InputSignal, output, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'govuk-cookies-pop-up',
  imports: [RouterLink],
  templateUrl: './cookies-pop-up.component.html',
  styleUrl: './cookies-pop-up.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CookiesPopUpComponent {
  private readonly baseHref = inject<string>(APP_BASE_HREF, { optional: true });
  readonly cookieRejected = signal(false);
  readonly cookiesExpirationTime: InputSignal<string> = input<string>();
  readonly cookiesAccepted: InputSignal<boolean> = input<boolean>();
  readonly areBrowserCookiesEnabled: InputSignal<boolean> = input<boolean>();
  readonly cookiesAcceptedEmitter = output<string>();
  readonly cookiesRejectedEmitter = output<string>();

  show = false;

  cookiesNotAccepted() {
    return this.cookiesAccepted() === false;
  }

  acceptCookies() {
    this.show = true;
    this.cookiesAcceptedEmitter.emit(this.cookiesExpirationTime());
  }

  rejectCookies() {
    this.show = true;
    this.cookieRejected.set(true);
    this.cookiesRejectedEmitter.emit(this.cookiesExpirationTime());
  }

  hideCookieMessage() {
    this.show = false;
  }

  goToSetPreferences() {
    location.href = new URL(
      'cookies',
      this.baseHref ? new URL(this.baseHref, location.origin) : location.origin,
    ).toString();
  }
}
