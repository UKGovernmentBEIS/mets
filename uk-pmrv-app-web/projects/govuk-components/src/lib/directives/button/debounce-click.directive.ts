import { Directive, EventEmitter, HostListener, Input, OnDestroy, OnInit, Output } from '@angular/core';

import { debounceTime, Subject, Subscription } from 'rxjs';

@Directive({
  selector: 'button[govukDebounceClick]',
  standalone: false,
})
export class DebounceClickDirective implements OnInit, OnDestroy {
  @Input() debounceTime = 500;
  @Output() readonly debounceClick = new EventEmitter<MouseEvent>();
  private subscription = new Subscription();
  private clicks = new Subject<MouseEvent>();

  // eslint-disable-next-line @angular-eslint/prefer-host-metadata-property
  @HostListener('click', ['$event'])
  onClick(event: MouseEvent): void {
    this.clicks.next(event);
  }

  ngOnInit(): void {
    this.subscription = this.clicks
      .pipe(debounceTime(this.debounceTime))
      .subscribe((e: MouseEvent) => this.debounceClick.emit(e));
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
