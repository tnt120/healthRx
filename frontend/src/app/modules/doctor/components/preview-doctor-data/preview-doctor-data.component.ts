import { Component, inject, input, OnDestroy, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { Observable, Subscription, tap } from 'rxjs';
import { UnverifiedDoctor } from '../../../../core/models/user/unverified-doctor-model';
import { ImageService } from '../../../../core/services/image/image.service';
import { ImageRequest } from '../../../../core/models/image-request.model';
import { ImageType } from '../../../../core/enums/image-type.enum';
import { ImageResponse } from '../../../../core/models/image-response.model';

@Component({
  selector: 'app-preview-doctor-data',
  templateUrl: './preview-doctor-data.component.html',
  styleUrl: './preview-doctor-data.component.scss'
})
export class PreviewDoctorDataComponent implements OnInit, OnDestroy {
  private readonly imageService = inject(ImageService);

  unverifiedDoctorData = input.required<UnverifiedDoctor>();

  photosPreview = signal<{ front: string | null, back: string | null }>({ front: null, back: null });

  images$!: Observable<ImageResponse[]>;

  subscriptions: Subscription = new Subscription();

  ngOnInit(): void {
    const req: ImageRequest = {
      imageTypes: [ImageType.FRONT_PWZ_PHOTO, ImageType.BACK_PWZ_PHOTO]
    };

    this.images$ = this.imageService.getPictures(req).pipe(
      tap(res => {
        res.forEach(image => {
          if (image.imageType === ImageType.FRONT_PWZ_PHOTO) {
            this.photosPreview.set({ ...this.photosPreview(), front: 'data:image/jpeg;base64 ,' + image.image });
          } else if (image.imageType === ImageType.BACK_PWZ_PHOTO) {
            this.photosPreview.set({ ...this.photosPreview(), back: 'data:image/jpeg;base64 ,' + image.image });
          }
        })
      })
    );
  }

  onImagesPreview(): void {
    this.subscriptions.add(this.images$.subscribe());
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
