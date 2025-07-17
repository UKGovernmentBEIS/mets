import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormGroupName } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { CountryService } from '@core/services/country.service';
import { AddressInputComponent } from '@shared/address-input/address-input.component';
import { LegalEntityDetails } from '@shared/interfaces/legal-entity';
import { SharedModule } from '@shared/shared.module';
import { buttonClick, changeInputValue, CountryServiceStub, getInputValue } from '@testing';
import { cloneDeep } from 'lodash-es';

import { LegalEntitiesService } from 'pmrv-api';

import { LegalEntitiesServiceStub } from '../../../../testing/legal-entities.service.stub';
import { legalEntityFormOpFactory } from '../../factories/legal-entity/legal-entity-form-op.factory';
import { InstallationAccountApplicationStore } from '../../store/installation-account-application.store';
import { LegalEntityDetailsOpComponent } from './legal-entity-details-op.component';

const value: Partial<LegalEntityDetails> = {
  name: 'test',
  type: 'PARTNERSHIP',
  address: {
    line1: 'line1',
    line2: 'line2',
    city: 'city',
    country: 'GR',
    postcode: 'postcode',
  },
  belongsToHoldingCompany: true,
  holdingCompanyGroup: {
    name: 'testhc',
    registrationNumber: '999999999',
    address: {
      line1: 'hcline1',
      line2: null,
      city: 'hccity',
      postcode: 'hcpostcode',
    },
  },
};

describe('LegalEntityDetailsOpComponent', () => {
  let component: LegalEntityDetailsOpComponent;
  let fixture: ComponentFixture<LegalEntityDetailsOpComponent>;
  let router: Router;

  @NgModule({
    imports: [CommonModule],
  })
  class FormErrorTestModule {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, FormErrorTestModule],
      declarations: [LegalEntityDetailsOpComponent],
      providers: [
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: LegalEntitiesService, useClass: LegalEntitiesServiceStub },
        legalEntityFormOpFactory,
        InstallationAccountApplicationStore,
      ],
    })
      .overrideComponent(AddressInputComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LegalEntityDetailsOpComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill form from setState', () => {
    component.form.patchValue(value);
    fixture.detectChanges();
    expect(getInputValue(fixture, '#name')).toBe(value.name);
    expect(getInputValue(fixture, '#type-option0')).toBeTruthy();
    expect(getInputValue(fixture, '#type-option1')).toBeTruthy();
    expect(getInputValue(fixture, '#type-option2')).toBeTruthy();

    expect(getInputValue(fixture, '#address\\.line1')).toBe(value.address.line1);
    expect(getInputValue(fixture, '#address\\.line2')).toBe(value.address.line2);
    expect(getInputValue(fixture, '#address\\.city')).toBe(value.address.city);
    expect(getInputValue(fixture, '#address\\.country')).toBe(value.address.country);
    expect(getInputValue(fixture, '#address\\.postcode')).toBe(value.address.postcode);
    expect(getInputValue(fixture, '#holdingCompanyGroup\\.name')).toBe(value.holdingCompanyGroup.name);
    expect(getInputValue(fixture, '#holdingCompanyGroup\\.registrationNumber')).toBe(
      value.holdingCompanyGroup.registrationNumber,
    );
    expect(getInputValue(fixture, '#holdingCompanyGroup\\.address\\.line1')).toBe(
      value.holdingCompanyGroup.address.line1,
    );
    expect(getInputValue(fixture, '#holdingCompanyGroup\\.address\\.city')).toBe(
      value.holdingCompanyGroup.address.city,
    );
    expect(getInputValue(fixture, '#holdingCompanyGroup\\.address\\.postcode')).toBe(
      value.holdingCompanyGroup.address.postcode,
    );
    expect(component.form.value).toEqual(value);
  });

  it('should submit only if address is filled', () => {
    const mock = cloneDeep(value);
    Object.keys(mock.address).forEach((key) => (mock.address[key] = null));
    component.form.patchValue(mock);
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    changeInputValue(fixture, '#address\\.line1', 'line1');
    changeInputValue(fixture, '#address\\.city', 'city');
    changeInputValue(fixture, '#address\\.country', 'GR');
    buttonClick(fixture);

    expect(navigateSpy).not.toHaveBeenCalled();

    changeInputValue(fixture, '#address\\.postcode', 'post code');
    buttonClick(fixture);

    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should not submit if legal entity exists', () => {
    component.form.patchValue(value);
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    changeInputValue(fixture, '#name', 'Mock Entity');
    buttonClick(fixture);

    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
