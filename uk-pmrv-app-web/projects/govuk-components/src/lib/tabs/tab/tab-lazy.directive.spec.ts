import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { delay, of } from 'rxjs';

import { TabLazyDirective, TabsComponent } from '../index';

describe('TabLazyDirective', () => {
  let component: TabsComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    imports: [TabsComponent, TabLazyDirective, AsyncPipe],
    template: `
      <govuk-tabs>
        @for (tab of tabsLazy$ | async; track tab) {
          <ng-template govukTabLazy [id]="tab.id" [label]="tab.label">
            {{ tab.body }}
          </ng-template>
        }
        <ng-template govukTabLazy id="lazy3" label="Lazy 3">Lazy 3</ng-template>
      </govuk-tabs>
    `,
  })
  class TestComponent {
    tabsLazy$ = of([
      { id: 'asyncLazy1', label: 'Async Lazy 1', body: 'Async Lazy 1 content' },
      { id: 'asyncLazy2', label: 'Async Lazy 2', body: 'Async Lazy 2 content' },
    ]).pipe(delay(200));
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TabsComponent, TabLazyDirective, TestComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(TabsComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
