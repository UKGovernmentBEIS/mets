import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { take } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { RequestActionStore } from '@aviation/request-action/store';
import { AviationEmissionsSummaryTemplateComponent } from '@aviation/shared/components/dre/aviation-emissions-summary-template';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub } from '@testing';

import { AviationEmissionsUpdatedComponent } from './aviation-emissions-updated.component';

describe('AviationEmissionsUpdatedComponent', () => {
  let component: AviationEmissionsUpdatedComponent;
  let fixture: ComponentFixture<AviationEmissionsUpdatedComponent>;
  let store: RequestActionStore;

  const route = new ActivatedRouteStub();
  const currentDate = new Date().toISOString();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AviationEmissionsSummaryTemplateComponent, SharedModule, RequestActionTaskComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED',
        creationDate: currentDate,

        payload: {
          dre: {
            determinationReason: {
              type: 'VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER',
              furtherDetails: 'Is further details',
            },
            totalReportableEmissions: '5260',
            calculationApproach: {
              type: 'EUROCONTROL_SUPPORT_FACILITY',
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
            name: 'DRE_notice.pdf',
            uuid: '8fe67a55-748c-41e6-ba04-48924bcbb582',
          },
        } as any,
      },
      regulatorViewer: true,
    });

    fixture = TestBed.createComponent(AviationEmissionsUpdatedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update ViewModel properties when vm$ emits a value', () => {
    component.vm$.pipe(take(1)).subscribe((vm) => {
      expect(vm.requestActionType).toEqual('AVIATION_AER_UKETS_APPLICATION_SUBMITTED');
      expect(vm.pageHeader).toEqual('Aviation emissions updated');
      expect(vm.creationDate).toBeDefined();
      expect(vm.usersInfo).toBeDefined();
      expect(vm.officialNotice).toBeDefined();
      expect(vm.downloadUrl).toEqual('/');
      expect(vm.data).toBeDefined();
    });
  });
});
