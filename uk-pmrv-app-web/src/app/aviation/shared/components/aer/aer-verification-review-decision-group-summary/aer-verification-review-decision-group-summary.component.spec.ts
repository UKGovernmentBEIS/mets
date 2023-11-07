import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { AerVerificationReportDataReviewDecision } from 'pmrv-api';

import { AerVerificationReviewDecisionGroupSummaryComponent } from './aer-verification-review-decision-group-summary.component';

describe('AerVerificationReviewDecisionGroupSummaryComponent', () => {
  let component: AerVerificationReviewDecisionGroupSummaryComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-aer-verification-review-decision-group-summary
        [data]="data"
      ></app-aer-verification-review-decision-group-summary>
    `,
  })
  class TestComponent {
    data = {
      type: 'ACCEPTED',
      details: {
        notes: 'My notes',
      },
    } as AerVerificationReportDataReviewDecision;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, AerVerificationReviewDecisionGroupSummaryComponent],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    component = fixture.debugElement.query(
      By.directive(AerVerificationReviewDecisionGroupSummaryComponent),
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
        ['Decision status', 'Notes'],
        ['Accepted', 'My notes'],
      ],
    ]);
  });
});
