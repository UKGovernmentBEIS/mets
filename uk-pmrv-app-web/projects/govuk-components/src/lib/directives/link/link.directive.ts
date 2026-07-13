import { ChangeDetectorRef, Directive, ElementRef, inject, input, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';

import { filter, Subscription } from 'rxjs';

@Directive({
  selector: 'a[govukLink]',
  host: {
    '[class.govuk-link]': 'hasSimpleLink',
    '[class.govuk-breadcrumbs__link]': 'hasBreadcrumbsLink',
    '[class.govuk-footer__link]': 'hasFooterLink',
    '[class.govuk-header-legacy__link]': 'hasHeaderLink',
    '[class.govuk-notification-banner__link]': 'hasNotificationLink',
  },
})
export class LinkDirective implements OnDestroy, OnInit {
  private readonly elementRef = inject(ElementRef);
  private readonly renderer = inject(Renderer2);
  private readonly router = inject(Router);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  private readonly link = inject(RouterLink, { optional: true });
  private readonly linkWithHref = inject(RouterLink, { optional: true });

  readonly navLinkType = input<'header' | 'footer' | 'meta' | 'breadcrumb' | 'notification' | 'summaryAction' | ''>(
    '',
    {
      alias: 'govukLink',
    },
  );
  // eslint-disable-next-line @angular-eslint/no-input-rename
  readonly hiddenText = input<string>('', { alias: 'hidden-text' });

  private isActive = false;
  private subscription: Subscription;
  private liElement: HTMLLIElement;

  get hasSimpleLink() {
    const navLinkType = this.navLinkType();
    return !navLinkType || navLinkType === 'summaryAction';
  }

  get hasBreadcrumbsLink() {
    return this.navLinkType() === 'breadcrumb';
  }

  get hasFooterLink() {
    const navLinkType = this.navLinkType();
    return navLinkType === 'meta' || navLinkType === 'footer';
  }

  get hasHeaderLink() {
    return this.navLinkType() === 'header';
  }

  get hasNotificationLink() {
    return this.navLinkType() === 'notification';
  }

  ngOnInit(): void {
    const element: HTMLElement = this.elementRef.nativeElement;

    if (this.navLinkType()) {
      this.subscription = this.router.events
        .pipe(filter((s) => s instanceof NavigationEnd))
        .subscribe(() => this.update());

      const parentNode = element.parentNode;
      this.liElement = this.renderer.createElement('li');
      this.renderer.addClass(this.liElement, this.getLiClassName());
      this.renderer.insertBefore(parentNode, this.liElement, element);
      this.renderer.appendChild(this.liElement, element);
    }

    const hiddenText = this.hiddenText();
    if (hiddenText) {
      const span = this.renderer.createElement('span');
      this.renderer.addClass(span, 'govuk-visually-hidden');

      const text = this.renderer.createText(` ${hiddenText}`);
      this.renderer.appendChild(span, text);
      this.renderer.appendChild(element, span);
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    if (this.liElement) {
      this.renderer.removeChild(this.liElement.parentElement, this.liElement);
    }
  }

  private getLiClassName(): string {
    switch (this.navLinkType()) {
      case 'meta':
        return 'govuk-footer__inline-list-item';
      case 'footer':
        return 'govuk-footer__list-item';
      case 'header':
        return 'govuk-header-legacy__navigation-item';
      case 'breadcrumb':
        return 'govuk-breadcrumbs__list-item';
      case 'summaryAction':
        return 'govuk-summary-card__action';
      default:
        return null;
    }
  }

  private getActiveLiClassName(): string {
    switch (this.navLinkType()) {
      case 'header':
        return 'govuk-header-legacy__navigation-item--active';
      default:
        return null;
    }
  }

  private isLinkActive(link: RouterLink | RouterLink): boolean {
    return this.router.isActive(link.urlTree, {
      paths: 'subset',
      queryParams: 'subset',
      fragment: 'ignored',
      matrixParams: 'ignored',
    });
  }

  private hasActiveLinks(): boolean {
    return (this.link && this.isLinkActive(this.link)) || (this.linkWithHref && this.isLinkActive(this.linkWithHref));
  }

  private update(): void {
    const activeClass = this.getActiveLiClassName();
    const hasActiveLinks = this.hasActiveLinks();
    if (this.isActive !== hasActiveLinks) {
      this.isActive = hasActiveLinks;

      if (hasActiveLinks) {
        this.renderer.addClass(this.liElement, activeClass);
      } else {
        this.renderer.removeClass(this.liElement, activeClass);
      }

      this.changeDetectorRef.markForCheck();
    }
  }
}
