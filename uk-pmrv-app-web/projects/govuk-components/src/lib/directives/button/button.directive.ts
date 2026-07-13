import { booleanAttribute, Directive, ElementRef, inject, input } from '@angular/core';

@Directive({
  selector:
    'a[govukButton], a[govukSecondaryButton], a[govukWarnButton], a[govukInverseButton], button[govukButton], button[govukWarnButton], button[govukSecondaryButton], button[govukInverseButton]',
  host: {
    '[attr.aria-disabled]': 'ariaDisabled',
    '[attr.role]': 'roleLink',
    '[class.govuk-button]': 'elementClass',
    '[class.govuk-button--secondary]': 'secondaryButton',
    '[class.govuk-button--warning]': 'warningButton',
    '(keydown)': 'onKeyDown($event)',
    '[attr.disabled]': 'ariaDisabled',
    '[class.govuk-button--disabled]': 'ariaDisabled',
  },
})
export class ButtonDirective {
  private readonly elementRef = inject(ElementRef);

  readonly disabled = input(false, { transform: booleanAttribute });

  get ariaDisabled(): boolean | null {
    return (ButtonDirective.isAnchor(this.nativeElement) || ButtonDirective.isButton(this.nativeElement)) &&
      this.disabled()
      ? true
      : null;
  }

  get roleLink(): string | null {
    return ButtonDirective.isAnchor(this.nativeElement) && this.disabled() ? 'link' : null;
  }

  get elementClass(): boolean {
    return true;
  }

  get secondaryButton(): boolean {
    return this.nativeElement.hasAttribute('govuksecondarybutton');
  }

  get warningButton(): boolean {
    return this.nativeElement.hasAttribute('govukwarnbutton');
  }

  private get nativeElement(): HTMLButtonElement | HTMLAnchorElement {
    return this.elementRef.nativeElement;
  }

  private static isButton(nativeElement: HTMLButtonElement | HTMLAnchorElement): nativeElement is HTMLButtonElement {
    return nativeElement.tagName === 'BUTTON';
  }

  private static isAnchor(nativeElement: HTMLButtonElement | HTMLAnchorElement): nativeElement is HTMLAnchorElement {
    return nativeElement.tagName === 'A';
  }

  onKeyDown(event: Event): void {
    if ((event as KeyboardEvent)?.code === 'Space') {
      event.target.dispatchEvent(new MouseEvent('click'));
    }
  }
}
