import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AviationAccountsModule } from '@aviation/accounts/aviation-accounts.module';
import { AviationAccountsStore } from '@aviation/accounts/store';
import { mockedAccount } from '@aviation/accounts/testing/mock-data';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { EditReportingStatusSummaryComponent } from './edit-reporting-status-summary.component';

describe('EditReportingStatusSummaryComponent', () => {
  let component: EditReportingStatusSummaryComponent;
  let fixture: ComponentFixture<EditReportingStatusSummaryComponent>;
  let store: AviationAccountsStore;
  let page: Page;

  class Page extends BasePage<EditReportingStatusSummaryComponent> {
    get summariesContents(): string[] {
      return this.queryAll<HTMLDListElement>('dl dt, dl dd').map((dd) => dd.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditReportingStatusSummaryComponent],
      imports: [SharedModule, AviationAccountsModule],
      providers: [
        provideHttpClient(),
        AviationAccountsStore,

        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ account: 3, reportingYear: 2025 }) },
      ],
    }).compileComponents();

    store = TestBed.inject(AviationAccountsStore);
    store.setCurrentAccount(mockedAccount);
    store.setCurrentStatus({
      status: 'EXEMPT_COMMERCIAL',
      year: '2025',
      reason: 'Lorem ipsum',
      isReported: false,
    });
    store.editReportingStatus({
      status: 'EXEMPT_NON_COMMERCIAL',
      reason: 'test reason',
      year: 2025,
    });
    fixture = TestBed.createComponent(EditReportingStatusSummaryComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTML elements', () => {
    expect(page.summariesContents).toEqual([
      'Selected year',
      '2025',
      '',
      'Change reporting status to',
      'Exempt (non commercial)',
      '',
      'Reason',
      'test reason',
      'Change',
    ]);
  });
});
