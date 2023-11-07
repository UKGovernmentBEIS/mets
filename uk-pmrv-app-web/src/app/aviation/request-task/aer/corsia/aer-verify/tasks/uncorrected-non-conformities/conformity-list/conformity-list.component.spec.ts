import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ConformityListComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/uncorrected-non-conformities/conformity-list/conformity-list.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

describe('ConformityListComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: ConformityListComponent;
  let fixture: ComponentFixture<ConformityListComponent>;

  class Page extends BasePage<ConformityListComponent> {
    get rows() {
      return Array.from(this.queryAll('tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
      );
    }

    get buttons() {
      return this.queryAll<HTMLButtonElement>('.govuk-button-group');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ConformityListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConformityListComponent, RouterTestingModule],
      providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    }).compileComponents();
  });

  describe('for existing uncorrected non conformities', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                uncorrectedNonConformities: {
                  existUncorrectedNonConformities: true,
                  uncorrectedNonConformities: [
                    {
                      reference: 'B1',
                      explanation: 'non-compliance',
                      materialEffect: true,
                    },
                  ],
                },
              },
              verificationSectionsCompleted: {},
            },
          },
        },
        isEditable: true,
      } as any);

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the list', () => {
      expect(page.rows).toEqual([[], ['B1', 'non-compliance', 'Material', 'Change', 'Remove']]);
      expect(page.buttons).toHaveLength(2);
      expect(page.buttons.map((el) => el.textContent.trim())).toEqual(['Add another item', 'Continue']);
    });
  });

  describe('for no uncorrected non conformities', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                uncorrectedNonConformities: {
                  existUncorrectedNonConformities: true,
                  uncorrectedNonConformities: [],
                },
              },
              verificationSectionsCompleted: {},
            },
          },
        },
        isEditable: true,
      } as any);

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the list', () => {
      expect(page.rows).toEqual([[]]);
      expect(page.buttons).toHaveLength(1);
      expect(page.buttons.map((el) => el.textContent.trim())).toEqual(['Add another item']);
    });
  });
});
