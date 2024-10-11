import { Component, input, model, output, signal } from '@angular/core';

@Component({
  selector: 'app-choose-photo-button',
  templateUrl: './choose-photo-button.component.html',
  styleUrl: './choose-photo-button.component.scss'
})
export class ChoosePhotoButtonComponent {
  imagesToSave = signal<File | null>(null);

  imagePreview = model<string | null>(null);

  isProfile = input<boolean>(false);

  label = input<string | null>(null);

  photoChange = output<{ file: File, preview: string }>();

  onFileSelect(event: any) {
    const file: File = event.target?.files[0];

    if (file) {
      const reader = new FileReader();

      reader.onload = (e: any) => {
        this.imagePreview.set(e.target.result);
        this.imagesToSave.set(file);
        this.photoChange.emit({ file, preview: e.target.result });
      }

      reader.readAsDataURL(file);
    }
  }
}
