import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../../testing/mock-state';
import { EmissionSourceDeleteComponent } from './emission-source-delete.component';

describe('EmissionSourceDeleteComponent', () => {
  let component: EmissionSourceDeleteComponent;
  let fixture: ComponentFixture<EmissionSourceDeleteComponent>;
  let store: PermitApplicationStore<PermitApplicationState>;
  let router: Router;
  let page: Page;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ sourceId: mockPermitApplyPayload.permit.emissionSources[0].id }, null, {
    permitTask: 'emissionSources',
  });

  class Page extends BasePage<EmissionSourceDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmissionSourceDeleteComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(EmissionSourceDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the emission source name', () => {
    expect(page.header.textContent.trim()).toContain('S1 Boiler');
  });

  it('should delete the emission source', () => {
    expect(store.permit.emissionSources).toEqual(mockPermitApplyPayload.permit.emissionSources);

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');
    const targetId = mockPermitApplyPayload.permit.emissionSources[0].id;
    const expectedEmissionSources = mockPermitApplyPayload.permit.emissionSources.filter(
      (source) => source.id !== targetId,
    );
    const expectedEmissionSummaries = mockPermitApplyPayload.permit.emissionSummaries.map((summary) => ({
      ...summary,
      emissionSources:
        summary.emissionSources.length > 1
          ? summary.emissionSources.filter((summarySourceId) => summarySourceId !== targetId)
          : summary.emissionSources,
    }));

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        { emissionSources: expectedEmissionSources, emissionSummaries: expectedEmissionSummaries },
        { emissionSources: [false] },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(store.permit.emissionSources).toEqual(expectedEmissionSources);
    expect(store.permit.emissionSummaries).toEqual(expectedEmissionSummaries);
  });
});
