import { Component, inject, model, OnDestroy, OnInit, signal } from '@angular/core';
import { ImageService } from '../../../../core/services/image/image.service';
import { Subscription } from 'rxjs';
import { ImageType } from '../../../../core/enums/image-type.enum';
import { UserResponse } from '../../../../core/models/user/user-response.model';
import { CustomSnackbarService } from '../../../../core/services/custom-snackbar/custom-snackbar.service';

@Component({
  selector: 'app-profile-picture-change',
  templateUrl: './profile-picture-change.component.html',
  styleUrl: './profile-picture-change.component.scss'
})
export class ProfilePictureChangeComponent implements OnInit, OnDestroy {
  private readonly imageService = inject(ImageService);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  userProfileData = model.required<UserResponse | null>();

  subscriptions: Subscription[] = [];

  pictureForUpdate = signal<File | null>(null);
  
  profilePicturePreview = signal<string | null>(null);

  currPicturePreview = signal<string | null>(null);

  ngOnInit(): void {
    this.subscriptions.push(
      this.imageService.getProfilePicture().subscribe(res => {
        if (res.image) {
          this.currPicturePreview.set('data:image/jpeg;base64 ,' + res.image);
          this.profilePicturePreview.set('data:image/jpeg;base64 ,' + res.image);
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  changePicture(event: { file: File, preview: string }) {
    this.pictureForUpdate.set(event.file);
    this.profilePicturePreview.set(event.preview);
  }

  onReset(): void {
    this.pictureForUpdate.set(null);
    this.profilePicturePreview.set(this.currPicturePreview());
  }

  onSave(): void {
    if (this.pictureForUpdate() && this.userProfileData()?.email){
      this.subscriptions.push(
        this.imageService.uploadPhotos([ImageType.PROFILE], [this.pictureForUpdate()!], this.userProfileData()?.email!).subscribe({
          next : res => {
            this.currPicturePreview.set(this.profilePicturePreview());
            this.pictureForUpdate.set(null);
            this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie zaktualizowano zdjęcie profilowe.', type: 'success', duration: 2500 });
          },
          error: () => {
            this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Wystąpił błąd podczas aktualizacji zdjęcia profilowego.', type: 'error', duration: 2500 });
          }
        })
      );
    }
  }
}