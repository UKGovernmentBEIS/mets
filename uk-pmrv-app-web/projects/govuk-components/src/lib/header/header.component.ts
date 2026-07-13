import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'govuk-header',
  imports: [RouterLink, NgTemplateOutlet],
  templateUrl: './header.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {
  readonly homepageUrl = input<string>('https://www.gov.uk');
  readonly productName = input<string>();

  readonly isExternal = computed(() => {
    const url = this.homepageUrl();
    return url.startsWith('http') || url.startsWith('https');
  });
}
