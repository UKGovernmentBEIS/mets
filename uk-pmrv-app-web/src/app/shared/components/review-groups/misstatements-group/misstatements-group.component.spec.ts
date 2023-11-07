import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { MisstatementsGroupComponent } from '@shared/components/review-groups/misstatements-group/misstatements-group.component';
import { SharedModule } from '@shared/shared.module';

describe('MisstatementsGroupComponent', () => {
  let component: MisstatementsGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  function getRows() {
    return Array.from(element.querySelectorAll('tr')).map((el) =>
      Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
    );
  }

  function getSummaryListValues() {
    return Array.from(element.querySelectorAll('dl')).map((el) => [
      el.querySelector('dt').textContent.trim(),
      Array.from(el.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
    ]);
  }

  @Component({
    template: `
      <app-misstatements-group
        [isEditable]="isEditable"
        [uncorrectedMisstatements]="uncorrectedMisstatements"
        baseChangeLink=".."
      ></app-misstatements-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    uncorrectedMisstatements = {
      areThereUncorrectedMisstatements: true,
      uncorrectedMisstatements: [
        {
          explanation: 'Explanation 1',
          reference: 'Reference 1',
          materialEffect: true,
        },
        {
          explanation: 'Explanation 2',
          reference: 'Reference 2',
          materialEffect: false,
        },
      ],
    };
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
    component = fixture.debugElement.query(By.directive(MisstatementsGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the summary', () => {
    expect(getSummaryListValues()).toEqual([
      ['Are there any misstatements that were not corrected before completing this report?', ['Yes', '']],
    ]);
    expect(getRows()).toEqual([
      [],
      ['Reference 1', 'Explanation 1', 'Material', '', ''],
      ['Reference 2', 'Explanation 2', 'Immaterial', '', ''],
    ]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(getSummaryListValues()).toEqual([
      ['Are there any misstatements that were not corrected before completing this report?', ['Yes', 'Change']],
    ]);
    expect(getRows()).toEqual([
      [],
      ['Reference 1', 'Explanation 1', 'Material', 'Change', 'Remove'],
      ['Reference 2', 'Explanation 2', 'Immaterial', 'Change', 'Remove'],
    ]);

    hostComponent.uncorrectedMisstatements = {
      areThereUncorrectedMisstatements: true,
      uncorrectedMisstatements: [],
    };
    fixture.detectChanges();

    expect(getSummaryListValues()).toEqual([
      ['Are there any misstatements that were not corrected before completing this report?', ['Yes', 'Change']],
    ]);
    expect(getRows()).toEqual([]);

    hostComponent.uncorrectedMisstatements = {
      areThereUncorrectedMisstatements: true,
      uncorrectedMisstatements: undefined,
    };
    fixture.detectChanges();

    expect(getSummaryListValues()).toEqual([
      ['Are there any misstatements that were not corrected before completing this report?', ['Yes', 'Change']],
    ]);
    expect(getRows()).toEqual([]);

    hostComponent.uncorrectedMisstatements = {
      areThereUncorrectedMisstatements: false,
      uncorrectedMisstatements: undefined,
    };
    fixture.detectChanges();

    expect(getSummaryListValues()).toEqual([
      ['Are there any misstatements that were not corrected before completing this report?', ['No', 'Change']],
    ]);
    expect(getRows()).toEqual([]);

    hostComponent.uncorrectedMisstatements = undefined;
    fixture.detectChanges();

    expect(getSummaryListValues()).toEqual([]);
    expect(getRows()).toEqual([]);
  });
});
