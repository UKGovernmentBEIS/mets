import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { VerifierDetailsCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/verifier-details-corsia-template/verifier-details-corsia-template.component';

describe('VerifierDetailsCorsiaTemplateComponent', () => {
  let component: VerifierDetailsCorsiaTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-verifier-details-corsia-template
        [verificationBodyDetails]="verificationBodyDetails"
        [verifierDetails]="verifierDetails"
        [isEditable]="isEditable"
      ></app-verifier-details-corsia-template>
    `,
  })
  class TestComponent {
    verificationBodyDetails = {
      name: 'Verification body company',
      address: {
        line1: 'Korinthou 4, Neo Psychiko',
        line2: 'line 2 legal test',
        city: 'Athens',
        country: 'GR',
        postcode: '15452',
      },
    };
    verifierDetails = {
      interestConflictAvoidance: {
        breakTaken: true,
        sixVerificationsConducted: true,
        reason: null,
        impartialityAssessmentResult: null,
      },
      verificationTeamLeader: {
        name: 'My name',
        role: 'My role',
        email: 'test@pmrv.com',
        position: 'My position',
      },
    };
    isEditable = true;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifierDetailsCorsiaTemplateComponent, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(VerifierDetailsCorsiaTemplateComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(summaryListValues()).toEqual([
      ['Company name', ['Verification body company']],
      ['Address', [`Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`]],
      ['Name', ['My name', 'Change']],
      ['Position', ['My position', 'Change']],
      ['Role and expertise', ['My role', 'Change']],
      ['Email', ['test@pmrv.com', 'Change']],
      [
        'Have you conducted six or more annual verifications as a team leader for this aeroplane operator?',
        ['Yes', 'Change'],
      ],
      [
        'Following completion of six annual verifications as team leader, did you take a break of three-consecutive years from providing verification services for this Aeroplane Operator?',
        ['Yes', 'Change'],
      ],
    ]);

    hostComponent.verifierDetails = {
      interestConflictAvoidance: {
        breakTaken: false,
        sixVerificationsConducted: true,
        reason: 'My reason',
        impartialityAssessmentResult: null,
      },
      verificationTeamLeader: {
        name: 'My name',
        role: 'My role',
        email: 'test@pmrv.com',
        position: 'My position',
      },
    };
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Company name', ['Verification body company']],
      ['Address', [`Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`]],
      ['Name', ['My name', 'Change']],
      ['Position', ['My position', 'Change']],
      ['Role and expertise', ['My role', 'Change']],
      ['Email', ['test@pmrv.com', 'Change']],
      [
        'Have you conducted six or more annual verifications as a team leader for this aeroplane operator?',
        ['Yes', 'Change'],
      ],
      [
        'Following completion of six annual verifications as team leader, did you take a break of three-consecutive years from providing verification services for this Aeroplane Operator?',
        ['No', 'Change'],
      ],
      ['Provide a reason for not meeting the conflict of interest requirement', ['My reason', 'Change']],
    ]);

    hostComponent.verifierDetails = {
      interestConflictAvoidance: {
        breakTaken: null,
        sixVerificationsConducted: false,
        reason: null,
        impartialityAssessmentResult: 'My results',
      },
      verificationTeamLeader: {
        name: 'My name',
        role: 'My role',
        email: 'test@pmrv.com',
        position: 'My position',
      },
    };
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Company name', ['Verification body company']],
      ['Address', [`Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`]],
      ['Name', ['My name', 'Change']],
      ['Position', ['My position', 'Change']],
      ['Role and expertise', ['My role', 'Change']],
      ['Email', ['test@pmrv.com', 'Change']],
      [
        'Have you conducted six or more annual verifications as a team leader for this aeroplane operator?',
        ['No', 'Change'],
      ],
      [
        'Describe the main results of impartiality and avoidance of conflict of interest assessment',
        ['My results', 'Change'],
      ],
    ]);

    hostComponent.isEditable = false;
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Company name', ['Verification body company']],
      ['Address', [`Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`]],
      ['Name', ['My name']],
      ['Position', ['My position']],
      ['Role and expertise', ['My role']],
      ['Email', ['test@pmrv.com']],
      ['Have you conducted six or more annual verifications as a team leader for this aeroplane operator?', ['No']],
      ['Describe the main results of impartiality and avoidance of conflict of interest assessment', ['My results']],
    ]);

    hostComponent.verifierDetails = {
      interestConflictAvoidance: null,
      verificationTeamLeader: {
        name: 'My name',
        role: 'My role',
        email: 'test@pmrv.com',
        position: 'My position',
      },
    };
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Company name', ['Verification body company']],
      ['Address', [`Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`]],
      ['Name', ['My name']],
      ['Position', ['My position']],
      ['Role and expertise', ['My role']],
      ['Email', ['test@pmrv.com']],
    ]);

    hostComponent.verifierDetails = {
      interestConflictAvoidance: null,
      verificationTeamLeader: null,
    };
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Company name', ['Verification body company']],
      ['Address', [`Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`]],
    ]);

    hostComponent.verifierDetails = null;
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Company name', ['Verification body company']],
      ['Address', [`Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`]],
    ]);
  });

  function summaryListValues() {
    return Array.from(element.querySelectorAll<HTMLDivElement>('.govuk-summary-list__row')).map((row) => [
      row.querySelector('dt').textContent.trim(),
      Array.from(row.querySelectorAll('dd')).map((el) => el.textContent.trim()),
    ]);
  }
});
