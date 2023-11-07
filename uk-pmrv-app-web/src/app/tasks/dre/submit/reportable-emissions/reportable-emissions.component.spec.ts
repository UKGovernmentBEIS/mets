import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { DreTaskComponent } from '../../shared/components/dre-task/dre-task.component';
import { dreCompleted, mockCompletedDreApplicationSubmitRequestTaskItem, updateMockedDre } from '../../test/mock';
import { ReportableEmissionsComponent } from './reportable-emissions.component';

describe('ReportableEmissionsComponent', () => {
  let component: ReportableEmissionsComponent;
  let fixture: ComponentFixture<ReportableEmissionsComponent>;
  let hostElement: HTMLElement;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ReportableEmissionsComponent> {
    get calculationCO2CombustionReportableEmission() {
      return this.getInputValue('input[name$="calculationOfCO2.combustionEmissions.reportableEmissions"]');
    }
    set calculationCO2CombustionReportableEmission(value: string) {
      this.setInputValue('input[name$="calculationOfCO2.combustionEmissions.reportableEmissions"]', value);
    }
    get calculationCO2CombustionSustainableBiomass() {
      return this.getInputValue('input[name$="calculationOfCO2.combustionEmissions.sustainableBiomass"]');
    }
    set calculationCO2CombustionSustainableBiomass(value: string) {
      this.setInputValue('input[name$="calculationOfCO2.combustionEmissions.sustainableBiomass"]', value);
    }

    get calculationCO2ProcessEmissionsReportableEmission() {
      return this.getInputValue('input[name$="calculationOfCO2.processEmissions.reportableEmissions"]');
    }
    set calculationCO2ProcessEmissionsReportableEmission(value: string) {
      this.setInputValue('input[name$="calculationOfCO2.processEmissions.reportableEmissions"]', value);
    }
    get calculationCO2ProcessEmissionsSustainableBiomass() {
      return this.getInputValue('input[name$="calculationOfCO2.processEmissions.sustainableBiomass"]');
    }
    set calculationCO2ProcessEmissionsSustainableBiomass(value: string) {
      this.setInputValue('input[name$="calculationOfCO2.processEmissions.sustainableBiomass"]', value);
    }

    get calculationCO2MassBalanceEmissionsReportableEmission() {
      return this.getInputValue('input[name$="calculationOfCO2.massBalanceEmissions.reportableEmissions"]');
    }
    set calculationCO2MassBalanceEmissionsReportableEmission(value: string) {
      this.setInputValue('input[name$="calculationOfCO2.massBalanceEmissions.reportableEmissions"]', value);
    }
    get calculationCO2MassBalanceEmissionsSustainableBiomass() {
      return this.getInputValue('input[name$="calculationOfCO2.massBalanceEmissions.sustainableBiomass"]');
    }
    set calculationCO2MassBalanceEmissionsSustainableBiomass(value: string) {
      this.setInputValue('input[name$="calculationOfCO2.massBalanceEmissions.sustainableBiomass"]', value);
    }

    get calculationCO2TransferredCO2EmissionsReportableEmission() {
      return this.getInputValue('input[name$="calculationOfCO2.transferredCO2Emissions.reportableEmissions"]');
    }
    set calculationCO2TransferredCO2EmissionsReportableEmission(value: string) {
      this.setInputValue('input[name$="calculationOfCO2.transferredCO2Emissions.reportableEmissions"]', value);
    }
    get calculationCO2TransferredCO2EmissionsSustainableBiomass() {
      return this.getInputValue('input[name$="calculationOfCO2.transferredCO2Emissions.sustainableBiomass"]');
    }
    set calculationCO2TransferredCO2EmissionsSustainableBiomass(value: string) {
      this.setInputValue('input[name$="calculationOfCO2.transferredCO2Emissions.sustainableBiomass"]', value);
    }

    get calculationOfPFCEmissionsReportableEmission() {
      return this.getInputValue('input[name$="calculationOfPFC.totalEmissions.reportableEmissions"]');
    }
    set calculationOfPFCEmissionsReportableEmission(value: string) {
      this.setInputValue('input[name$="calculationOfPFC.totalEmissions.reportableEmissions"]', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ReportableEmissionsComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReportableEmissionsComponent, DreTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockedDre(
        {
          monitoringApproachReportingEmissions: {
            CALCULATION_CO2: {
              type: 'CALCULATION_CO2',
              calculateTransferredCO2: true,
            } as any,
            CALCULATION_PFC: {
              type: 'CALCULATION_PFC',
            } as any,
          },
        },
        false,
      ),
    });
  });
  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit', () => {
    expect(page.errorSummary).toBeFalsy();

    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.calculationCO2CombustionReportableEmission = '10';
    page.calculationCO2CombustionSustainableBiomass = '11';
    page.calculationCO2ProcessEmissionsReportableEmission = '12';
    page.calculationCO2ProcessEmissionsSustainableBiomass = '13';
    page.calculationCO2MassBalanceEmissionsReportableEmission = '14';
    page.calculationCO2MassBalanceEmissionsSustainableBiomass = '15';
    page.calculationCO2TransferredCO2EmissionsReportableEmission = '16';
    page.calculationCO2TransferredCO2EmissionsSustainableBiomass = '17';
    page.calculationOfPFCEmissionsReportableEmission = '18';
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeFalsy();
    expect(hostElement.textContent.trim()).toContain('Reportable emissions: 70 tonnes CO2e');
    expect(hostElement.textContent.trim()).toContain('Sustainable biomass: 56 tonnes CO2e');
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'DRE_SAVE_APPLICATION',
      requestTaskId: mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'DRE_SAVE_APPLICATION_PAYLOAD',
        dre: {
          ...dreCompleted,
          monitoringApproachReportingEmissions: {
            CALCULATION_CO2: {
              type: 'CALCULATION_CO2',
              calculateTransferredCO2: true,
              combustionEmissions: {
                reportableEmissions: '10',
                sustainableBiomass: '11',
              },
              processEmissions: {
                reportableEmissions: '12',
                sustainableBiomass: '13',
              },
              massBalanceEmissions: {
                reportableEmissions: '14',
                sustainableBiomass: '15',
              },
              transferredCO2Emissions: {
                reportableEmissions: '16',
                sustainableBiomass: '17',
              },
            } as any,
            CALCULATION_PFC: {
              type: 'CALCULATION_PFC',
              totalEmissions: {
                reportableEmissions: '18',
              },
            } as any,
          },
        },
        sectionCompleted: false,
      },
    });
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'information-sources'], { relativeTo: route });
  });
});
