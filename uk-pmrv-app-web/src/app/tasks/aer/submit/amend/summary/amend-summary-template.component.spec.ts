import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { AerModule } from '../../../aer.module';
import { mockState } from '../../testing/mock-aer-apply-action';
import { AmendSummaryTemplateComponent } from './amend-summary-template.component';

describe('AmendSummaryTemplateComponent', () => {
  let page: Page;
  let component: AmendSummaryTemplateComponent;
  let fixture: ComponentFixture<AmendSummaryTemplateComponent>;
  let store: CommonTasksStore;

  class Page extends BasePage<AmendSummaryTemplateComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    const route = new ActivatedRouteStub({ section: 'FUELS_AND_EQUIPMENT' });

    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          payload: {
            ...mockState.requestTaskItem.requestTask.payload,
            reviewGroupDecisions: {
              FUELS_AND_EQUIPMENT: {
                type: 'OPERATOR_AMENDS_NEEDED',
                details: {
                  notes: 'srfs',
                  requiredChanges: [
                    {
                      reason: '234234',
                    },
                  ],
                },
                reviewDataType: 'AER_DATA',
              },
            },
            reviewSectionsCompleted: {},
          } as AerApplicationReviewRequestTaskPayload,
        },
      },
    });

    fixture = TestBed.createComponent(AmendSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Amends needed for fuels and equipment inventory');
    expect(page.summaryListValues).toHaveLength(1);
    expect(page.summaryListValues).toEqual([['Changes required', '1. 234234']]);
  });
});
