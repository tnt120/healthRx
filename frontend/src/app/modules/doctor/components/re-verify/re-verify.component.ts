import { Component, inject, input, OnInit, signal } from '@angular/core';
import { DoctorService } from '../../../../core/services/doctor/doctor.service';
import { ImageService } from '../../../../core/services/image/image.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FormErrorsService } from '../../../../core/services/form-errors/form-errors.service';
import { ImageType } from '../../../../core/enums/image-type.enum';
import { UserResponse } from '../../../../core/models/user/user-response.model';
import { ReVerifyDoctorRequest } from '../../../../core/models/user/re-verify-doctor-request.model';
import { catchError, mergeMap, tap, throwError } from 'rxjs';
import { CustomSnackbarService } from '../../../../core/services/custom-snackbar/custom-snackbar.service';

@Component({
  selector: 'app-re-verify',
  templateUrl: './re-verify.component.html',
  styleUrl: './re-verify.component.scss'
})
export class ReVerifyComponent implements OnInit {
  private readonly doctorService = inject(DoctorService);

  private readonly imageService = inject(ImageService);

  private readonly formErrorsService = inject(FormErrorsService);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  user = input.required<UserResponse>();

  reVerifyForm = new FormGroup({
    numberPESEL: new FormControl('', Validators.required),
    numberPWZ: new FormControl('', Validators.required),
    idPhotoFrontUrl: new FormControl<File | null>(null, Validators.required),
    idPhotoBackUrl: new FormControl<File | null>(null, Validators.required),
  });

  formErrors = signal<any>({
    reVerifyForm: {
      numberPESEL: '',
      numberPWZ: '',
    },
  });

  previewImage = signal<{ [key in Exclude<ImageType, ImageType.PROFILE>]: File | null }>({
    [ImageType.FRONT_PWZ_PHOTO]: null,
    [ImageType.BACK_PWZ_PHOTO]: null
  });

  ngOnInit(): void {
    this.reVerifyForm.controls.numberPESEL.setValue(this.user().unverifiedDoctor!.numberPESEL!);
    this.reVerifyForm.controls.numberPWZ.setValue(this.user().unverifiedDoctor!.numberPWZ!);
  }

  updateErrorMessage(form: FormGroup, formName: string, field: string, name: string) {
    const control = form.get(field as string)!;

    if (control) {
      this.updateFormErrors(formName, field, this.formErrorsService.getErrorMessage(control, name));
    }
  }

  updateFormErrors(formName: string, field: string, message: string) {
    this.formErrors.update(errors => ({
      ...errors,
      [formName]: {
        ...errors[formName],
        [field]: message
      }
    }));
  }

  changePhoto(event: { file: File, preview: string }, type: string) {
    let photoType = type as ImageType;
    if (type === 'USER_PROFILE') photoType = ImageType.PROFILE;

    this.previewImage.update(images => ({
      ...images,
      [photoType]: event.preview
    }));

    switch (type) {
      case ImageType.FRONT_PWZ_PHOTO:
        this.reVerifyForm.controls.idPhotoFrontUrl.setValue(event.file);
        break;
      case ImageType.BACK_PWZ_PHOTO:
        this.reVerifyForm.controls.idPhotoBackUrl.setValue(event.file);
        break;
    }
  }

  onSubmit(): void {
    const idPhotoFront = this.reVerifyForm.controls.idPhotoFrontUrl.value!;
    const idPhotoBack = this.reVerifyForm.controls.idPhotoBackUrl.value!;
    const types = [ImageType.FRONT_PWZ_PHOTO, ImageType.BACK_PWZ_PHOTO];
    const files = [idPhotoFront, idPhotoBack];

    const uploadImagesReq$ = this.imageService.uploadPhotos(types, files, this.user().email);

    const reVerifyReq: ReVerifyDoctorRequest = {
      numberPESEL: this.reVerifyForm.controls.numberPESEL.value!,
      numberPWZ: this.reVerifyForm.controls.numberPWZ.value!
    };

    const reVerify$ = this.doctorService.reVerify(reVerifyReq);

    uploadImagesReq$.pipe(
      mergeMap(() => reVerify$),
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie wysłano dane', type: 'success', duration: 2500 });
        window.location.href = '/doctor';
      }),
      catchError(err => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Wystąpił błąd podczas wysyłania danych', type: 'error', duration: 2500 });

        return throwError(() => err);
      })
    ).subscribe()
  }
}
