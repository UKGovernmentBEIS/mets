import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { AviationAerVerifiedSatisfactoryWithCommentsDecision } from 'pmrv-api';

import { OverallDecisionGroupComponent } from './overall-decision-group.component';

describe('OverallDecisionGroupComponent', () => {
  let component: OverallDecisionGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  function getSummaryListValues() {
    return Array.from(element.querySelectorAll('dl')).map((el) => [
      el.querySelector('dt').textContent.trim(),
      Array.from(el.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
    ]);
  }

  function getBodyStatements() {
    return Array.from(element.querySelectorAll('.govuk-body'));
  }

  @Component({
    template: `
      <app-aviation-overall-decision-group
        [isEditable]="isEditable"
        [overallAssessment]="overallAssessmentInfo"></app-aviation-overall-decision-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    overallAssessmentInfo = {
      type: 'VERIFIED_AS_SATISFACTORY_WITH_COMMENTS',
      reasons: ['Reason 1', 'Reason 2'],
    } as AviationAerVerifiedSatisfactoryWithCommentsDecision;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverallDecisionGroupComponent],
      declarations: [TestComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(OverallDecisionGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show body statements', () => {
    expect(getBodyStatements()[0].textContent.trim()).toEqual(
      'You have conducted a verification of the greenhouse gas data reported by this operator in its annual emissions report.',
    );

    expect(getBodyStatements()[1].textContent.trim()).toEqual(
      'On the basis of your verification work these data are fairly stated, with the exception of the following reasons.',
    );
  });

  it('should render the summary', () => {
    expect(getSummaryListValues()).toEqual([
      ['Decision', ['Verified as satisfactory with comments', '1. Reason 1 2. Reason 2']],
    ]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(getSummaryListValues()).toEqual([
      ['Decision', ['Verified as satisfactory with comments', 'Change', '1. Reason 1 2. Reason 2', 'Change']],
    ]);
  });
});
