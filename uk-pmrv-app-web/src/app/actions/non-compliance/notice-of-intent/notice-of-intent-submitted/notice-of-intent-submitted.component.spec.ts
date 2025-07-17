import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { NonComplianceService } from '@actions/non-compliance/core/non-compliance.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { CaExternalContactsService } from 'pmrv-api';

import { NoticeOfIntentSubmittedComponent } from './notice-of-intent-submitted.component';
import { mockExternalContacts, mockPayload } from './testing/mock-notice-of-intent-submitted';
describe('NoticeOfIntentSubmittedComponent', () => {
  let page: Page;
  let component: NoticeOfIntentSubmittedComponent;
  let fixture: ComponentFixture<NoticeOfIntentSubmittedComponent>;

  const route = new ActivatedRouteStub({ taskId: 14 }, null, {
    pageTitle: 'Notice of intent submitted',
    actionType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED',
  });

  class Page extends BasePage<NoticeOfIntentSubmittedComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get data() {
      return this.queryAll<HTMLElement>('h2, dl dt, dl dd div').map((item) => item.textContent.trim());
    }
  }

  const nonComplianceService = {
    getPayload: jest.fn().mockReturnValue(of(mockPayload)),
  };

  const externalContactsService = {
    getCaExternalContacts: jest.fn().mockReturnValue(of(mockExternalContacts)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NoticeOfIntentSubmittedComponent],
      imports: [ActionSharedModule, SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: NonComplianceService, useValue: nonComplianceService },
        { provide: CaExternalContactsService, useValue: externalContactsService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NoticeOfIntentSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', async () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', async () => {
    expect(page.heading).toEqual('Details of notice of intent');
    expect(page.data).toEqual([
      'Details',
      'Comments',
      'Uploaded document',
      'Recipients',
      'Users',
      'Mock user name, Operator admin - Primary contact, Service contact, Financial contact',
      'Installation 5 Account - External contact',
    ]);
  });
});
