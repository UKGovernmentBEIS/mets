import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Input,
  OnInit,
  Optional,
  Self,
  ViewChild,
} from '@angular/core';
import { ControlValueAccessor, FormGroupDirective, NgControl, NgForm, UntypedFormControl } from '@angular/forms';

import { BehaviorSubject, combineLatest, filter, map, merge, Observable, startWith, tap, withLatestFrom } from 'rxjs';

import { FormService } from 'govuk-components';

import { FileUploadService } from './file-upload.service';
import { FileUpload, FileUploadEvent } from './file-upload-event';

/*
  eslint-disable
  @typescript-eslint/no-empty-function
 */
@Component({
  selector: 'app-file-input',
  templateUrl: './file-input.component.html',
  styleUrl: '../multiple-file-input/multiple-file-input.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FileInputComponent implements OnInit, ControlValueAccessor {
  @Input() headerSize: 'm' | 's';
  @Input() listTitle: string;
  @Input() label: string;
  @Input() text: string;
  @Input() showFilesizeHint = true;
  @Input() hint: string;
  @Input() accepted = '*/*';
  @Input() downloadUrl: (uuid: string) => string | string[];
  @Input() fileNameTransformer!: (file: File | null) => File | null;

  uploadedFiles$: Observable<FileUploadEvent[]>;
  isDisabled: boolean;
  onFileBlur: () => any;

  @ViewChild('input') private readonly input: ElementRef<HTMLInputElement>;
  private value$ = new BehaviorSubject<FileUpload>(null);
  private onChange: (value: FileUpload) => any;

  constructor(
    @Self() @Optional() private readonly ngControl: NgControl,
    private readonly formService: FormService,
    private readonly fileUploadService: FileUploadService,
    @Optional() private readonly root: FormGroupDirective,
    @Optional() private readonly rootNgForm: NgForm,
  ) {
    ngControl.valueAccessor = this;
  }

  get control(): UntypedFormControl {
    return this.ngControl.control as UntypedFormControl;
  }

  get id(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  get shouldDisplayErrors(): boolean {
    return this.control?.invalid && (!this.form || this.form.submitted);
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.root ?? this.rootNgForm;
  }

  ngOnInit(): void {
    this.uploadedFiles$ = merge(
      combineLatest([
        this.value$.pipe(map((value) => (value ? { ...value, progress: null } : null))),
        this.control.statusChanges.pipe(
          map(() => this.control.errors),
          startWith(this.control.errors),
        ),
      ]).pipe(map(([value, errors]) => (value ? { ...value, errors } : null))),
      this.fileUploadService.uploadProgress$.pipe(
        withLatestFrom(this.value$),
        filter(([fileEvent, value]) => fileEvent.file === value?.file),
        tap(([uploadEvent, value]) => {
          if (uploadEvent.uuid) {
            this.onChange({ ...value, uuid: uploadEvent.uuid, dimensions: value.dimensions });
          }
        }),
        map(([fileEvent]) => fileEvent),
      ),
    ).pipe(
      map((fileEvent) =>
        fileEvent
          ? [
              {
                ...fileEvent,
                ...(fileEvent.uuid && { downloadUrl: this.downloadUrl(fileEvent.uuid) }),
              },
            ]
          : [],
      ),
    );
  }

  registerOnChange(onChange: (value: FileUpload) => any): void {
    this.onChange = (value) => {
      this.value$.next(value);
      onChange(value);
    };
  }

  registerOnTouched(onBlur: () => any): void {
    this.onFileBlur = onBlur;
  }

  writeValue(value: FileUploadEvent): void {
    this.value$.next(value);
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }

  onFileChange(event: Event): void {
    const files = (event.target as HTMLInputElement).files;

    if (files.length === 1) {
      const renamedFile = this.fileNameTransformer ? this.fileNameTransformer(files[0]) : files[0];
      if (this.isImage(renamedFile)) {
        const fileAsDataURL = window.URL.createObjectURL(renamedFile);
        this.getImageFileDimensionsResolver(fileAsDataURL)
          .then((dimensions) => {
            this.uploadFile(renamedFile, dimensions);
          })
          .catch(() => {
            this.uploadFile(renamedFile, null);
          });
      } else {
        this.uploadFile(renamedFile, null);
      }
    }
  }

  onFileDeleteClick(): void {
    this.onChange(null);
    this.input.nativeElement.value = null;
  }

  private uploadFile(file: File, dimensions): void {
    this.onChange({ file, uuid: null, dimensions });
  }

  private isImage(file: File) {
    return file['type'].split('/')[0] == 'image';
  }

  private getImageFileDimensionsResolver = (dataURL) =>
    new Promise<{ width: number; height: number }>((resolve, reject) => {
      const img = new Image();
      img.onload = () => {
        resolve({
          width: img.width,
          height: img.height,
        });
      };
      img.onerror = function () {
        reject();
      };
      img.src = dataURL;
    });
}
