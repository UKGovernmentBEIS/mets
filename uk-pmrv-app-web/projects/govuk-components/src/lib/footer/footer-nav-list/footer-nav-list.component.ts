import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'govuk-footer-nav-list',
  templateUrl: './footer-nav-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FooterNavListComponent {
  readonly title = input<string>();
  readonly columns = input<1 | 2>();
}
