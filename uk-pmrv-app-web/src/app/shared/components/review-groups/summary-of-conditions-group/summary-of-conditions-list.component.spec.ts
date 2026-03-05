import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SummaryOfConditionsListComponent } from '@shared/components/review-groups/summary-of-conditions-group/summary-of-conditions-list.component';
import { SharedModule } from '@shared/shared.module';

describe('SummaryOfConditionsListComponent', () => {
  let component: SummaryOfConditionsListComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-summary-of-conditions-list
        [isEditable]="isEditable"
        [list]="list"
        baseChangeLink=".."></app-summary-of-conditions-list>
    `,
  })
  class TestComponent {
    isEditable = false;
    list = [
      {
        explanation: 'Explanation 1',
        reference: 'Reference 1',
      },
      {
        explanation: 'Explanation 2',
        reference: 'Reference 2',
      },
    ];
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(SummaryOfConditionsListComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the table', () => {
    expect(
      Array.from(element.querySelectorAll('tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
      ),
    ).toEqual([[], ['Reference 1', 'Explanation 1', '', ''], ['Reference 2', 'Explanation 2', '', '']]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll('tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
      ),
    ).toEqual([
      [],
      ['Reference 1', 'Explanation 1', 'Change', 'Remove'],
      ['Reference 2', 'Explanation 2', 'Change', 'Remove'],
    ]);

    hostComponent.list = [];
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll('tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
      ),
    ).toEqual([[]]);
  });
});
