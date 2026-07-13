import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';

import { PasswordService } from './password.service';

describe('PasswordService', () => {
  let service: PasswordService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PasswordService],
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(PasswordService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return strong error if the password is not strong', () => {
    const formControl = new FormControl('password', [(control) => service.strong(control)]);
    formControl.updateValueAndValidity();

    expect(formControl.errors.weakPassword).toBeTruthy();
  });
});
