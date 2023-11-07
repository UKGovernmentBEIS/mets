import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { OverallDecisionReasonListComponent } from '@shared/components/review-groups/overall-decision-group/overall-decision-reason-list.component';
import { SharedModule } from '@shared/shared.module';

describe('OverallDecisionReasonListComponent', () => {
  let component: OverallDecisionReasonListComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: ` <app-overall-decision-list [isEditable]="isEditable" [list]="list"></app-overall-decision-list> `,
  })
  class TestComponent {
    isEditable = false;
    list = ['Reason 1', 'Reason 2'];
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
    component = fixture.debugElement.query(By.directive(OverallDecisionReasonListComponent)).componentInstance;
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
    ).toEqual([[], ['Reason 1', '', ''], ['Reason 2', '', '']]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll('tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
      ),
    ).toEqual([[], ['Reason 1', 'Change', 'Remove'], ['Reason 2', 'Change', 'Remove']]);
  });
});
