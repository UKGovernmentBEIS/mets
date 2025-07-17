import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { EmpVariationRegulatorLedDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-regulator-led-decision-group-summary/emp-variation-regulator-led-decision-group-summary.component';
import { SharedModule } from '@shared/shared.module';

describe('EmpVariationRegulatorLedDecisionGroupSummaryComponent', () => {
  let component: EmpVariationRegulatorLedDecisionGroupSummaryComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-emp-variation-regulator-led-decision-group-summary
        [data]="data"></app-emp-variation-regulator-led-decision-group-summary>
    `,
  })
  class TestComponent {
    data = {
      notes: 'My Notes',
      variationScheduleItems: ['Item 1', 'Item 2'],
    };
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, EmpVariationRegulatorLedDecisionGroupSummaryComponent],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    component = fixture.debugElement.query(
      By.directive(EmpVariationRegulatorLedDecisionGroupSummaryComponent),
    ).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the summary values', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Items added to the variation schedule', 'Notes'],
        ['1. Item 1 2. Item 2', 'My Notes'],
      ],
    ]);
  });
});
