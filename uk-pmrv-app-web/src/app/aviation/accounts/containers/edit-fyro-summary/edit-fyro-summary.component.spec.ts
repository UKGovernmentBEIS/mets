import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AviationAccountsModule } from '@aviation/accounts/aviation-accounts.module';
import { AviationAccountsStore } from '@aviation/accounts/store';
import { mockedAccount } from '@aviation/accounts/testing/mock-data';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { EditFyroSummaryComponent } from './edit-fyro-summary.component';

describe('EditFyroSummaryComponent', () => {
  let component: EditFyroSummaryComponent;
  let fixture: ComponentFixture<EditFyroSummaryComponent>;
  let store: AviationAccountsStore;
  let page: Page;

  class Page extends BasePage<EditFyroSummaryComponent> {
    get summariesContents(): string[] {
      return this.queryAll<HTMLDListElement>('dl dt, dl dd').map((dd) => dd.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditFyroSummaryComponent],
      imports: [SharedModule, AviationAccountsModule],
      providers: [
        provideHttpClient(),
        AviationAccountsStore,

        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ account: 3 }) },
      ],
    }).compileComponents();

    store = TestBed.inject(AviationAccountsStore);
    store.setCurrentAccount(mockedAccount);
    store.editFirstYearOfReportingObligation({
      commencementDate: mockedAccount.aviationAccount.commencementDate,
      reason: 'reason',
    });

    fixture = TestBed.createComponent(EditFyroSummaryComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTML elements', () => {
    expect(page.summariesContents).toEqual([
      'First year of reporting obligation',
      '1 Jan 2023',
      'Change  first year of reporting obligation',
      'Reason',
      'reason',
      'Change reason',
    ]);
  });
});
