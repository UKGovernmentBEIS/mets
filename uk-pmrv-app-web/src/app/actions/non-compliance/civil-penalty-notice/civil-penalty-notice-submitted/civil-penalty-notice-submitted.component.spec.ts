import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { NonComplianceService } from '@actions/non-compliance/core/non-compliance.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { CaExternalContactsService } from 'pmrv-api';

import { CivilPenaltyNoticeSubmittedComponent } from './civil-penalty-notice-submitted.component';
import { mockExternalContacts, mockPayload } from './testing/mock-civil-penalty-notice-submitted';

describe('CivilPenaltyNoticeSubmittedComponent', () => {
  let page: Page;
  let component: CivilPenaltyNoticeSubmittedComponent;
  let fixture: ComponentFixture<CivilPenaltyNoticeSubmittedComponent>;

  const route = new ActivatedRouteStub({ taskId: 14 }, null, {
    pageTitle: 'Civil penalty notice submitted',
    actionType: 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED',
  });

  class Page extends BasePage<CivilPenaltyNoticeSubmittedComponent> {
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
      declarations: [CivilPenaltyNoticeSubmittedComponent],
      imports: [ActionSharedModule, SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: NonComplianceService, useValue: nonComplianceService },
        { provide: CaExternalContactsService, useValue: externalContactsService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CivilPenaltyNoticeSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', async () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', async () => {
    expect(page.heading).toEqual('Details of penalty notice');
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
