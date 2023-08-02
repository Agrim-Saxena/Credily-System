import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AllProviderExpirableComponent } from './all-provider-expirable/all-provider-expirable.component';
import { ProviderDashboardComponent } from './provider-dashboard/provider-dashboard.component';
import { ExpirablesComponent } from './expirables.component';
import { AllProviderMonitoringComponent } from './all-provider-monitoring/all-provider-monitoring.component';
import { ProviderAuditrailDetailsComponent } from './provider-auditrail-details/provider-auditrail-details.component';
import { ProviderDocumentVaultComponent } from './provider-document-vault/provider-document-vault.component';
import { AllProviderElementComponent } from './all-provider-element/all-provider-element.component';
import { AllProviderFacilityComponent } from './all-provider-facility/all-provider-facility.component';

const routes: Routes = [{ 
  path: '', component: ExpirablesComponent,
  children:[
    {path: 'all-provider-expirable', component: AllProviderExpirableComponent},
    {path: 'all-provider-element', component: AllProviderElementComponent},
    {path: 'all-provider-facility', component: AllProviderFacilityComponent},
    {path: 'all-provider-monitoring', component: AllProviderMonitoringComponent},
    {path: 'provider-dashboard', component: ProviderDashboardComponent},
    {path: 'document-vault', component: ProviderDocumentVaultComponent},
    {path: 'audittrail-details', component: ProviderAuditrailDetailsComponent}
  ]
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})

export class ExpirablesRoutingModule { }
