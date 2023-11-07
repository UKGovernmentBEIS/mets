import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { UncorrectedItemGroupComponent } from './uncorrected-item-group.component';

describe('UncorrectedItemGroupComponent', () => {
  let component: UncorrectedItemGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  function getRows() {
    return Array.from(element.querySelectorAll('tr')).map((el) =>
      Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
    );
  }

  @Component({
    template: `
      <app-uncorrected-item-group
        [isEditable]="isEditable"
        [queryParams]="queryParams"
        [uncorrectedItems]="uncorrectedItems"
      ></app-uncorrected-item-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    queryParams = { change: true };
    uncorrectedItems = [{ reference: 'A1', explanation: 'explanation', materialEffect: true }];
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UncorrectedItemGroupComponent, SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(UncorrectedItemGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the list table', () => {
    expect(getRows()).toEqual([[], ['A1', 'explanation', 'Material', '', '']]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(getRows()).toEqual([[], ['A1', 'explanation', 'Material', 'Change', 'Remove']]);
  });
});
