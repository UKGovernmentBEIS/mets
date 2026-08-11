import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { MiReportsUserDefinedService } from 'pmrv-api';

import { mockMiReportUserDefinedHistoryResults } from '../testing/mock-data';
import { ReportHistoryComponent } from './report-history.component';

describe('ReportHistoryComponent', () => {
  let component: ReportHistoryComponent;
  let fixture: ComponentFixture<ReportHistoryComponent>;
  let page: Page;
  let miReportsService: Partial<jest.Mocked<MiReportsUserDefinedService>>;

  class Page extends BasePage<ReportHistoryComponent> {
    get heading(): HTMLElement {
      return this.query('app-page-heading');
    }

    get eventHeadings(): string[] {
      return this.queryAll<HTMLHeadingElement>('h2').map((heading) => heading.textContent.trim());
    }

    get summaryLists(): HTMLDListElement[] {
      return this.queryAll<HTMLDListElement>('dl');
    }

    keysOf(summaryList: HTMLDListElement): string[] {
      return Array.from(summaryList.querySelectorAll('dt')).map((dt) => dt.textContent.trim());
    }

    valuesOf(summaryList: HTMLDListElement): string[] {
      return Array.from(summaryList.querySelectorAll('dd')).map((dd) => dd.textContent.replace(/\s+/g, ' ').trim());
    }

    categoriesOf(summaryList: HTMLDListElement): string[] {
      return Array.from(summaryList.querySelectorAll('dd li')).map((li) => li.textContent.trim());
    }

    get sqlDetails(): HTMLElement[] {
      return this.queryAll<HTMLElement>('govuk-details');
    }
  }

  const routeStub = new ActivatedRouteStub({ id: '7' });

  beforeEach(async () => {
    miReportsService = {
      getHistory: jest.fn().mockReturnValue(of(mockMiReportUserDefinedHistoryResults)),
    };

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: routeStub },
        { provide: MiReportsUserDefinedService, useValue: miReportsService },
      ],
      declarations: [ReportHistoryComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    routeStub.setQueryParamMap({});
    fixture = TestBed.createComponent(ReportHistoryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the page heading and load the first history page', () => {
    expect(page.heading.textContent).toContain('Report history');
    expect(miReportsService.getHistory).toHaveBeenCalledWith(7, 0, 3);
  });

  it('should display a timestamped heading for every timeline event', () => {
    expect(page.eventHeadings).toEqual(['9 Apr 2026, 10:22am', '4 Mar 2026, 10:20am']);
  });

  it('should display the reason for change, changed by and the full snapshot for an edit event', () => {
    const [updateEvent] = page.summaryLists;

    expect(page.keysOf(updateEvent)).toEqual([
      'Reason for change',
      'Changed by',
      'Report name',
      'Categories',
      'Description',
      'SQL Query',
    ]);

    const [reasonForChange, changedBy, reportName, , description] = page.valuesOf(updateEvent);
    expect(reasonForChange).toBe('Added region_name join and updated ORDER BY clause.');
    expect(changedBy).toBe('Regulator England');
    expect(reportName).toBe('Active installations by permit type and region');
    expect(description).toBe('Returns all active installation accounts grouped by permit type and region.');

    expect(page.categoriesOf(updateEvent)).toEqual(['Workflow Submission Status', 'Management', 'Financial']);
  });

  it('should display the submitted by and the full snapshot for the initial submission event', () => {
    const [, createEvent] = page.summaryLists;

    expect(page.keysOf(createEvent)).toEqual(['Submitted by', 'Report name', 'Categories', 'Description', 'SQL Query']);

    const [submittedBy] = page.valuesOf(createEvent);
    expect(submittedBy).toBe('Regulator England');

    expect(page.categoriesOf(createEvent)).toEqual(['Workflow Submission Status', 'Management']);
  });

  it('should display the SQL query of every event behind a details expander', () => {
    expect(page.sqlDetails).toHaveLength(2);

    const [updateDetails, createDetails] = page.sqlDetails;
    expect(updateDetails.querySelector('summary').textContent.trim()).toBe('Show SQL code');
    expect(updateDetails.textContent).toContain('FROM accounts a');
    expect(createDetails.textContent).toContain('SELECT * FROM accounts');
  });

  it('should fetch the selected page using a zero based page index', () => {
    routeStub.setQueryParamMap({ page: '2' });
    fixture.detectChanges();

    expect(miReportsService.getHistory).toHaveBeenCalledWith(7, 1, 3);
  });

  it('should display an empty state when there is no history', () => {
    miReportsService.getHistory.mockReturnValue(of({ results: [], total: 0 }));

    const emptyFixture = TestBed.createComponent(ReportHistoryComponent);
    emptyFixture.detectChanges();

    expect(emptyFixture.nativeElement.textContent).toContain('There is no history available.');
  });

  it('should display a return link back to the MI reports page', () => {
    const links = Array.from(fixture.nativeElement.querySelectorAll('a')) as HTMLAnchorElement[];
    const returnLink = links.find((link) => link.textContent.trim() === 'Return to: MI Reports');

    expect(returnLink).toBeTruthy();
  });
});
