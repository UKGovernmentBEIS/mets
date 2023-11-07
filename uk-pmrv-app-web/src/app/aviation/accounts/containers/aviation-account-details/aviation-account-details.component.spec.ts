import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BasePage, mockClass } from '@testing';

import { AviationAccountReportingStatusService, AviationAccountsService, AviationAccountUpdateService } from 'pmrv-api';

import { AviationAccountsStore } from '../../store';
import { mockedAccount } from '../../testing/mock-data';
import { AviationAccountDetailsComponent } from './aviation-account-details.component';

describe('AviationAccountDetailsComponent', () => {
  let component: AviationAccountDetailsComponent;
  let fixture: ComponentFixture<AviationAccountDetailsComponent>;
  let page: Page;
  let store: AviationAccountsStore;

  class Page extends BasePage<AviationAccountDetailsComponent> {
    get heading() {
      return this.queryAll<HTMLHeadingElement>('h2');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AviationAccountDetailsComponent],
      providers: [
        AviationAccountsStore,
        { provide: AviationAccountsService, useValue: mockClass(AviationAccountsService) },
        { provide: AviationAccountUpdateService, useValue: mockClass(AviationAccountUpdateService) },
        { provide: AviationAccountReportingStatusService, useValue: mockClass(AviationAccountReportingStatusService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(AviationAccountDetailsComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(AviationAccountsStore);
    store.setCurrentAccount(mockedAccount);
    component.currentTab = 'details';
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the heading', () => {
    expect(page.heading.map((el) => el.textContent.trim())).toEqual(['Aviation operator details']);
  });

  it('should not render account closed when account is not closed', () => {
    store.setCurrentAccount(mockedAccount);
    fixture.detectChanges();
    const accountClosed = fixture.debugElement.query(By.css('#aviation-account-closed'));
    expect(accountClosed).toBeFalsy();
  });

  it('should not render live account content when account has not emp data', () => {
    store.setCurrentAccount(mockedAccount);
    fixture.detectChanges();
    const accountClosed = fixture.debugElement.query(By.css('.live-account-content'));
    expect(accountClosed).toBeFalsy();
  });

  it('should render live account content when account has emp data', () => {
    store.setCurrentAccount({
      ...mockedAccount,
      emp: {
        id: 'UK-E-AV-00090',
        empAttachments: {},
        fileDocument: { name: 'UK-E-AV-00090 v1.pdf', uuid: 'fc042c1f-1608-4443-8766-f0c60c49cd87' },
      },
    });
    fixture.detectChanges();
    const accountClosed = fixture.debugElement.query(By.css('.live-account-content'));
    expect(accountClosed).toBeTruthy();
  });
});
