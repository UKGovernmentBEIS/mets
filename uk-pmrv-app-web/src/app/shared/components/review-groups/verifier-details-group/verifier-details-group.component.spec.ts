import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { VerifierDetailsGroupComponent } from '@shared/components/review-groups/verifier-details-group/verifier-details-group.component';
import { SharedModule } from '@shared/shared.module';

import { AerVerificationReport } from 'pmrv-api';

describe('VerifierDetailsGroupComponent', () => {
  let component: VerifierDetailsGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-verifier-details-group
        [showVerifierDetails]="showVerifierDetails"
        [isEditable]="isEditable"
        [verificationReport]="verificationReport"
      ></app-verifier-details-group>
    `,
  })
  class TestComponent {
    showVerifierDetails = true;
    isEditable = false;
    verificationReport = {
      verificationBodyDetails: {
        accreditationReferenceNumber: '1313',
        address: {
          city: 'City',
          country: 'GR',
          line1: 'street 1',
          line2: 'street 2',
          postcode: '111 80',
        },
        emissionTradingSchemes: ['CORSIA', 'EU_ETS_INSTALLATIONS'],
        name: 'VB Company',
      },
      verificationTeamDetails: {
        authorisedSignatoryName: 'authorised signatory name',
        etsAuditors: 'ets auditors',
        etsTechnicalExperts: 'ets technical experts',
        independentReviewer: 'independent reviewer',
        leadEtsAuditor: 'lead ets auditor',
        technicalExperts: 'technical experts',
      },
      verifierContact: {
        email: 'test@test.com',
        name: 'Verifier Name',
        phoneNumber: '6691423232',
      },
    } as AerVerificationReport;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(VerifierDetailsGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name')).map(
        (el) => el.textContent.trim(),
      ),
    ).toEqual(['Verification body', 'Accreditation information', 'Verifier contact', 'Verification team details']);

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Company name', 'Address'],
        ['VB Company', 'street 1  , street 2 City111 80'],
      ],
      [
        ['Accreditation number', 'National accreditation body'],
        ['1313', 'CORSIA EU ETS Installations'],
      ],
      [
        ['Name', 'Email', 'Telephone number'],
        ['Verifier Name', 'test@test.com', '6691423232'],
      ],
      [
        [
          'Lead ETS Auditor',
          'ETS Auditors',
          'Technical Experts (ETS Auditor)',
          'Independent Reviewer',
          'Technical Experts (Independent Review)',
          'Name of authorised signatory',
        ],
        [
          'lead ets auditor',
          'ets auditors',
          'ets technical experts',
          'independent reviewer',
          'technical experts',
          'authorised signatory name',
        ],
      ],
    ]);

    hostComponent.showVerifierDetails = false;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Company name', 'Address'],
        ['VB Company', 'street 1  , street 2 City111 80'],
      ],
      [
        ['Accreditation number', 'National accreditation body'],
        ['1313', 'CORSIA EU ETS Installations'],
      ],
    ]);

    hostComponent.showVerifierDetails = true;
    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Company name', 'Address'],
        ['VB Company', 'street 1  , street 2 City111 80'],
      ],
      [
        ['Accreditation number', 'National accreditation body'],
        ['1313', 'CORSIA EU ETS Installations'],
      ],
      [
        ['Name', 'Email', 'Telephone number'],
        ['Verifier Name', 'Change', 'test@test.com', 'Change', '6691423232', 'Change'],
      ],
      [
        [
          'Lead ETS Auditor',
          'ETS Auditors',
          'Technical Experts (ETS Auditor)',
          'Independent Reviewer',
          'Technical Experts (Independent Review)',
          'Name of authorised signatory',
        ],
        [
          'lead ets auditor',
          'Change',
          'ets auditors',
          'Change',
          'ets technical experts',
          'Change',
          'independent reviewer',
          'Change',
          'technical experts',
          'Change',
          'authorised signatory name',
          'Change',
        ],
      ],
    ]);
  });
});
