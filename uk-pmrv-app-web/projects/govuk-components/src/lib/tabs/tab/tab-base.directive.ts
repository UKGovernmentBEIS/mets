import { ChangeDetectorRef, Directive, inject, input, OnChanges, SimpleChanges, TemplateRef } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

@Directive()
export abstract class TabBaseDirective implements OnChanges {
  public readonly cdRef: ChangeDetectorRef = inject(ChangeDetectorRef);
  public readonly templateRef: TemplateRef<void> = inject(TemplateRef);

  readonly id = input<string>();
  readonly label = input<string>();

  isSelected = new BehaviorSubject<boolean>(false);

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  ngOnChanges(changes: SimpleChanges): void {
    this.isSelected.next(this.isSelected.getValue());
  }
}
