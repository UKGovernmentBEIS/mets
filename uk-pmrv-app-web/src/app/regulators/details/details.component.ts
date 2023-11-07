import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  EMPTY,
  filter,
  first,
  map,
  Observable,
  shareReplay,
  switchMap,
  takeUntil,
  withLatestFrom,
} from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserId } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { FileType } from '@shared/file-input/file-type.enum';
import { UuidFilePair } from '@shared/file-input/file-upload-event';
import { FileValidators, requiredFileValidator } from '@shared/file-input/file-validators';

import { GovukTableColumn, GovukValidators } from 'govuk-components';

import {
  AuthoritiesService,
  AuthorityManagePermissionDTO,
  RegulatorAuthoritiesService,
  RegulatorUserDTO,
  RegulatorUsersService,
} from 'pmrv-api';

import { saveNotFoundRegulatorError } from '../errors/business-error';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class DetailsComponent implements OnInit {
  basePermissionSelected: string;
  userFullName: string;

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  confirmedAddedRegulator$ = new BehaviorSubject<string>(null);
  allowEditPermissions$: Observable<boolean>;
  userPermissions$: Observable<AuthorityManagePermissionDTO['permissions']>;

  userId$ = this.activatedRoute.paramMap.pipe(map((parameters) => parameters.get('userId')));
  isLoggedUser$ = combineLatest([this.authStore.pipe(selectUserId), this.userId$]).pipe(
    first(),
    map(([loggedInUserId, userId]) => loggedInUserId === userId),
  );
  isInviteUserMode = !this.activatedRoute.snapshot.paramMap.has('userId');

  userRolePermissions$ = this.authoritiesService
    .getRegulatorRoles()
    .pipe(takeUntil(this.destroy$), shareReplay({ bufferSize: 1, refCount: false }));

  permissionGroups$ = this.regulatorAuthoritiesService
    .getRegulatorPermissionGroupLevels()
    .pipe(shareReplay({ bufferSize: 1, refCount: true }));

  form = this.fb.group({
    user: this.fb.group({
      firstName: [
        null,
        [
          GovukValidators.required(`Enter user's first name`),
          GovukValidators.maxLength(255, 'First name should not be more than 255 characters'),
        ],
      ],
      lastName: [
        null,
        [
          GovukValidators.required(`Enter user's last name`),
          GovukValidators.maxLength(255, 'Last name should not be more than 255 characters'),
        ],
      ],
      phoneNumber: [
        null,
        [
          GovukValidators.empty(`Enter user's phone number`),
          GovukValidators.maxLength(255, 'Phone number should not be more than 255 characters'),
        ],
      ],
      mobileNumber: [null, GovukValidators.maxLength(255, 'Mobile number should not be more than 255 characters')],
      email: [
        null,
        [
          GovukValidators.required(`Enter user's email`),
          GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
          GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        ],
      ],
      jobTitle: [
        null,
        [
          GovukValidators.required(`Enter user's job title`),
          GovukValidators.maxLength(255, 'Job title should not be more than 255 characters'),
        ],
      ],
    }),

    signature: [
      null,
      {
        validators: [
          FileValidators.validContentTypes([FileType.BMP], 'must be BMP'),
          FileValidators.maxFileSize(0.2, 'must be smaller than 200KB'),
          FileValidators.maxImageDimensionsSize(240, 140, 'must be 240 x 140 pixels'),
          FileValidators.notEmpty(),
          ...(this.isInviteUserMode ? [requiredFileValidator] : []),
        ],
        updateOn: 'change',
      },
    ],

    permissions: this.fb.group({
      ADD_OPERATOR_ADMIN: ['NONE'],
      ASSIGN_REASSIGN_TASKS: ['NONE'],
      MANAGE_USERS_AND_CONTACTS: ['NONE'],
      MANAGE_VERIFICATION_BODIES: ['NONE'],
      REVIEW_INSTALLATION_ACCOUNT: ['NONE'],
      REVIEW_PERMIT_APPLICATION: ['NONE'],
      PEER_REVIEW_PERMIT_APPLICATION: ['NONE'],
      REVIEW_PERMIT_SURRENDER: ['NONE'],
      PEER_REVIEW_PERMIT_SURRENDER: ['NONE'],
      SUBMIT_PERMIT_REVOCATION: ['NONE'],
      PEER_REVIEW_PERMIT_REVOCATION: ['NONE'],
      REVIEW_PERMIT_NOTIFICATION: ['NONE'],
      PEER_REVIEW_PERMIT_NOTIFICATION: ['NONE'],
      SUBMIT_REVIEW_PERMIT_VARIATION: ['NONE'],
      PEER_REVIEW_PERMIT_VARIATION: ['NONE'],
      REVIEW_PERMIT_TRANSFER: ['NONE'],
      PEER_REVIEW_PERMIT_TRANSFER: ['NONE'],
      SUBMIT_PERMIT_BATCH_REISSUE: ['NONE'],
      REVIEW_AER: ['NONE'],
      REVIEW_VIR: ['NONE'],
      SUBMIT_DRE: ['NONE'],
      PEER_REVIEW_DRE: ['NONE'],
      SUBMIT_NON_COMPLIANCE: ['NONE'],
      PEER_REVIEW_NON_COMPLIANCE: ['NONE'],
      REVIEW_NER: ['NONE'],
      PEER_REVIEW_NER: ['NONE'],
      SUBMIT_DOAL: ['NONE'],
      PEER_REVIEW_DOAL: ['NONE'],
      REVIEW_AIR: ['NONE'],
      REVIEW_EMP_APPLICATION: ['NONE'],
      PEER_REVIEW_EMP_APPLICATION: ['NONE'],
      SUBMIT_AVIATION_DRE: ['NONE'],
      PEER_REVIEW_AVIATION_DRE: ['NONE'],
      AVIATION_ACCOUNT_CLOSURE: ['NONE'],
      SUBMIT_REVIEW_EMP_VARIATION: ['NONE'],
      PEER_REVIEW_EMP_VARIATION: ['NONE'],
      SUBMIT_WITHHOLDING_OF_ALLOWANCES: ['NONE'],
      PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES: ['NONE'],
      SUBMIT_RETURN_OF_ALLOWANCES: ['NONE'],
      PEER_REVIEW_RETURN_OF_ALLOWANCES: ['NONE'],
      REVIEW_AVIATION_AER: ['NONE'],
      SUBMIT_EMP_BATCH_REISSUE: ['NONE'],
      SUBMIT_AVIATION_NON_COMPLIANCE: ['NONE'],
      PEER_REVIEW_AVIATION_NON_COMPLIANCE: ['NONE'],
      REVIEW_AVIATION_VIR: ['NONE'],
    }),
  });

  tableColumns: GovukTableColumn[] = [
    { field: 'task', header: 'Task / Item name', isSortable: false },
    { field: 'type', header: 'Type', isSortable: false },
    { field: 'EXECUTE', header: 'Execute', isSortable: false },
    { field: 'VIEW_ONLY', header: 'View only', isSortable: false },
    { field: 'NONE', header: 'None', isSortable: false },
  ];

  tableRows = [
    {
      permission: 'ADD_OPERATOR_ADMIN',
      task: 'Add operator admin',
      type: 'Users, contacts and verifiers',
    },
    {
      permission: 'ASSIGN_REASSIGN_TASKS',
      task: 'Assign/re-assign tasks',
      type: 'Task assignment',
    },
    {
      permission: 'MANAGE_USERS_AND_CONTACTS',
      task: 'Manage users and contacts',
      type: 'Regulator users and contacts',
    },
    {
      permission: 'MANAGE_VERIFICATION_BODIES',
      task: 'Manage verification bodies',
      type: 'Verification body accounts',
    },
    {
      permission: 'REVIEW_INSTALLATION_ACCOUNT',
      task: 'Review Installation Account',
      type: 'Installation account details',
    },
    {
      permission: 'REVIEW_PERMIT_APPLICATION',
      task: 'Review',
      type: 'Permit application',
    },
    {
      permission: 'PEER_REVIEW_PERMIT_APPLICATION',
      task: 'Peer review',
      type: 'Permit application',
    },
    {
      permission: 'REVIEW_PERMIT_SURRENDER',
      task: 'Review',
      type: 'Permit surrender',
    },
    {
      permission: 'PEER_REVIEW_PERMIT_SURRENDER',
      task: 'Peer review',
      type: 'Permit surrender',
    },
    {
      permission: 'SUBMIT_PERMIT_REVOCATION',
      task: 'Submit',
      type: 'Permit revocation',
    },
    {
      permission: 'PEER_REVIEW_PERMIT_REVOCATION',
      task: 'Peer review',
      type: 'Permit revocation',
    },
    {
      permission: 'REVIEW_PERMIT_NOTIFICATION',
      task: 'Review',
      type: 'Permit notification',
    },
    {
      permission: 'PEER_REVIEW_PERMIT_NOTIFICATION',
      task: 'Peer review',
      type: 'Permit notification',
    },
    {
      permission: 'SUBMIT_REVIEW_PERMIT_VARIATION',
      task: 'Review/Submit',
      type: 'Permit variation',
    },
    {
      permission: 'PEER_REVIEW_PERMIT_VARIATION',
      task: 'Peer review',
      type: 'Permit variation',
    },
    {
      permission: 'REVIEW_PERMIT_TRANSFER',
      task: 'Review',
      type: 'Permit transfer',
    },
    {
      permission: 'PEER_REVIEW_PERMIT_TRANSFER',
      task: 'Peer review',
      type: 'Permit transfer',
    },
    {
      permission: 'SUBMIT_PERMIT_BATCH_REISSUE',
      task: 'Submit',
      type: 'Batch variation',
    },
    {
      permission: 'REVIEW_AER',
      task: 'Review',
      type: 'AER',
    },
    {
      permission: 'REVIEW_VIR',
      task: 'Review',
      type: 'VIR',
    },
    {
      permission: 'SUBMIT_DRE',
      task: 'Submit',
      type: 'DRE',
    },
    {
      permission: 'PEER_REVIEW_DRE',
      task: 'Peer review',
      type: 'DRE',
    },
    {
      permission: 'SUBMIT_NON_COMPLIANCE',
      task: 'Submit',
      type: 'Non compliance',
    },
    {
      permission: 'PEER_REVIEW_NON_COMPLIANCE',
      task: 'Peer review',
      type: 'Non compliance',
    },
    {
      permission: 'REVIEW_NER',
      task: 'Review',
      type: 'NER',
    },
    {
      permission: 'PEER_REVIEW_NER',
      task: 'Peer review',
      type: 'NER',
    },
    {
      permission: 'REVIEW_AIR',
      task: 'Review',
      type: 'Annual improvement report',
    },
    {
      permission: 'SUBMIT_DOAL',
      task: 'Submit',
      type: 'Determination of activity level',
    },
    {
      permission: 'PEER_REVIEW_DOAL',
      task: 'Peer review',
      type: 'Determination of activity level',
    },
    {
      permission: 'REVIEW_EMP_APPLICATION',
      task: 'Review',
      type: 'Emissions plan application',
    },
    {
      permission: 'PEER_REVIEW_EMP_APPLICATION',
      task: 'Peer review',
      type: 'Emissions plan application',
    },
    {
      permission: 'SUBMIT_AVIATION_DRE',
      task: 'Submit',
      type: 'Aviation: DRE',
    },
    {
      permission: 'PEER_REVIEW_AVIATION_DRE',
      task: 'Peer review',
      type: 'Aviation: DRE',
    },
    {
      permission: 'AVIATION_ACCOUNT_CLOSURE',
      task: 'Submit',
      type: 'Close account',
    },
    {
      permission: 'SUBMIT_REVIEW_EMP_VARIATION',
      task: 'Review/Submit',
      type: 'Emissions plan variation',
    },
    {
      permission: 'PEER_REVIEW_EMP_VARIATION',
      task: 'Peer review',
      type: 'Emissions plan variation',
    },
    {
      permission: 'SUBMIT_WITHHOLDING_OF_ALLOWANCES',
      task: 'Submit',
      type: 'Withholding of allowances',
    },
    {
      permission: 'PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES',
      task: 'Peer review',
      type: 'Withholding of allowances',
    },
    {
      permission: 'SUBMIT_RETURN_OF_ALLOWANCES',
      task: 'Submit',
      type: 'Return of allowances',
    },
    {
      permission: 'PEER_REVIEW_RETURN_OF_ALLOWANCES',
      task: 'Peer review',
      type: 'Return of allowances',
    },
    {
      permission: 'REVIEW_AVIATION_AER',
      task: 'Review',
      type: 'Aviation: AER',
    },
    {
      permission: 'SUBMIT_EMP_BATCH_REISSUE',
      task: 'Submit',
      type: 'Aviation: Batch variation',
    },
    {
      permission: 'SUBMIT_AVIATION_NON_COMPLIANCE',
      task: 'Submit',
      type: 'Aviation: Non compliance',
    },
    {
      permission: 'PEER_REVIEW_AVIATION_NON_COMPLIANCE',
      task: 'Peer review',
      type: 'Aviation: Non compliance',
    },
    {
      permission: 'REVIEW_AVIATION_VIR',
      task: 'Review',
      type: 'Aviation: VIR',
    },
  ];

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly activatedRoute: ActivatedRoute,
    private readonly authStore: AuthStore,
    private readonly authoritiesService: AuthoritiesService,
    private readonly router: Router,
    private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService,
    private readonly regulatorUsersService: RegulatorUsersService,
    private readonly destroy$: DestroySubject,
    private readonly businessErrorService: BusinessErrorService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.confirmedAddedRegulator$.pipe(takeUntil(this.destroy$)).subscribe((res) => {
      if (res === null) {
        this.backLinkService.show();
      } else {
        this.backLinkService.hide();
      }
    });
    const routeData = this.activatedRoute.data as Observable<{
      user: RegulatorUserDTO;
      permissions: AuthorityManagePermissionDTO;
    }>;
    this.allowEditPermissions$ = routeData.pipe(map(({ permissions }) => !permissions || permissions.editable));
    this.userPermissions$ = routeData.pipe(map(({ permissions }) => permissions?.permissions));
    routeData
      .pipe(
        takeUntil(this.destroy$),
        filter(({ user, permissions }) => !!user && !!permissions),
      )
      .subscribe(({ user, permissions: { permissions } }) => {
        this.form.patchValue({
          user,
          signature: user.signature?.uuid
            ? ({
                uuid: user.signature.uuid,
                file: { name: user.signature.name } as File,
              } as UuidFilePair)
            : null,

          permissions,
        });
        this.form.get('user').get('email').disable();
        this.userFullName = user.firstName + ' ' + user.lastName;
      });
  }

  setBasePermissions(roleCode: string): void {
    this.basePermissionSelected = roleCode;
    this.userRolePermissions$.subscribe((roles) => {
      const { rolePermissions } = roles.find((role) => role.code === roleCode);
      this.form.get('permissions').patchValue(rolePermissions);
    });

    this.form.markAsDirty();
  }

  submitForm(): void {
    if (this.form.valid) {
      const userEmail = this.form.get('user').get('email').value;
      const userId$ = this.userId$.pipe(first());
      userId$
        .pipe(
          withLatestFrom(this.authStore.pipe(selectUserId)),
          switchMap(([userId, loggedInUserId]) => {
            const signature = this.form.get('signature').value as UuidFilePair;
            if (!signature) {
              this.form.get('signature').setErrors({
                fileNotExist: 'Select a file',
              });
              this.isSummaryDisplayed$.next(true);
              return EMPTY;
            }

            const signatureBlob = signature.file?.size ? signature.file : null;

            if (userId) {
              const payload = { ...this.form.getRawValue() };
              const { signature, ...payloadWithoutSignature } = payload;
              return userId === loggedInUserId
                ? this.regulatorUsersService.updateCurrentRegulatorUser(payloadWithoutSignature, signatureBlob)
                : this.regulatorUsersService.updateRegulatorUserByCaAndId(
                    userId,
                    payloadWithoutSignature,
                    signatureBlob,
                  );
            } else {
              const payload = { ...this.form.get('user').value, permissions: this.form.get('permissions').value };
              const { signature, ...payloadWithoutSignature } = payload;
              return this.regulatorUsersService.inviteRegulatorUserToCA(payloadWithoutSignature, signatureBlob);
            }
          }),
          catchBadRequest([ErrorCodes.USER1001, ErrorCodes.AUTHORITY1005], () => {
            this.form.get('user').get('email').setErrors({
              emailExists: 'This user email already exists in the service',
            });
            this.isSummaryDisplayed$.next(true);

            return EMPTY;
          }),
          catchBadRequest(ErrorCodes.AUTHORITY1003, () =>
            this.businessErrorService.showError(saveNotFoundRegulatorError),
          ),
          switchMap(() => userId$),
        )
        .subscribe((userId) => {
          if (userId) {
            this.router.navigate(['../../regulators'], { relativeTo: this.activatedRoute });
          } else {
            this.confirmedAddedRegulator$.next(userEmail);
          }
        });
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }

  getCurrentUserDownloadUrl(uuid: string): string | string[] {
    return ['..', 'file-download', uuid];
  }

  getDownloadUrl(uuid: string): string | string[] {
    return ['file-download', uuid];
  }
}
