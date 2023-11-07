import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';
import produce from 'immer';

import { UncorrectedMisstatementsFormProvider } from '../uncorrected-misstatements-form.provider';
import { MisstatementListComponent } from './misstatement-list.component';

describe('MisstatementListComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: MisstatementListComponent;
  let fixture: ComponentFixture<MisstatementListComponent>;

  class Page extends BasePage<MisstatementListComponent> {
    get buttons() {
      return this.queryAll<HTMLButtonElement>('.govuk-button-group');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(MisstatementListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MisstatementListComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: UncorrectedMisstatementsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.isEditable = true;
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              verificationReport: {
                uncorrectedMisstatements: {
                  exist: true,
                  uncorrectedMisstatements: [{ reference: 'A1', explanation: 'explanation', materialEffect: true }],
                },
              },
            } as any,
          },
        };
      }),
    );
  });

  describe('for existing uncorrected misstatement', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the list', () => {
      expect(page.buttons).toHaveLength(2);
      expect(page.buttons.map((el) => el.textContent.trim())).toEqual(['Add another item', 'Continue']);
    });
  });
});
