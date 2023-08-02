import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { EnterpriseDashboardRoutingModule } from './enterprise-dashboard-routing.module';
import { EnterpriseDashboardComponent } from './enterprise-dashboard.component';
import { HeaderModule } from '../header/header.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { FormsModule } from '@angular/forms';
import { NgxMaskModule } from 'ngx-mask';
import { GooglePlaceModule } from 'ngx-google-places-autocomplete';
import { SharedModule } from '../modules/shared/shared/shared.module';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { AngularFireDatabaseModule } from '@angular/fire/compat/database';
import { AngularFireAuthModule } from '@angular/fire/compat/auth';
import { AngularFireModule } from '@angular/fire/compat';
import { initializeApp, provideFirebaseApp } from '@angular/fire/app';
import { getFirestore, provideFirestore } from '@angular/fire/firestore';
import { environment } from 'src/environments/environment';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [
    EnterpriseDashboardComponent,
    DashboardComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    EnterpriseDashboardRoutingModule,
    HeaderModule,
    NgxMaskModule.forChild(),
    GooglePlaceModule,
    SharedModule,
    AngularMultiSelectModule,
    AngularFireDatabaseModule,
    AngularFireAuthModule,
    AngularFireModule.initializeApp(environment.firebaseConfig),
    provideFirebaseApp(() => initializeApp(environment.firebaseConfig)),
    provideFirestore(() => getFirestore()),
  ]
})
export class EnterpriseDashboardModule { }
