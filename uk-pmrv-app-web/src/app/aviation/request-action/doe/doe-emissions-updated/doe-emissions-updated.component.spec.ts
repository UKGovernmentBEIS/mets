import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { take } from 'rxjs';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AviationEmissionsSummaryTemplateComponent } from '@aviation/shared/components/dre/aviation-emissions-summary-template';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { DoeEmissionsUpdatedComponent } from './doe-emissions-updated.component';

describe('DoeEmissionsUpdatedComponent', () => {
  let component: DoeEmissionsUpdatedComponent;
  let fixture: ComponentFixture<DoeEmissionsUpdatedComponent>;
  let store: CommonActionsStore;
  let page: Page;

  const route = new ActivatedRouteStub();
  const currentDate = new Date().toISOString();

  class Page extends BasePage<DoeEmissionsUpdatedComponent> {
    get recipientsTemplate() {
      return this.query('app-action-recipients-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AviationEmissionsSummaryTemplateComponent, SharedModule, RequestActionTaskComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        type: 'AVIATION_DOE_CORSIA_SUBMITTED',
        creationDate: currentDate,

        payload: {
          doe: {
            determinationReason: {
              type: 'VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED',
              furtherDetails: 'Is further details',
            },
            emissions: {
              calculationApproach: '123',
              emissionsAllInternationalFlights: '123',
              emissionsClaimFromCorsiaEligibleFuels: '1234',
              emissionsFlightsWithOffsettingRequirements: '12',
            },
            fee: {
              chargeOperator: true,
              feeDetails: {
                totalBillableHours: '152',
                hourlyRate: '55',
                dueDate: '2023-11-25',
                comments: 'Further optional details',
              },
            },
            supportingDocuments: [],
            usersInfo: {
              '8efe50da-b790-4b9e-b599-e1b89a094a20': {
                name: 'Operator England',
                roleCode: 'operator_admin',
                contactTypes: ['SERVICE', 'PRIMARY', 'FINANCIAL'],
              },
            },
          },
          officialNotice: {
            name: 'DoE_notice.pdf',
            uuid: '8fe67a55-748c-41e6-ba04-48924bcbb582',
          },
        } as any,
      },
    });

    fixture = TestBed.createComponent(DoeEmissionsUpdatedComponent);

    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update ViewModel properties when vm$ emits a value', () => {
    component.vm$.pipe(take(1)).subscribe((vm) => {
      expect(vm.requestActionType).toEqual('AVIATION_DOE_CORSIA_SUBMITTED');
      expect(vm.pageHeader).toEqual('Aviation emissions updated');
      expect(vm.creationDate).toBeDefined();

      expect(vm.data).toBeDefined();

      expect(page.recipientsTemplate).toBeTruthy();
    });
  });
});
