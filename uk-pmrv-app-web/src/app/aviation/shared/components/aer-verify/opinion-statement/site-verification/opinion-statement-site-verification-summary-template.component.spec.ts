import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import OpinionStatementSiteVerificationSummaryTemplateComponent from './opinion-statement-site-verification-summary-template.component';

describe('OpinionStatementSiteVerificationSummaryTemplateComponent', () => {
  let component: OpinionStatementSiteVerificationSummaryTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let element: HTMLElement;

  @Component({
    template: `
      <app-opinion-statement-site-verification-summary-template
        [siteVisit]="siteVisit"
        [isEditable]="isEditable"
        [queryParams]="queryParams"></app-opinion-statement-site-verification-summary-template>
    `,
  })
  class TestComponent {
    siteVisit: any = {
      type: 'IN_PERSON',
      visitDates: [{ startDate: '2020-01-01', numberOfDays: 5 }],
      teamMembers: '5',
    };

    isEditable = false;
    queryParams = {};
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementSiteVerificationSummaryTemplateComponent, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;

    component = fixture.debugElement.query(
      By.directive(OpinionStatementSiteVerificationSummaryTemplateComponent),
    ).componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the site verification with in person site visit and without being editable', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['What kind of site visit did your team make?', 'Site visits', 'Team members involved'],
        ['In person site visit', '1 Jan 2020 for 5 days', '5'],
      ],
    ]);
  });

  it('should render the site verification with virtual site visit and without being editable', () => {
    hostComponent.siteVisit = {
      type: 'VIRTUAL',
      reason: 'reason',
    };

    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['What kind of site visit did your team make?', 'Reasons for making a virtual site visit'],
        ['Virtual site visit', 'reason'],
      ],
    ]);
  });

  it('should render the site verification with in person site visit and be editable', () => {
    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['What kind of site visit did your team make?', 'Site visits', 'Team members involved'],
        ['In person site visit', 'Change', '1 Jan 2020 for 5 days', 'Change', '5', 'Change'],
      ],
    ]);
  });

  it('should render the site verification with virtual site visit and be editable', () => {
    hostComponent.isEditable = true;
    hostComponent.siteVisit = {
      type: 'VIRTUAL',
      reason: 'reason',
    };

    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['What kind of site visit did your team make?', 'Reasons for making a virtual site visit'],
        ['Virtual site visit', 'Change', 'reason', 'Change'],
      ],
    ]);
  });
});
