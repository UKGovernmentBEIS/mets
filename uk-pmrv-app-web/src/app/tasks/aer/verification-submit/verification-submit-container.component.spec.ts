import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { VerificationSubmitContainerComponent } from '@tasks/aer/verification-submit/verification-submit-container.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { AerModule } from '../aer.module';
import { AerService } from '../core/aer.service';

describe('VerificationSubmitContainerComponent', () => {
  let component: VerificationSubmitContainerComponent;
  let fixture: ComponentFixture<VerificationSubmitContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;
  let aerService: AerService;

  class Page extends BasePage<VerificationSubmitContainerComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item .app-task-list__task-name'));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule, AerModule],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  describe('Submit Non GHGE', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
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
            type: 'AER_APPLICATION_VERIFICATION_SUBMIT',
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

      fixture = TestBed.createComponent(VerificationSubmitContainerComponent);
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
        'Installation details',
        'Fuels and equipment inventory',
        'Calculation of CO2 emissions',
        'Measurement of CO2 emissions',
        'Emissions summary',
        'Additional information',

        'Verifier details',
        'Opinion statement',
        'Compliance with ETS rules',
        'Compliance with monitoring and reporting principles',
        'Overall decision',

        'Uncorrected misstatements',
        'Uncorrected non-conformities',
        'Uncorrected non-compliances',
        'Recommended improvements',
        'Methodologies to close data gaps',
        'Materiality level and reference documents',
        'Summary of conditions, changes, clarifications and variations',
        'Send report to operator',
      ]);
    });
  });

  describe('Submit Non GHGE after 2025', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      aerService = TestBed.inject(AerService);
      jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
      jest.spyOn(store, 'requestMetadata$', 'get').mockReturnValue(
        of({
          type: 'AER',
          year: '2025',
        }),
      );
      jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
        of({
          requestTask: {
            id: 1,
            type: 'AER_APPLICATION_VERIFICATION_SUBMIT',
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
      jest.spyOn(aerService, 'isMaterialityUpdated$', 'get').mockReturnValue(of(true));

      fixture = TestBed.createComponent(VerificationSubmitContainerComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the content', () => {
      expect(page.heading).toEqual('2025 emissions report');
      expect(page.sections.map((el) => el.textContent.trim())).toEqual([
        'Installation details',
        'Fuels and equipment inventory',
        'Calculation of CO2 emissions',
        'Measurement of CO2 emissions',
        'Emissions summary',
        'Additional information',

        'Verifier details',
        'Opinion statement',
        'Compliance with ETS rules',
        'Compliance with monitoring and reporting principles',
        'Overall decision',

        'Uncorrected misstatements',
        'Uncorrected non-conformities',
        'Uncorrected non-compliances',
        'Recommended improvements',
        'Methodologies to close data gaps',
        'Further information of relevance to the opinion',
        'Summary of conditions, changes, clarifications and variations',
        'Send report to operator',
      ]);
    });
  });

  describe('Submit GHGE with non hidden ALR', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
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
            type: 'AER_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              permitOriginatedData: { permitType: 'GHGE' },
              isPostALRSectionRemoval: null,
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

      fixture = TestBed.createComponent(VerificationSubmitContainerComponent);
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
        'Installation details',
        'Fuels and equipment inventory',
        'Calculation of CO2 emissions',
        'Measurement of CO2 emissions',
        'Emissions summary',
        'Activity level report',
        'Additional information',

        'Verifier details',
        'Opinion statement',
        'Compliance with ETS rules',
        'Compliance with monitoring and reporting principles',
        'Verification report of the activity level report',
        'Overall decision',

        'Uncorrected misstatements',
        'Uncorrected non-conformities',
        'Uncorrected non-compliances',
        'Recommended improvements',
        'Methodologies to close data gaps',
        'Materiality level and reference documents',
        'Summary of conditions, changes, clarifications and variations',
        'Send report to operator',
      ]);
    });
  });
});
