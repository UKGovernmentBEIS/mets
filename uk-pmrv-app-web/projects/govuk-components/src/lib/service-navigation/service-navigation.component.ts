import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink, RouterLinkActive } from '@angular/router';

import { debounceTime, distinctUntilChanged, fromEvent, map } from 'rxjs';

import { NavigationItem } from './navigation-item.interface';

@Component({
  selector: 'govuk-service-navigation',
  imports: [RouterLink, RouterLinkActive, NgTemplateOutlet],
  templateUrl: './service-navigation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ServiceNavigationComponent {
  readonly serviceName = input<string>();
  readonly serviceUrl = input<string>();
  readonly navigationItems = input<NavigationItem[]>();
  readonly ariaLabel = input<string>('Service information');
  readonly menuButtonText = input<string>('Menu');
  readonly menuButtonLabel = input<string>('Menu');
  readonly navigationId = input<string>('navigation');
  readonly navigationLabel = input<string>('Menu');
  readonly collapseNavigationOnMobile = input<boolean>(true);

  private readonly breakpoint = '(min-width: 641px)';

  readonly isMenuOpen = signal(false);
  readonly isDesktop = toSignal(
    fromEvent(window, 'resize').pipe(
      map(() => window.matchMedia(this.breakpoint).matches),
      debounceTime(20),
      distinctUntilChanged(),
    ),
    { initialValue: window.matchMedia(this.breakpoint).matches },
  );

  toggleMenu() {
    this.isMenuOpen.update(() => !this.isMenuOpen());
  }
}
