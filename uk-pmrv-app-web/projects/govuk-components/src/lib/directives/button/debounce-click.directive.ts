import { Directive, input, OnDestroy, OnInit, output } from '@angular/core';

import { debounceTime, Subject, Subscription } from 'rxjs';

@Directive({
  selector: 'button[govukDebounceClick]',
  host: { '(click)': 'onClick($event)' },
})
export class DebounceClickDirective implements OnInit, OnDestroy {
  readonly debounceTime = input(500);
  readonly debounceClick = output<MouseEvent>();
  private subscription = new Subscription();
  private clicks = new Subject<MouseEvent>();

  onClick(event: MouseEvent): void {
    this.clicks.next(event);
  }

  ngOnInit(): void {
    this.subscription = this.clicks
      .pipe(debounceTime(this.debounceTime()))
      .subscribe((e: MouseEvent) => this.debounceClick.emit(e));
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
