import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerModule } from '../aer.module';
import { SubmitContainerComponent } from './submit-container.component';

describe('SubmitComponent', () => {
  let component: SubmitContainerComponent;
  let fixture: ComponentFixture<SubmitContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<SubmitContainerComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(store, 'requestTaskType', 'get').mockReturnValue('AER_APPLICATION_SUBMIT');
    jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
    jest.spyOn(store, 'requestMetadata$', 'get').mockReturnValue(
      of({
        type: 'AER',
        year: '2022',
      }),
    );
    jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
      of({
        requestTask: {
          id: 1,
          type: 'AER_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'AER_APPLICATION_SUBMIT_PAYLOAD',
            aer: {
              monitoringApproachEmissions: {
                CALCULATION_CO2: {
                  type: 'CALCULATION_CO2',
                },
                MEASUREMENT_CO2: null,
              },
            },
            aerSectionsCompleted: {
              emissionSources: [true],
              sourceStreams: [true],
              monitoringApproachEmissions: [false],
            },
          },
        },
        allowedRequestTaskActions: [],
      }),
    );

    fixture = TestBed.createComponent(SubmitContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the content', () => {
    expect(page.heading).toEqual('2022 emissions report');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Installation and operator details',
      'Pollutant Release and Transfer Register codes (PRTR)',
      'NACE codes',
      'Regulated activities carried out at the installation',
      'Monitoring plan versions during the reporting year',
      'Monitoring approaches used during the reporting year',
      'Source streams (fuels and materials)',
      'Emission sources',
      'Emission points',
      'Calculation of CO2 emissions',
      'Measurement of CO2 emissions',
      'Emissions summary',
      'Abbreviations and definitions',
      'Additional documents and information',
      'Confidentiality statement',
      'Send report',
    ]);
  });
});
