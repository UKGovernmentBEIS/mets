import { Component, EventEmitter, HostBinding, Input, Output } from '@angular/core';
import { Params } from '@angular/router';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'h2[app-summary-header]',
  template: `
    <ng-content></ng-content>
    <a
      *ngIf="changeRoute"
      [routerLink]="changeRoute"
      [queryParams]="queryParams"
      (click)="changeClick.emit($event)"
      govukLink
      class="govuk-!-font-size-19 govuk-!-font-weight-regular float-right"
    >
      Change
    </a>
  `,
  styles: [
    `
      .float-right {
        float: right;
      }
    `,
  ],
})
export class SummaryHeaderComponent {
  @Input() changeRoute: string | any[];
  @Input() queryParams?: Params | null;
  @Output() readonly changeClick = new EventEmitter<Event>();

  @HostBinding('class.govuk-clearfix') get clearfix() {
    return true;
  }
}
