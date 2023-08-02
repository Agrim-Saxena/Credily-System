import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { NgxMaskModule } from 'ngx-mask';
import { HeaderModule } from '../header/header.module';
import { SharedModule } from '../modules/shared/shared/shared.module';
import { AllProviderExpirableComponent } from './all-provider-expirable/all-provider-expirable.component';
import { ExpirablesComponent } from './expirables.component';
import { ProviderDashboardComponent } from './provider-dashboard/provider-dashboard.component';
import { ExpirablesRoutingModule } from './expirables-routing.module';
import { AllProviderMonitoringComponent } from './all-provider-monitoring/all-provider-monitoring.component';
import { ProviderAuditrailDetailsComponent } from './provider-auditrail-details/provider-auditrail-details.component';
import { ProviderDocumentVaultComponent } from './provider-document-vault/provider-document-vault.component';
import { FileDragNDropDirective } from '../services/FileDragNDropDirective';
import { AllProviderElementComponent } from './all-provider-element/all-provider-element.component';
import { AllProviderFacilityComponent } from './all-provider-facility/all-provider-facility.component';


@NgModule({
  declarations: [
    AllProviderExpirableComponent,
    ProviderDashboardComponent,
    ExpirablesComponent,
    AllProviderMonitoringComponent,
    ProviderAuditrailDetailsComponent,
    ProviderDocumentVaultComponent,
    FileDragNDropDirective,
    AllProviderElementComponent,
    AllProviderFacilityComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    ExpirablesRoutingModule,
    HeaderModule,
    SharedModule,
    NgxMaskModule.forChild(),
    AngularMultiSelectModule,
    
]
})
export class ExpirablesModule { }
