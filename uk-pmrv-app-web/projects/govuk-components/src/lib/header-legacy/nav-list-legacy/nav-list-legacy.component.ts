import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'govuk-header-nav-list-legacy',
  templateUrl: './nav-list-legacy.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavListLegacyComponent {
  readonly identifier = input('navigation');
  readonly ariaLabel = input('Navigation menu');
  readonly menuButtonAriaLabel = input('Show or hide navigation menu');

  isNavigationOpen = false;
}
