import { Component, ElementRef, EventEmitter, inject, Input, OnInit, Output, Renderer2 } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { LinkDirective } from 'govuk-components';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-summary-header',
  imports: [RouterLink, LinkDirective],
  template: `
    <h2 [class]="class"><ng-content></ng-content></h2>
    @if (changeRoute) {
      <a
        [routerLink]="changeRoute"
        [queryParams]="queryParams"
        (click)="changeClick.emit($event)"
        govukLink
        class="govuk-!-font-size-19 govuk-!-font-weight-regular">
        Change
        @if (changeHiddenText) {
          <span class="govuk-visually-hidden">{{ changeHiddenText }}</span>
        }
      </a>
    }
  `,
  styles: `
    :host {
      display: flex;
      justify-content: space-between;
      align-items: baseline;
    }
  `,
})
export class SummaryHeaderComponent implements OnInit {
  @Input() class = '';
  @Input() changeRoute?: string | any[] | null;
  @Input() queryParams?: Params | null;
  @Input() changeHiddenText?: string | null;
  @Output() readonly changeClick = new EventEmitter<Event>();

  private readonly renderer = inject(Renderer2);
  private readonly elementRef = inject(ElementRef<HTMLElement>);

  ngOnInit(): void {
    this.renderer.removeAttribute(this.elementRef.nativeElement, 'class');
  }
}
