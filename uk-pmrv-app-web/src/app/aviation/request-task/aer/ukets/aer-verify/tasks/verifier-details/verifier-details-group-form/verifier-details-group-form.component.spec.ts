import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import { VerifierDetailsFormProvider } from '../verifier-details-form.provider';
import AerVerifierDetailsGroupFormComponent from './verifier-details-group-form.component';

describe('AerVerifierDetailsGroupFormComponent', () => {
  let fixture: ComponentFixture<AerVerifierDetailsGroupFormComponent>;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerVerifierDetailsGroupFormComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: VerifierDetailsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AerVerifierDetailsGroupFormComponent);

    fixture.componentInstance.verificationReport = VERIFICATION_REPORT;
    element = fixture.nativeElement;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(Array.from(element.querySelectorAll<HTMLHeadElement>('h2')).map((el) => el.textContent.trim())).toEqual([
      'Verification body',
      'Accreditation information',
      'Verifier contact',
      'Verification team details',
    ]);

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [['Company name'], ['VB Company']],
      [['Address'], ['street 1  , street 2 City111 80']],
      [['Accreditation number'], ['1313']],
      [['National accreditation body'], ['UK ETS Aviation EU ETS Installations CORSIA UK ETS Installations']],
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

    fixture.componentInstance.showVerifierDetails = false;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [['Company name'], ['VB Company']],
      [['Address'], ['street 1  , street 2 City111 80']],
      [['Accreditation number'], ['1313']],
      [['National accreditation body'], ['UK ETS Aviation EU ETS Installations CORSIA UK ETS Installations']],
    ]);

    fixture.componentInstance.showVerifierDetails = true;
    fixture.componentInstance.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [['Company name'], ['VB Company']],
      [['Address'], ['street 1  , street 2 City111 80']],
      [['Accreditation number'], ['1313']],
      [['National accreditation body'], ['UK ETS Aviation EU ETS Installations CORSIA UK ETS Installations']],
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
