import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import * as moment from 'moment';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Constant } from 'src/app/constants/Constant';
import { Route } from 'src/app/constants/Route';
import { DatabaseHelper } from 'src/app/models/DatabaseHelper';
import { Expirable } from 'src/app/models/Expirable';
import { Provider } from 'src/app/models/Provider';
import { Monitoring } from 'src/app/models/Provider-Details/Monitoring';
import { DataService } from 'src/app/services/data.service';
import { MonitoringService } from 'src/app/services/monitoring.service';

@Component({
  selector: 'app-all-provider-monitoring',
  templateUrl: './all-provider-monitoring.component.html',
  styleUrls: ['./all-provider-monitoring.component.css']
})
export class AllProviderMonitoringComponent implements OnInit {

  readonly Constant = Constant;
  readonly Route = Route;

  enableSearch: boolean = false;
  found: number = 0;
  notFound: number = 0;
  pending: number = 0;
  selectedImage:string='';
  totalItems: number = 0;
  filteredTotalItems: number = 0;
  loadingToggle: boolean = false;
  filterloadingToggle: boolean = false;
  monitoringList: Monitoring[] = new Array();
  filteredMonitoringList: Monitoring[] = new Array();
  monitoringEntity: Monitoring = new Monitoring();

  monitoringSearch = new Subject<string>();
  constructor(private monitoringService: MonitoringService,
    private dataService: DataService) {
      this.monitoringSearch.pipe(
        debounceTime(600),
        distinctUntilChanged())
        .subscribe(value => {
          this.monitoringEntity.databaseHelper.currentPage=1;
          this.getAllMonitoring();
        });
    }

  ngOnInit(): void {
    this.getAllMonitoring();
    this.statuswiseMonitoringCount();
  }

  statuswiseMonitoringCount() {
    this.monitoringService.statuswiseMonitoringCount().subscribe(response=>{
      if (response.status) {
        this.found = response.found;
        this.notFound = response.notFound;
        this.pending = response.pending;
      }
    },error=>{
      this.dataService.showToast('Network Error !');
    })
  }

  getAllMonitoring() {
    debugger

      this.loadingToggle = true;
      this.monitoringService.getOngoingMonitoring(0, null, this.monitoringEntity.databaseHelper, null, '').subscribe(response=>{
        if (response.status) {
          this.monitoringList = response.object;
          if (this.monitoringList.length > 0) {
            this.enableSearch = true;
          }
          this.totalItems = response.totalItems;
          this.loadingToggle = false;
        }
      },error=>{
        this.loadingToggle = false;
        this.dataService.showToast('Network Error !');
      })
    
  }

  pageChanged(event: any) {
    debugger
    if (event != this.monitoringEntity.databaseHelper.currentPage) {
      this.monitoringEntity.databaseHelper.currentPage = event;
      this.getAllMonitoring();
    }
  }

  monitoringType: string = ''
  cartToggle: boolean = false;
  toggleCartFunc(type: string) {
    this.monitoringType = type;
    this.filteredMonitoring()
    this.cartToggle = !this.cartToggle;
  }

  databaseHelper: DatabaseHelper = new DatabaseHelper();
  filteredMonitoring() {
    this.filterloadingToggle = true;
    this.monitoringService.getOngoingMonitoring(0, null, this.databaseHelper, null, this.monitoringType).subscribe(response=>{
      if (response.status) {
        this.filteredMonitoringList = response.object;
        this.filteredTotalItems = response.totalItems;
        this.filterloadingToggle = false;
      }
    },error=>{
      this.filterloadingToggle = false;
      this.dataService.showToast('Network Error !');
    })
  }

  filteredPageChanged(event: any) {
    debugger
    if (event != this.databaseHelper.currentPage) {
      this.databaseHelper.currentPage = event;
      this.filteredMonitoring();
    }
  }

  togglecloseCartFunc() {
    this.cartToggle = false;
  }

}
