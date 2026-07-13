import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'govuk-phase-banner',
  templateUrl: './phase-banner.component.html',
  styles: `
    .align-right {
      display: flex;
      justify-content: space-between;
    }
    .order-2 {
      order: 2;
    }
    .govuk-phase-banner__text {
      width: 100%;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PhaseBannerComponent {
  readonly phase = input<string>();
  readonly tagColor = input<string>();
  readonly tagAlign = input<'right' | 'left'>('left');
}
