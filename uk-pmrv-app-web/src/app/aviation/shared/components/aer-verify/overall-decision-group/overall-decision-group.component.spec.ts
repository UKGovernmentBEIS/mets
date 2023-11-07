import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { AviationAerVerifiedSatisfactoryWithCommentsDecision } from 'pmrv-api';

import { OverallDecisionGroupComponent } from './overall-decision-group.component';

describe('OverallDecisionGroupComponent', () => {
  let component: OverallDecisionGroupComponent;
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
      <app-aviation-overall-decision-group
        [isEditable]="isEditable"
        [overallAssessment]="overallAssessmentInfo"
      ></app-aviation-overall-decision-group>
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
      imports: [OverallDecisionGroupComponent, SharedModule, RouterTestingModule],
      declarations: [TestComponent],
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

  it('should render the summary', () => {
    expect(getRows()).toEqual([[], ['Reason 1', '', ''], ['Reason 2', '', '']]);
    expect(getSummaryListValues()).toEqual([['Decision', ['Verified with comments']]]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(getRows()).toEqual([[], ['Reason 1', 'Change', 'Remove'], ['Reason 2', 'Change', 'Remove']]);
    expect(getSummaryListValues()).toEqual([['Decision', ['Verified with comments', 'Change']]]);
  });
});
