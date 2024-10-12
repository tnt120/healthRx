import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DividerComponent } from './components/divider/divider.component';
import { MyMaterialModule } from '../material';
import { ForbiddenRegexValidatorDirective } from './directives/forbiddenRegexValidator/forbidden-regex-validator.directive';
import { RepeatPasswordValidatorDirective } from './directives/repeatPasswordValidator/repeat-password-validator.directive';
import { ParameterTailComponent } from './components/parameter-tail/parameter-tail.component';
import { SettingsComponent } from './components/settings/settings.component';
import { HeaderBarComponent } from './components/header-bar/header-bar.component';
import { HelloItemComponent } from './components/header-bar/hello-item/hello-item.component';
import { ContainerLayoutComponent } from './components/container-layout/container-layout.component';
import { SectionLayoutComponent } from './components/container-layout/section-layout/section-layout.component';
import { CustomSnackbarComponent } from './components/custom-snackbar/custom-snackbar.component';
import { CollapsingContainerLayoutComponent } from './components/container-layout/collapsing-container-layout/collapsing-container-layout.component';
import { TableComponent } from './components/table/table.component';
import { DayTailComponent } from './components/day-tail/day-tail.component';
import { ConfirmationDialogComponent } from './components/confirmation-dialog/confirmation-dialog.component';
import { OnlyNumbersDirective } from './directives/onlyNumbers/only-numbers.directive';
import { TabsComponent } from './components/tabs/tabs.component';
import { TabComponent } from './components/tabs/tab/tab.component';
import { SettingsUserProfileComponent } from './components/settings/settings-user-profile/settings-user-profile.component';
import { UserProfileEditDialogComponent } from './components/settings/user-profile-edit-dialog/user-profile-edit-dialog.component';
import { PasswordChangeDialogComponent } from './components/settings/password-change-dialog/password-change-dialog.component';
import { NotificationsManageComponent } from './components/settings/notifications-manage/notifications-manage.component';
import { NotificationsManageDialogComponent } from './components/settings/notifications-manage/notifications-manage-dialog/notifications-manage-dialog.component';
import { ParametersManageComponent } from './components/settings/parameters-manage/parameters-manage.component';
import { ParametersManageDialogComponent } from './components/settings/parameters-manage/parameters-manage-dialog/parameters-manage-dialog.component';
import { FriendshipCardComponent } from './components/friendship-card/friendship-card.component';
import { FriendshipPermissionUpdateDialogComponent } from './components/friendship-permission-update-dialog/friendship-permission-update-dialog.component';
import { FriendshipFilterPanelComponent } from './components/friendship-filter-panel/friendship-filter-panel.component';
import { ChatComponent } from './components/chat/chat.component';
import { ChatConversationsTabComponent } from './components/chat/chat-conversations-tab/chat-conversations-tab.component';
import { ConversationItemComponent } from './components/chat/chat-conversations-tab/conversation-item/conversation-item.component';
import { MessagesContainerComponent } from './components/chat/messages-container/messages-container.component';
import { LineChartComponent } from './components/line-chart/line-chart.component';
import { BarChartComponent } from './components/bar-chart/bar-chart.component';
import { ChoosePhotoButtonComponent } from './components/choose-photo-button/choose-photo-button.component';
import { ProfilePictureChangeComponent } from './components/settings/profile-picture-change/profile-picture-change.component';

@NgModule({
  declarations: [
    DividerComponent,
    ForbiddenRegexValidatorDirective,
    RepeatPasswordValidatorDirective,
    ParameterTailComponent,
    SettingsComponent,
    HeaderBarComponent,
    HelloItemComponent,
    ContainerLayoutComponent,
    SectionLayoutComponent,
    CustomSnackbarComponent,
    CollapsingContainerLayoutComponent,
    TableComponent,
    DayTailComponent,
    ConfirmationDialogComponent,
    OnlyNumbersDirective,
    TabsComponent,
    TabComponent,
    SettingsUserProfileComponent,
    UserProfileEditDialogComponent,
    PasswordChangeDialogComponent,
    NotificationsManageComponent,
    NotificationsManageDialogComponent,
    ParametersManageComponent,
    ParametersManageDialogComponent,
    FriendshipCardComponent,
    FriendshipPermissionUpdateDialogComponent,
    FriendshipFilterPanelComponent,
    ChatComponent,
    ChatConversationsTabComponent,
    ConversationItemComponent,
    MessagesContainerComponent,
    LineChartComponent,
    BarChartComponent,
    ChoosePhotoButtonComponent,
    ProfilePictureChangeComponent,
  ],
  imports: [
    CommonModule,
    MyMaterialModule
  ],
  exports: [
    DividerComponent,
    ForbiddenRegexValidatorDirective,
    RepeatPasswordValidatorDirective,
    ParameterTailComponent,
    SettingsComponent,
    HeaderBarComponent,
    ContainerLayoutComponent,
    SectionLayoutComponent,
    CollapsingContainerLayoutComponent,
    TableComponent,
    DayTailComponent,
    OnlyNumbersDirective,
    FriendshipCardComponent,
    FriendshipFilterPanelComponent,
    ChatComponent,
    LineChartComponent,
    BarChartComponent,
    ChoosePhotoButtonComponent,
  ]
})
export class SharedModule { }
