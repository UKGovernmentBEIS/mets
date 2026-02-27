/* eslint-disable @angular-eslint/prefer-host-metadata-property */
import { Directive, HostBinding, Input, OnInit } from '@angular/core';

import { SummaryListComponent } from 'govuk-components';

@Directive({
  selector: '[govuk-summary-list][appGroupedSummaryList]',
  standalone: false,
})
export class GroupedSummaryListDirective implements OnInit {
  @Input() hasBottomBorder = true;

  constructor(private readonly summaryList: SummaryListComponent) {}

  @HostBinding('class.summary-list--edge-border') get edgeBorder() {
    return true;
  }
  @HostBinding('class.summary-list--no-bottom-border') get noBottomBorder() {
    return !this.hasBottomBorder;
  }

  ngOnInit(): void {
    this.summaryList.hasBorders = false;
  }
}
