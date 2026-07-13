import { contentChildren, Directive, forwardRef } from '@angular/core';
import { AbstractControl, AbstractControlDirective, ControlContainer, NgControl } from '@angular/forms';

@Directive({
  selector: '[govukConditionalContent]',
})
export class ConditionalContentDirective {
  private readonly childControls = contentChildren(NgControl, { descendants: true });
  private readonly childContainers = contentChildren(ControlContainer, { descendants: true });

  private readonly childConditionals = contentChildren(
    forwardRef(() => ConditionalContentDirective),
    { descendants: true },
  );

  private get childControlsAndContainers(): AbstractControlDirective[] {
    return [...this.childControls(), ...this.childContainers()];
  }

  enableControls(): void {
    // There seems to be an extreme case in content projection that it detects itself
    const nestedControls: AbstractControl[] = [].concat(
      ...this.childConditionals()
        .filter((conditional) => conditional !== this)
        .map((conditional) => conditional.childControlsAndContainers.map(({ control }) => control)),
    );

    const nestedStatuses = nestedControls.map((nested) => nested.status);

    this.childControlsAndContainers
      .filter(({ control }) => !nestedControls.includes(control))
      .forEach(({ control }) => {
        if (control) control.enable({ emitEvent: this.shouldEmitEvent(control) });
      });

    nestedControls
      .filter((control, index) => nestedStatuses[index] === 'DISABLED' && !control.value)
      .forEach((control) => control.disable({ emitEvent: this.shouldEmitEvent(control) }));
  }

  disableControls(): void {
    this.childControlsAndContainers.forEach(({ control }) => {
      if (control) control.disable({ emitEvent: this.shouldEmitEvent(control) });
    });
  }

  private shouldEmitEvent(control: AbstractControl): boolean {
    return control.updateOn !== 'submit';
  }
}
