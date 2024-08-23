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
    CollapsingContainerLayoutComponent
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
    CollapsingContainerLayoutComponent
  ]
})
export class SharedModule { }
