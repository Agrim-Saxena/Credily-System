import { Component, OnInit } from '@angular/core';

import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Route } from 'src/app/constants/Route';
import { DatabaseHelper } from 'src/app/models/DatabaseHelper';
import { Audit } from 'src/app/models/Provider-Details/Audit';
import { DataService } from 'src/app/services/data.service';
import { ProviderService } from 'src/app/services/provider.service';

@Component({
  selector: 'app-provider-auditrail-details',
  templateUrl: './provider-auditrail-details.component.html',
  styleUrls: ['./provider-auditrail-details.component.css']
})
export class ProviderAuditrailDetailsComponent implements OnInit {

  actionFrom: string | null = null;
  readonly Route = Route;
  auditTrailSearch = new Subject<string>();
  constructor( private providerSerivce: ProviderService,
    public dataService: DataService,
    private _routeParams: ActivatedRoute, ) { 
    if (this._routeParams.snapshot.queryParamMap.has('uuid') && this._routeParams.snapshot.queryParamMap.has('actionFrom')) {
      this.uuid = this._routeParams.snapshot.queryParamMap.get('uuid');
      this.actionFrom = this._routeParams.snapshot.queryParamMap.get('actionFrom');
    } else {
      this.uuid = this.dataService.getUserUUID();
    }

    this.auditTrailSearch.pipe(
      debounceTime(600),
      distinctUntilChanged())
      .subscribe(value => {
        this.AuditDatabaseHelper.currentPage=1;
        this.getAuditTrail();
      });

  }

  ngOnInit(): void {
    this.getAuditTrail();
  }
  enableSearch:boolean=false;
  totalAuditTrail: number = 0;
  AuditList: Audit[] = new Array();
  uuid:any
  AuditDatabaseHelper : DatabaseHelper = new DatabaseHelper();
  loadingAuditTrail:boolean=false;
  getAuditTrail(){
    this.loadingAuditTrail = true;
    this.providerSerivce.getAuditTrail(this.uuid, this.AuditDatabaseHelper,this.actionFrom).subscribe((response) => {
      this.AuditList = response.list;
      this.totalAuditTrail = response.totalItems;
      if(this.AuditList.length>0){
        debugger
        this.enableSearch = true
      }
      this.loadingAuditTrail = false;
      
    }, error=>{
      this.loadingAuditTrail = false;
    })
  }
  pageChanged(page: any) {
    if (page != this.AuditDatabaseHelper.currentPage) {
      this.AuditDatabaseHelper.currentPage = page;
      this.getAuditTrail();
    }
  }

  gotoPreviousPage(){
    window.history.back();
  }

}
