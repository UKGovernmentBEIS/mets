import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'govuk-phase-banner',
  standalone: false,
  templateUrl: './phase-banner.component.html',
  styles: `
    .align-right {
      display: flex;
      justify-content: space-between;
    }
    .order-2 {
      order: 2;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PhaseBannerComponent {
  @Input() phase: string;
  @Input() tagColor: string;
  @Input() tagAlign: 'right' | 'left' = 'left';
}
