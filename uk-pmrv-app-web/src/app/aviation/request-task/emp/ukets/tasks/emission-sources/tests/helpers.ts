import { Location } from '@angular/common';
import { HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingHarness } from '@angular/router/testing';

import { transformAircraftTypeDescription } from '@aviation/shared/pipes/aircraft-type-description.pipe';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { aircraftTypeSearchResults } from './aircraft-type-search-results.fixture';
import { saveEmpFixture } from './save-emp.fixture';

export const basePath = 'http://localhost:8080/api';

export async function navigateToAddAircraftType(harness: RouterTestingHarness, inUse = true) {
  const el = screen.getByTestId(inUse ? 'add-aicraft-in-use-btn' : 'add-aicraft-not-in-use-btn');
  await userEvent.click(el);
  harness.detectChanges();
}
export async function navigateToSearchAircractType(harness: RouterTestingHarness) {
  const el = screen.getByTestId('choose-aircraft-type-link');
  await userEvent.click(el); // navigate to search for aircraft type
  harness.detectChanges();
}

export async function selectAircraftTypeFromSearch(
  harness: RouterTestingHarness,
  httpTestingController: HttpTestingController,
  index: number,
) {
  expect(screen.getByTestId('aircraft-type-search-form')).toBeInTheDocument(); // should render search form
  expect(TestBed.inject(Location).path()).toEqual('/aircraft-type/search?isCurrentlyUsed=1&change=true');
  await userEvent.click(screen.getByTestId('aircraft-type-search-btn')); // send request to search for aircraft types
  const req = httpTestingController.expectOne(basePath + '/v1.0/aircraft-types');
  expect(req.request.method).toEqual('POST');
  req.flush(aircraftTypeSearchResults);
  httpTestingController.verify();
  harness.detectChanges();
  expect(screen.getByTestId('aircraft-types-radio-list')).toBeInTheDocument();
  const selectedAircraftType = aircraftTypeSearchResults.aircraftTypes[index];
  await userEvent.click(screen.getByText(transformAircraftTypeDescription(selectedAircraftType, 'label'))); // select an aircraft type
  await userEvent.click(screen.getByText('Select'));
  harness.detectChanges();
  expect(screen.getByTestId('aicraft-type-form')).toBeInTheDocument(); // should navigate back to aircraft type search form
  expect(screen.getByText(transformAircraftTypeDescription(selectedAircraftType))).toBeInTheDocument();
}
export async function SubmitAircraftTypeForm(
  harness: RouterTestingHarness,
  httpTestingController: HttpTestingController,
) {
  await userEvent.click(screen.getByText('Continue'));
  const req = httpTestingController.expectOne(basePath + '/v1.0/tasks/actions');
  expect(req.request.method).toEqual('POST');
  req.flush(saveEmpFixture);
  httpTestingController.verify();
  harness.detectChanges();
  expect(await screen.findByTestId('emission-sources-page')).toBeInTheDocument();
}
export async function fillAircraftTypeForm(harness: RouterTestingHarness) {
  // fill rest of aircraft type form fields

  const subtype = document.getElementById('subtype') as HTMLInputElement;
  const subtypeValue = 'An aircraft subtype';
  await userEvent.type(subtype, subtypeValue);
  expect(subtype).toHaveValue(subtypeValue);

  const numberOfAircrafts = document.getElementById('numberOfAircrafts') as HTMLInputElement;
  const numberOfAircraftsValue = '12';
  await userEvent.type(numberOfAircrafts, numberOfAircraftsValue);
  expect(numberOfAircrafts).toHaveValue(numberOfAircraftsValue);

  await userEvent.click(document.getElementById('fuelTypes-0'));
  await userEvent.click(document.getElementById('fuelTypes-3'));
  const fuelTypeCheckboxes = document.querySelectorAll('input[type=checkbox]');
  expect(fuelTypeCheckboxes[0]).toBeChecked();
  expect(fuelTypeCheckboxes[1]).not.toBeChecked();
  expect(fuelTypeCheckboxes[2]).not.toBeChecked();
  expect(fuelTypeCheckboxes[3]).toBeChecked();
  harness.detectChanges();
}
export async function fillAircraftTypeFormWithFUMM(harness: RouterTestingHarness) {
  // fill rest of aircraft type form fields

  const subtype = document.getElementById('subtype') as HTMLInputElement;
  const subtypeValue = 'An aircraft subtype';
  await userEvent.type(subtype, subtypeValue);
  expect(subtype).toHaveValue(subtypeValue);

  const numberOfAircrafts = document.getElementById('numberOfAircrafts') as HTMLInputElement;
  const numberOfAircraftsValue = '12';
  await userEvent.type(numberOfAircrafts, numberOfAircraftsValue);
  expect(numberOfAircrafts).toHaveValue(numberOfAircraftsValue);

  await userEvent.click(document.getElementById('fuelTypes-0'));
  await userEvent.click(document.getElementById('fuelTypes-3'));
  const fuelTypeCheckboxes = document.querySelectorAll('input[type=checkbox]');
  expect(fuelTypeCheckboxes[0]).toBeChecked();
  expect(fuelTypeCheckboxes[1]).not.toBeChecked();
  expect(fuelTypeCheckboxes[2]).not.toBeChecked();
  expect(fuelTypeCheckboxes[3]).toBeChecked();
  expect(document.getElementById('fuelConsumptionMeasuringMethod')).toBeInTheDocument();
  const methodB = document.getElementById('fuelConsumptionMeasuringMethod-option2');
  await userEvent.click(methodB);
  harness.detectChanges();
}
