import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-navigation',
  standalone: false,
  template: `
    <section class="govuk-service-navigation">
      <div class="govuk-service-navigation__container">
        <nav class="govuk-service-navigation__wrapper" [attr.aria-label]="ariaLabel">
          <ul class="govuk-service-navigation__list">
            <ng-content></ng-content>
          </ul>
        </nav>
      </div>
    </section>
  `,
  styles: `
    .govuk-service-navigation {
      width: 100%;
      margin: 0;
      float: left;
    }

    .govuk-service-navigation__container {
      margin: 0;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavigationComponent {
  @Input() ariaLabel = 'Primary navigation';
}
