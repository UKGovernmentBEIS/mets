import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionPageComponent } from '@aviation/request-action/containers';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import produce from 'immer';

class Page extends BasePage<RequestActionPageComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get creationDate(): string {
    return this.query('.govuk-caption-m').textContent.trim();
  }
  get sections(): HTMLUListElement[] {
    return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item .app-task-list__task-name'));
  }
}

describe('RequestActionPageComponent', () => {
  let component: RequestActionPageComponent;
  let fixture: ComponentFixture<RequestActionPageComponent>;
  let store: RequestActionStore;
  const backlinkService = mockClass(BackLinkService);
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [RequestActionPageComponent],
      providers: [
        { provide: BackLinkService, useValue: backlinkService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          emissionsMonitoringPlan: {},
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(RequestActionPageComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct header', async () => {
    expect(page.header).toMatch(/^Changes submitted/);
    expect(page.creationDate).toMatch(/29 Nov 2022, 12:12pm/);

    store.setState(
      produce(store.getState(), (state) => {
        state.requestActionItem = {
          ...state.requestActionItem,
          type: 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED',
        };
      }),
    );
    fixture.detectChanges();
    expect(page.header).toMatch(/^Approved/);
    expect(page.creationDate).toMatch(/29 Nov 2022, 12:12pm/);
  });

  it('should display the "Approved" header for an approved EMP CORSIA variation application', () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestActionItem = {
          ...state.requestActionItem,
          type: 'EMP_VARIATION_CORSIA_APPLICATION_APPROVED',
        };
      }),
    );
    fixture.detectChanges();
    expect(page.header).toMatch(/^Approved/);
  });

  it('should display the task list', async () => {
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Service contact details',
      'Operator details',
      'Flights and aircraft monitoring procedures',
      'Monitoring approach',
      'Emissions reduction claim',
      'Emission sources',
      'Management procedures',
      'Abbreviations and definitions',
      'Additional documents and information',
      'When you need to apply',
    ]);
  });

  // METS-2604: the "Emissions plan application" link on the decision-summary page for rejected/deemed withdrawn
  // decisions routes back to this page - it must render a populated task list (snapshot), not an empty one.
  describe('rejected/deemed withdrawn EMP decisions (METS-2604)', () => {
    it('should display the task list for a deemed withdrawn EMP issuance application', () => {
      store.setState({
        requestActionItem: {
          type: 'EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN',
          creationDate: '2022-11-29T12:12:48.469862Z',
          payload: {
            emissionsMonitoringPlan: {},
          } as EmpRequestActionPayload,
        },
        regulatorViewer: true,
      });
      fixture = TestBed.createComponent(RequestActionPageComponent);
      page = new Page(fixture);
      fixture.detectChanges();

      expect(page.sections.map((el) => el.textContent.trim())).not.toEqual([]);
      expect(page.header).toMatch(/^Withdrawn/);
    });

    it('should display the task list, including variation details, for a rejected EMP CORSIA variation application', () => {
      store.setState({
        requestActionItem: {
          type: 'EMP_VARIATION_CORSIA_APPLICATION_REJECTED',
          creationDate: '2022-11-29T12:12:48.469862Z',
          payload: {
            emissionsMonitoringPlan: {},
            empVariationDetails: {},
            payloadType: 'EMP_VARIATION_CORSIA_APPLICATION_REJECTED_PAYLOAD',
          } as EmpRequestActionPayload,
        },
        regulatorViewer: true,
      });
      fixture = TestBed.createComponent(RequestActionPageComponent);
      page = new Page(fixture);
      fixture.detectChanges();

      const sectionNames = page.sections.map((el) => el.textContent.trim());
      expect(sectionNames).not.toEqual([]);
      expect(sectionNames).toContain('Describe the changes');
      expect(page.header).toMatch(/^Rejected/);
    });

    it('should display the task list, including variation details, for a deemed withdrawn EMP CORSIA variation application', () => {
      store.setState({
        requestActionItem: {
          type: 'EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN',
          creationDate: '2022-11-29T12:12:48.469862Z',
          payload: {
            emissionsMonitoringPlan: {},
            empVariationDetails: {},
            payloadType: 'EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD',
          } as EmpRequestActionPayload,
        },
        regulatorViewer: true,
      });
      fixture = TestBed.createComponent(RequestActionPageComponent);
      page = new Page(fixture);
      fixture.detectChanges();

      const sectionNames = page.sections.map((el) => el.textContent.trim());
      expect(sectionNames).not.toEqual([]);
      expect(sectionNames).toContain('Describe the changes');
      expect(page.header).toMatch(/^Withdrawn/);
    });

    it('should display variation details for a rejected EMP UKETS variation application', () => {
      store.setState({
        requestActionItem: {
          type: 'EMP_VARIATION_UKETS_APPLICATION_REJECTED',
          creationDate: '2022-11-29T12:12:48.469862Z',
          payload: {
            emissionsMonitoringPlan: {},
            empVariationDetails: {},
            payloadType: 'EMP_VARIATION_UKETS_APPLICATION_REJECTED_PAYLOAD',
          } as EmpRequestActionPayload,
        },
        regulatorViewer: true,
      });
      fixture = TestBed.createComponent(RequestActionPageComponent);
      page = new Page(fixture);
      fixture.detectChanges();

      expect(page.sections.map((el) => el.textContent.trim())).toContain('Describe the changes');
    });
  });
});
