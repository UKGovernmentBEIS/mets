import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ServiceContactDetailsComponent } from '@aviation/request-task/aer/corsia/shared/tasks/service-contact-details/service-contact-details.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

describe('ServiceContactDetailsComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: ServiceContactDetailsComponent;
  let fixture: ComponentFixture<ServiceContactDetailsComponent>;

  class Page extends BasePage<ServiceContactDetailsComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServiceContactDetailsComponent, RouterTestingModule],
      providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    }).compileComponents();
  });

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
            serviceContactDetails: {
              name: 'FirstName LastName',
              email: 'test@pmrv.gr',
              roleCode: 'operator_admin',
            },
          },
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(ServiceContactDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.summaryValues).toEqual([
      ['Full name', 'FirstName LastName'],
      ['Role', 'Operator admin'],
      ['Email address', 'test@pmrv.gr'],
    ]);
  });
});
