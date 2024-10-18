import { Subscription, catchError, tap, throwError } from 'rxjs';
import { Component, HostBinding, inject, input, model, OnDestroy, OnInit, output, signal } from '@angular/core';
import { FriendshipPermissions, FriendshipResponse } from '../../../core/models/friendship-response.model';
import { FriendshipService } from '../../../core/services/friendship/friendship.service';
import { MatDialog } from '@angular/material/dialog';
import { FriendshipPermissionUpdateDialogComponent } from '../friendship-permission-update-dialog/friendship-permission-update-dialog.component';
import { ConfirmationDialogComponent, ConfirmationDialogData } from '../confirmation-dialog/confirmation-dialog.component';
import { StatisticsServiceService } from '../../../core/services/statistics/statistics-service.service';
import { GenerateReportRequest } from '../../../core/models/generate-report-request.model';
import { GenerateRaportDialogComponent } from '../../../modules/doctor/components/generate-raport-dialog/generate-raport-dialog.component';

@Component({
  selector: 'app-friendship-card',
  templateUrl: './friendship-card.component.html',
  styleUrl: './friendship-card.component.scss'
})
export class FriendshipCardComponent implements OnInit, OnDestroy {
  private readonly friendshipService = inject(FriendshipService);

  private readonly statisticsService = inject(StatisticsServiceService);

  private readonly dialog = inject(MatDialog);

  @HostBinding('style.height.px') get height() {
    return this.friendship().status === 'ACCEPTED' ? 310 : 275;
  }

  friendship = input.required<FriendshipResponse>();
  isDoctor = input<boolean>(false);
  isRejected = model<boolean>(false);

  reportPermisison = signal<boolean>(false);

  emitAcceptedAndDeleted = output<string>();
  emitResend = output<string>();

  subscriptions: Subscription = new Subscription();

  profilePicture = signal<string>('../../../../../assets/images/user.png');

  ngOnInit(): void {
    if (this.friendship().user?.pictureUrl) {
      this.profilePicture.set('data:image/jpeg;base64 ,' + this.friendship().user.pictureUrl);
    }

    this.reportPermisison.set(this.friendship().permissions.activitiesAccess || this.friendship().permissions.parametersAccess || this.friendship().permissions.userMedicineAccess);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  isOverflow(e: HTMLElement): boolean {
    return e.scrollWidth <= e.clientWidth;
  }

  approve() {
    this.subscriptions.add(
      this.friendshipService.acceptInvitation({ invitationId: this.friendship().friendshipId }).subscribe((res) => {
        this.emitAcceptedAndDeleted.emit(res.friendshipId);
      })
    )
  }

  reject() {
    this.subscriptions.add(
      this.friendshipService.rejectInvitation({ invitationId: this.friendship().friendshipId }).subscribe((res) => {
        this.emitAcceptedAndDeleted.emit(res.friendshipId);
      })
    )
  }

  resend() {
    this.subscriptions.add(
      this.friendshipService.resendInvitation({ invitationId: this.friendship().friendshipId }).subscribe((res) => {
        this.emitResend.emit(res.friendshipId);
        this.isRejected.set(false);
      })
    )
  }

  onGenerateClick() {
    const dialogData: FriendshipPermissions = this.friendship().permissions;

    const dialogRef = this.dialog.open(GenerateRaportDialogComponent, { data: dialogData });

    this.subscriptions.add(
      dialogRef.afterClosed().pipe(
        tap((res) => {
          if (res) {
            this.generateReport(res);
          }
        })
      ).subscribe()
    )
  }

  generateReport(dialogRes: Omit<GenerateReportRequest, 'userId'>) {
    const req: GenerateReportRequest = {
      ...dialogRes,
      userId: this.friendship().user.id
    }

    this.subscriptions.add(
      this.statisticsService.generateReport(req).pipe(
        tap((res) => {
          const blob = new Blob([res], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `raport-${this.friendship().user.firstName}-${this.friendship().user.lastName}-${new Date().toLocaleDateString()}.pdf`;
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(url);
          a.remove();
        }),
        catchError((err) => {
          console.log('Error while generating report', err);
          return throwError(() => err);
        })
      ).subscribe()
    );
  }

  cancel() {
    const dialogData: ConfirmationDialogData = {
      title: 'Usuń znajomego',
      message: `Czy na pewno chcesz usunąć ${this.friendship().user.firstName} ${this.friendship().user.lastName} ze znajomych?`,
      acceptButton: 'Usuń'
    }

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, { data: dialogData });

    this.subscriptions.add(
      dialogRef.afterClosed().subscribe(res => {
        if (res) {
          const isAccepted = this.friendship().status === 'ACCEPTED';
          this.subscriptions.add(
            this.friendshipService.cancelInvitation(this.friendship().friendshipId, isAccepted).subscribe((res) => {
              this.emitAcceptedAndDeleted.emit(res.friendshipId);
            })
          )
        }
      })
    )

  }

  editPermissions() {
    const dialogRef = this.dialog.open(FriendshipPermissionUpdateDialogComponent, { data: this.friendship().permissions });

    this.subscriptions.add(
      dialogRef.afterClosed().subscribe(res => {
        if (res) {
          this.subscriptions.add(
            this.friendshipService.updatePermissions(this.friendship().friendshipId, res).subscribe()
          )
        }
      })
    );
  }
}
