import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { mockNerSubmitStateBuild } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { NERApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { NerDetailsVerificationComponent } from './ner-details.component';

describe('NerDetailsComponent', () => {
  let component: NerDetailsVerificationComponent;
  let fixture: ComponentFixture<NerDetailsVerificationComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const currentPayload = {
    ner: {
      nerFiles: {
        file: '22222222-2222-4222-a222-222222222222',
        supportingFiles: ['11111111-1111-4111-a111-111111111111'],
      },
      mmpFiles: {
        file: '33333333-3333-4222-a222-333333333333',
        supportingFiles: ['44444444-4444-4111-a111-444444444444'],
      },
      notes: 'A note',
    },
    nerAttachments: {
      '11111111-1111-4111-a111-111111111111': 'test1.txt',
      '22222222-2222-4222-a222-222222222222': 'test2.txt',
      '33333333-3333-4222-a222-333333333333': 'test3.txt',
      '44444444-4444-4111-a111-444444444444': 'test4.txt',
    },
    nerSectionsCompleted: { details: false },
  } as NERApplicationVerificationSubmitRequestTaskPayload;

  class Page extends BasePage<NerDetailsVerificationComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerDetailsVerificationComponent],
      providers: [CapitalizeFirstPipe, provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockNerSubmitStateBuild(currentPayload));

    fixture = TestBed.createComponent(NerDetailsVerificationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements ', () => {
    expect(page.pageContents).toEqual([
      'Uploaded new entrant reserve',
      'test2.txt',
      'Uploaded supporting files',
      'test1.txt',
      'Notes',
      'A note',
      'Uploaded monitoring methodology plan',
      'test3.txt',
      'Uploaded supporting files',
      'test4.txt',
    ]);
  });
});
