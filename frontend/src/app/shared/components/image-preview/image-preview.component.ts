import { Component, inject, input, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ImagePrefiewDialogData, ImagePreviewDialogComponent } from './image-preview-dialog/image-preview-dialog.component';
import { Subscription } from 'rxjs';
import { BreakpointObserver } from '@angular/cdk/layout';

@Component({
  selector: 'app-image-preview',
  templateUrl: './image-preview.component.html',
  styleUrl: './image-preview.component.scss'
})
export class ImagePreviewComponent implements OnDestroy {
  private readonly dialog = inject(MatDialog);

  private readonly observer = inject(BreakpointObserver);

  subscriptions: Subscription = new Subscription();

  minWidth: string = '800px';

  ngOnInit(): void {
    this.observer.observe('(max-width: 768px)').subscribe(res => {
      res.matches ? this.minWidth = '95%' : this.minWidth = '800px';
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  imagePreview = input.required<string>();

  altImage = input.required<string>();

  onPreviewClick(): void {
    const dialogData: ImagePrefiewDialogData = {
      image: this.imagePreview(),
    }

    this.dialog.open(ImagePreviewDialogComponent, { data: dialogData, minWidth: this.minWidth, panelClass: 'custom-container' });
  }
}
