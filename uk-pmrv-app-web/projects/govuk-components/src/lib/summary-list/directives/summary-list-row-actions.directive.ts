import { contentChildren, Directive, effect, ElementRef, inject, Renderer2 } from '@angular/core';

import { LinkDirective } from '../../directives';

@Directive({
  selector: 'dd[govukSummaryListRowActions]',
  host: { '[class]': 'className' },
})
export class SummaryListRowActionsDirective {
  private readonly el = inject(ElementRef);
  private readonly renderer = inject(Renderer2);

  readonly links = contentChildren(LinkDirective, { descendants: true, read: ElementRef });

  className = 'govuk-summary-list__actions';

  constructor() {
    effect(() => {
      const linkElements = this.links();

      if (linkElements.length > 0) {
        this.rebuildActionList(linkElements.map((ref) => ref.nativeElement));
      }
    });
  }

  /**
   * Automatically append a list of 'govuk-summary-list__actions-list'
   * and list elements 'govuk-summary-list__actions-list-item'
   * whenever 'a[govukLink]' is used within 'dd[govukSummaryListRowActions]'
   */
  private rebuildActionList(linkElements: HTMLElement[]) {
    const container = this.el.nativeElement;

    const ul = this.renderer.createElement('ul');
    this.renderer.addClass(ul, 'govuk-summary-list__actions-list');

    linkElements.forEach((link) => {
      const li = this.renderer.createElement('li');
      this.renderer.addClass(li, 'govuk-summary-list__actions-list-item');
      this.renderer.appendChild(li, link);
      this.renderer.appendChild(ul, li);
    });

    this.renderer.setProperty(container, 'innerHTML', '');
    this.renderer.appendChild(container, ul);
  }
}
