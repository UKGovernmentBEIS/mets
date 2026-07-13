import {
  ChangeDetectorRef,
  ComponentRef,
  Directive,
  DoCheck,
  ElementRef,
  EmbeddedViewRef,
  inject,
  input,
  OnDestroy,
  OnInit,
  Renderer2,
  ViewContainerRef,
} from '@angular/core';
import { ControlContainer, FormGroupDirective, NgControl, NgForm } from '@angular/forms';

import { BehaviorSubject, combineLatest, startWith, Subject, takeUntil, tap } from 'rxjs';

import { ErrorMessageComponent } from '../../error-message';

@Directive({ selector: '[govukFormError]', host: { '[attr.id]': 'identifier' } })
export class FormErrorDirective implements OnDestroy, OnInit, DoCheck {
  private readonly formControl = inject(NgControl, { self: true });
  private readonly elementRef = inject(ElementRef);
  private readonly viewContainer = inject(ViewContainerRef);
  private readonly renderer = inject(Renderer2);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  private readonly container = inject(ControlContainer, { optional: true });

  readonly id = input<string>();

  private readonly destroy$ = new Subject<void>();
  private touch$ = new BehaviorSubject(false);
  private errorComponent: ComponentRef<ErrorMessageComponent>;
  private isSubmitted = false;

  get identifier(): string {
    return this.id();
  }

  private get nativeElement(): HTMLElement {
    return this.elementRef.nativeElement;
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.container &&
      (this.container.formDirective instanceof FormGroupDirective || this.container.formDirective instanceof NgForm)
      ? this.container.formDirective
      : null;
  }

  ngOnInit(): void {
    combineLatest([
      this.formControl.statusChanges.pipe(startWith(this.formControl.status)),
      this.formControl.control.updateOn === 'submit'
        ? this.form?.ngSubmit.pipe(tap(() => (this.isSubmitted = this.formControl.status !== 'DISABLED')))
        : this.touch$,
    ])
      .pipe(takeUntil(this.destroy$))
      .subscribe(([status]) => this.handleErrorStatus(status));
  }

  ngDoCheck(): void {
    if (this.touch$.getValue() !== this.formControl.touched) {
      this.touch$.next(this.formControl.touched);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.unsubscribe();
  }

  private attachErrorComponent() {
    this.errorComponent = this.viewContainer.createComponent(ErrorMessageComponent);
    this.errorComponent.instance.identifier.set(this.id());

    const htmlElement: HTMLElement = (this.errorComponent.hostView as EmbeddedViewRef<ErrorMessageComponent>)
      .rootNodes[0];

    this.renderer.insertBefore(this.nativeElement.parentNode, htmlElement, this.nativeElement);
  }

  private detachErrorComponent() {
    if (this.errorComponent) {
      this.errorComponent.destroy();
      this.errorComponent = null;
    }
  }

  private handleErrorStatus(status: string) {
    const parentFormGroup: HTMLElement = this.nativeElement.closest('.govuk-form-group');

    if (status === 'INVALID' && this.shouldDisplayErrors()) {
      if (parentFormGroup) {
        this.renderer.addClass(parentFormGroup, 'govuk-form-group--error');

        if (!this.errorComponent) {
          this.attachErrorComponent();
        }

        this.errorComponent.setInput('errors', this.formControl.errors);
      }

      this.renderer.addClass(this.nativeElement, this.getErrorClass());
    } else {
      if (parentFormGroup) {
        this.renderer.removeClass(parentFormGroup, 'govuk-form-group--error');
        this.detachErrorComponent();
      }

      this.renderer.removeClass(this.nativeElement, this.getErrorClass());
    }

    this.changeDetectorRef.markForCheck();
  }

  private shouldDisplayErrors(): boolean {
    return this.formControl.invalid && (!this.form || this.isSubmitted);
  }

  private getErrorClass(): string {
    switch (this.nativeElement.tagName) {
      case 'SELECT':
        return 'govuk-select--error';
      case 'TEXTAREA':
        return 'govuk-textarea--error';
      default:
        return 'govuk-input--error';
    }
  }
}
