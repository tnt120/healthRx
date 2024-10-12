import { ImageType } from './../../enums/image-type.enum';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ImageResponse } from '../../models/image-response.model';

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  private readonly apiUrl = `${environment.apiUrl}/image`;

  private readonly http = inject(HttpClient);

  uploadPhotos(types: ImageType[], files: File[], email: string): Observable<ImageResponse> {
    const formData = new FormData();

    formData.append('email', email);
    files.forEach((file, index) => {
      formData.append('images', file);
      formData.append('imageTypes', types[index]);
    });

    return this.http.post<ImageResponse>(`${this.apiUrl}`, formData);
  }

  getProfilePicture(): Observable<ImageResponse> {
    return this.http.get<ImageResponse>(`${this.apiUrl}/profile`);
  }
}
