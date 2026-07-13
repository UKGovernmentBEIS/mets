import { ChangeDetectionStrategy, Component, input, OnInit } from '@angular/core';

import { GovukSpacingUnit } from '../types';

@Component({
  selector: 'govuk-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent implements OnInit {
  readonly summary = input.required<string>();
  readonly bottomSpacing = input<GovukSpacingUnit>(6);
  readonly bottomContentPadding = input<GovukSpacingUnit>(3);
  readonly openOnInit = input<boolean>(false);
  isOpen: boolean;

  ngOnInit(): void {
    this.isOpen = this.openOnInit();
  }

  onToggle(event: Event) {
    this.isOpen = (event as ToggleEvent).newState === 'open';
  }
}
