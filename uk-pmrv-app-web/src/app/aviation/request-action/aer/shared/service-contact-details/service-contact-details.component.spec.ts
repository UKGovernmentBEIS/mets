import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import ServiceContactDetailsComponent from '@aviation/request-action/aer/shared/service-contact-details/service-contact-details.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<ServiceContactDetailsComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('ServiceContactDetailsComponent', () => {
  let component: ServiceContactDetailsComponent;
  let fixture: ComponentFixture<ServiceContactDetailsComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, ServiceContactDetailsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          serviceContactDetails: {
            name: 'FirstName LastName',
            email: 'test@xx.gr',
            roleCode: 'operator_admin',
          },
        } as AerRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(ServiceContactDetailsComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Service contact details');
    expect(page.summaryValues).toEqual([
      ['Full name', 'FirstName LastName'],
      ['Role', 'Operator admin'],
      ['Email address', 'test@xx.gr'],
    ]);
  });
});
