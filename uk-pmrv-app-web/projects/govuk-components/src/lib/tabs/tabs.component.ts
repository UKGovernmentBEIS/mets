import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import {
  AfterContentInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  contentChildren,
  effect,
  ElementRef,
  inject,
  input,
  OnDestroy,
  OnInit,
  output,
  viewChildren,
} from '@angular/core';
import { ActivatedRoute, NavigationStart, QueryParamsHandling, Router, RouterLink } from '@angular/router';

import { filter, Subscription, tap } from 'rxjs';

import { TabDirective } from './tab/tab.directive';
import { TabBaseDirective } from './tab/tab-base.directive';
import { TabLazyDirective } from './tab/tab-lazy.directive';

@Component({
  selector: 'govuk-tabs',
  imports: [AsyncPipe, NgTemplateOutlet, RouterLink],
  templateUrl: './tabs.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TabsComponent implements OnInit, AfterContentInit, OnDestroy {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly cdRef = inject(ChangeDetectorRef);

  readonly title = input<string>();
  readonly queryParamsHandling = input<QueryParamsHandling>('preserve');

  readonly tabList = contentChildren(TabBaseDirective, { descendants: false });
  readonly tabEagerList = contentChildren(TabDirective, { descendants: false });
  readonly tabLazyList = contentChildren(TabLazyDirective, { descendants: false });

  readonly anchorList = viewChildren<ElementRef<HTMLAnchorElement>>('anchor');

  readonly selectedTab = output<string>();

  private shouldFocusAnchor: boolean;
  private subscriptions = new Subscription();

  constructor() {
    effect(() => {
      if (this.tabList()) {
        this.setTargetTab(this.activatedRoute.snapshot.fragment);
        this.cdRef.detectChanges();
      }
    });
  }

  ngOnInit(): void {
    this.subscriptions.add(
      this.router.events
        .pipe(
          filter((event) => event instanceof NavigationStart && event.navigationTrigger === 'popstate'),
          tap(() => (this.shouldFocusAnchor = true)),
        )
        .subscribe(),
    );
  }

  ngAfterContentInit(): void {
    this.subscriptions.add(this.activatedRoute.fragment.subscribe((fragment) => this.setTargetTab(fragment)));

    this.tabList().map((tab) =>
      this.subscriptions.add(
        tab.isSelected.subscribe((value) => {
          if (value) this.selectedTab.emit(tab.id());
        }),
      ),
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  anchorKeydown(event: KeyboardEvent, index: number): void {
    let targetTab: TabBaseDirective;

    switch (event.key) {
      case 'ArrowLeft':
      case 'ArrowUp':
        targetTab = this.tabList().find((_, i) => i === index - 1);
        break;

      case 'ArrowRight':
      case 'ArrowDown':
        targetTab = this.tabList().find((_, i) => i === index + 1);
        break;
    }

    if (targetTab) {
      this.shouldFocusAnchor = true;

      this.router.navigate(['.'], {
        fragment: targetTab.id(),
        queryParamsHandling: this.queryParamsHandling(),
        state: this.getState(),
        relativeTo: this.activatedRoute,
      });
    }
  }

  getState() {
    return { ...window.history.state, scrollSkip: true };
  }

  private setTargetTab(fragment: string): void {
    if (this.tabList().length === 0) return;

    const currentTab = this.tabList().find((tab) => tab.isSelected.getValue());
    const targetTab = this.tabList().find((tab) => tab.id() === fragment);

    if (!targetTab) {
      if (!currentTab) this.tabList()[0].isSelected.next(true);
      this.shouldFocusAnchor = false;
      return;
    }

    if (currentTab) currentTab.isSelected.next(false);

    targetTab.isSelected.next(true);
    targetTab.cdRef.detectChanges();

    if (this.shouldFocusAnchor) {
      this.shouldFocusAnchor = false;
      this.anchorFocus(targetTab);
    }
  }

  private anchorFocus(targetTab: TabBaseDirective) {
    const targetAnchor = this.anchorList()?.find((_, i) => i === this.tabList().indexOf(targetTab))?.nativeElement;
    if (targetAnchor && targetAnchor !== document.activeElement) targetAnchor.focus();
  }
}
