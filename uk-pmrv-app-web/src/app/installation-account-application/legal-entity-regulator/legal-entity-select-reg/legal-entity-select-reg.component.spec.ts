import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CountryService } from '@core/services/country.service';
import { LegalEntitySelect } from '@shared/interfaces/legal-entity';
import { SharedModule } from '@shared/shared.module';
import { BasePage, changeInputValue, CountryServiceStub, MockType } from '@testing';

import { LegalEntitiesService, LegalEntityDTO } from 'pmrv-api';

import { legalEntityFormRegFactory } from '../../factories/legal-entity/legal-entity-form-reg.factory';
import { InstallationAccountApplicationStore } from '../../store/installation-account-application.store';
import { mockPayload } from '../../testing/mock-state';
import { LegalEntitySelectRegComponent } from './legal-entity-select-reg.component';

const value: LegalEntitySelect = {
  isNew: true,
  id: null,
};

describe('LegalEntitySelectRegComponent', () => {
  let component: LegalEntitySelectRegComponent;
  let fixture: ComponentFixture<LegalEntitySelectRegComponent>;
  let page: Page;
  let router: Router;
  let legalEntitiesService: MockType<LegalEntitiesService>;
  let store: InstallationAccountApplicationStore;

  const legalEntity: LegalEntityDTO = { ...mockPayload.legalEntity, id: 1 };
  let navigateSpy: jest.SpyInstance<Promise<boolean>>;

  class Page extends BasePage<LegalEntitySelectRegComponent> {
    get optionValues() {
      return this.queryAll<HTMLInputElement>('input[name="isNew"]').map((option) => this.getInputValue(option));
    }

    get idValue() {
      return this.getInputValue('select[name="id"]');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    legalEntitiesService = { getLegalEntityById: jest.fn().mockReturnValue(of(legalEntity)) };

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [LegalEntitySelectRegComponent],
      providers: [
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: LegalEntitiesService, useValue: legalEntitiesService },
        legalEntityFormRegFactory,
        InstallationAccountApplicationStore,
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    TestBed.inject(ActivatedRoute).data = of({ legalEntities: [{ id: 1, name: 'Legal Entity' }] });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LegalEntitySelectRegComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    store = TestBed.inject(InstallationAccountApplicationStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill form from setState', () => {
    component.form.patchValue(value);
    fixture.detectChanges();

    fixture.detectChanges();
    expect(page.optionValues).toEqual(['false', 'true']);

    component.form.patchValue({ isNew: false, id: 2 });
    fixture.detectChanges();

    expect(page.optionValues).toEqual(['false', 'true']);
    expect(page.idValue).toEqual(2);
  });

  it('should not submit if not new and id is null', () => {
    component.form.patchValue({ ...value, id: null });

    changeInputValue(fixture, '#isNew-option0');

    page.submitButton.click();

    expect(navigateSpy).not.toHaveBeenCalled();

    changeInputValue(fixture, '#id', 1);
    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should navigate to details if new', () => {
    changeInputValue(fixture, '#isNew-option1');
    page.submitButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['../details'], { relativeTo: TestBed.inject(ActivatedRoute) });
    expect(legalEntitiesService.getLegalEntityById).not.toHaveBeenCalled();
  });

  it('should update task if existing and then move to task list', () => {
    const updateTaskSpy = jest.spyOn(store, 'updateTask').mockImplementation();
    changeInputValue(fixture, '#isNew-option0');
    changeInputValue(fixture, '#id', 1);
    page.submitButton.click();
    fixture.detectChanges();

    expect(updateTaskSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: TestBed.inject(ActivatedRoute) });
    expect(legalEntitiesService.getLegalEntityById).toHaveBeenCalled();
  });
});
