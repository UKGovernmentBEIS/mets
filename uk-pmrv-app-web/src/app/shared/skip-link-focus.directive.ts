import { DOCUMENT } from '@angular/common';
import { Directive, HostListener, Inject } from '@angular/core';
import { Router } from '@angular/router';

@Directive({
  selector: 'router-outlet[appSkipLinkFocus]',
  standalone: false,
})
export class SkipLinkFocusDirective {
  constructor(
    @Inject(DOCUMENT) private readonly document,
    private readonly router: Router,
  ) {}

  // eslint-disable-next-line @angular-eslint/prefer-host-metadata-property
  @HostListener('activate')
  onRouteActivation(): void {
    if (this.router.getCurrentNavigation()?.trigger !== 'popstate') {
      const target = this.document.querySelector('govuk-skip-link');
      target.tabIndex = 0;
      target.focus({ preventScroll: true });
      target.removeAttribute('tabIndex');
    }
  }
}
