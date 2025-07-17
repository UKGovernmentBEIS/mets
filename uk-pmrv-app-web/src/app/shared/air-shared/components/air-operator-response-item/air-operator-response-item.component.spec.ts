import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';
import { BasePage } from '@testing';

import {
  OperatorAirImprovementAlreadyMadeResponse,
  OperatorAirImprovementNoResponse,
  OperatorAirImprovementYesResponse,
} from 'pmrv-api';

import { AirOperatorResponseItemComponent } from './air-operator-response-item.component';

describe('AirOperatorResponseItemComponent', () => {
  let page: Page;
  let component: AirOperatorResponseItemComponent;
  let fixture: ComponentFixture<AirOperatorResponseItemComponent>;

  class Page extends BasePage<AirOperatorResponseItemComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AirOperatorResponseItemComponent],
      imports: [AirSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AirOperatorResponseItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.reference = '1';
    component.attachedFiles = [
      {
        downloadUrl: '/downloads/111111',
        fileName: '100.png',
      },
      {
        downloadUrl: '/downloads/222222',
        fileName: '200.png',
      },
    ];
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements for OperatorAirImprovementYesResponse', () => {
    component.operatorAirImprovementResponse = {
      type: 'YES',
      proposal: 'Test explanation 1',
      proposedDate: '2040-06-03',
      files: ['111111', '222222'],
    } as OperatorAirImprovementYesResponse as OperatorAirImprovementResponseAll;
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Your response',
      'Are improvements to meet the highest tier being proposed?',
      'Yes',
      'Change',
      'Proposals for attaining the highest tier',
      'Test explanation 1',
      'Change',
      'Proposed date for improvement',
      '3 Jun 2040',
      'Change',
      'Uploaded files',
      '100.png  200.png',
      'Change',
    ]);
  });

  it('should show all html elements for OperatorAirImprovementNoResponse', () => {
    component.operatorAirImprovementResponse = {
      type: 'NO',
      isCostUnreasonable: true,
      isTechnicallyInfeasible: true,
      technicalInfeasibilityExplanation: 'Test description 2',
      files: ['111111', '222222'],
    } as OperatorAirImprovementNoResponse as OperatorAirImprovementResponseAll;
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Your response',
      'Are improvements to meet the highest tier being proposed?',
      'No',
      'Change',
      'Justification for not meeting the highest tier',
      'Unreasonable costTechnical infeasibility',
      'Change',
      'Explain why it is technically infeasible to meet the highest tier',
      'Test description 2',
      'Change',
      'Uploaded files',
      '100.png  200.png',
      'Change',
    ]);
  });

  it('should show all html elements for OperatorAirImprovementAlreadyMadeResponse', () => {
    component.operatorAirImprovementResponse = {
      type: 'ALREADY_MADE',
      explanation: 'Test description 3',
      improvementDate: '2020-06-03',
      files: ['111111', '222222'],
    } as OperatorAirImprovementAlreadyMadeResponse as OperatorAirImprovementResponseAll;
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Your response',
      'Are improvements to meet the highest tier being proposed?',
      'Improvement already made',
      'Change',
      'Tell us about this improvement',
      'Test description 3',
      'Change',
      'Date improvement was made',
      '3 Jun 2020',
      'Change',
      'Uploaded files',
      '100.png  200.png',
      'Change',
    ]);
  });
});
