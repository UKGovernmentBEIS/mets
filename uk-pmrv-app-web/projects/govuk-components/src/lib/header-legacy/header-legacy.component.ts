import { ChangeDetectionStrategy, Component, ElementRef, input, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'govuk-header-legacy',
  imports: [RouterLink],
  templateUrl: './header-legacy.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderLegacyComponent {
  readonly title = input<string>();
  readonly header = viewChild<ElementRef>('header');
}
