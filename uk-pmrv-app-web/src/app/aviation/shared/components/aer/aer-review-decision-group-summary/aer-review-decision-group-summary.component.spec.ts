import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { AerDataReviewDecision } from 'pmrv-api';

import { AerReviewDecisionGroupSummaryComponent } from './aer-review-decision-group-summary.component';

describe('AerReviewDecisionGroupSummaryComponent', () => {
  let component: AerReviewDecisionGroupSummaryComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-aer-review-decision-group-summary
        [data]="data"
        [attachments]="attachments"
        downloadBaseUrl="test-link"></app-aer-review-decision-group-summary>
    `,
  })
  class TestComponent {
    data = {
      type: 'OPERATOR_AMENDS_NEEDED',
      details: {
        requiredChanges: [
          {
            reason: 'My first reason',
            files: ['randomUUID'],
          },
          {
            reason: 'My second reason',
          },
        ],
        notes: 'My notes',
      },
    } as AerDataReviewDecision;

    attachments = {
      randomUUID: 'fileName',
    };
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, AerReviewDecisionGroupSummaryComponent],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(AerReviewDecisionGroupSummaryComponent)).componentInstance;
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
        ['Decision status', 'Changes required by operator', 'Notes'],
        ['Operator changes required', '1. My first reason  fileName  2. My second reason', 'My notes'],
      ],
    ]);

    hostComponent.data = {
      type: 'ACCEPTED',
      details: {
        notes: 'My notes',
      },
    };
    fixture.detectChanges();

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
