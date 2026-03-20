import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage } from '@testing';

import { BDRS2 } from 'pmrv-api';

import { BDRS2BaselineSummaryTemplateComponent } from './baseline-summary-template.component';

describe('BDRS2BaselineSummaryTemplateComponent', () => {
  let page: Page;
  let component: BDRS2BaselineSummaryTemplateComponent;
  let fixture: ComponentFixture<BDRS2BaselineSummaryTemplateComponent>;

  class Page extends BasePage<BDRS2BaselineSummaryTemplateComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        TaskSharedModule,
        BdrS2TaskSharedModule,
        RouterTestingModule,
        BDRS2BaselineSummaryTemplateComponent,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BDRS2BaselineSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isEditable = true;
    component.files = [
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

  it('should show all html elements ', () => {
    component.data = {
      bdrs2guardQuestions: {
        applicationWithdrawalReason: undefined,
        continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
        covidAdjustments: true,
        inEiteSector: true,
        requiresAdditionalSubInstallationSplitsForCbam: true,
      },
    } as BDRS2;
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Do you want to continue with your application for free allocation?',
      'Yes, I hold a GHGE permit and want to continue my application for free allocation as a main scheme participant, or I currently hold HSE status and want to become a main scheme participant from 2027 to 2030',
      'Change  decision for free allocation',
      'Are you making COVID adjustments?',
      'Yes',
      'Change  decision for COVID adjustments',
      'Is your installation in the aluminium, cement, fertiliser, hydrogen, iron or steel sector?',
      'Yes',
      'Change  decision about EITE sector',
      'Are additional sub-installation splits required because of the UK CBAM?',
      'Yes',
      'Change CBAM decision',
      'Uploaded supporting files',
      '100.png  200.png',
      'Change  stage 2 baseline data report supporting files',
    ]);
  });
});
