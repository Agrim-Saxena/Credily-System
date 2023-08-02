import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Constant } from 'src/app/constants/Constant';
import { Route } from 'src/app/constants/Route';
import { AttachmentService } from 'src/app/services/attachment.service';
import { ProviderService } from 'src/app/services/provider.service';
import { DataService } from 'src/app/services/data.service';
import { Provider } from 'src/app/models/Provider';
import { Attachment } from 'src/app/models/common-json/Attachment';
import { DatabaseHelper } from 'src/app/models/DatabaseHelper';
import * as moment from 'moment';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { ExpirableService } from 'src/app/services/expirable.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-all-provider-element',
  templateUrl: './all-provider-element.component.html',
  styleUrls: ['./all-provider-element.component.css']
})
export class AllProviderElementComponent implements OnInit {

  readonly Constant = Constant;
  readonly Route = Route;
  provider: Provider = new Provider();
  providerUuid: any[] = new Array();
  attachmentSearch = new Subject<string>();
  constructor(
    private sanitizer: DomSanitizer,
    public dataService: DataService,
    public router: Router,
    private expirableService:ExpirableService,
    private providerSerivce : ProviderService
  ) { 
      this.attachmentSearch.pipe(
        debounceTime(600),
        distinctUntilChanged())
        .subscribe(value => {
          this.databaseHelperAttachment.currentPage=1;
          this.getExpirableByElement();
      });

  }

  dropdownSettingsName!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  providerNameList: any[] = new Array();
  selectedProvider: any[] = new Array();

  dropdownSettingsSpecialty!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  specialtyList: any[] = new Array();
  selectedSpecialty: any[] = new Array();

  dropdownSettingsStateName!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  stateNameList: any[] = new Array();
  selectedStateName: any[] = new Array();

  dropdownSettingsStatus!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  statusList: any[] = new Array();
  selectedStatus: any[] = new Array();
  
  dropdownSettingsCredentials!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  credentialsList: any[] = new Array();
  selectedCredentials: any[] = new Array(); 

  ngOnInit() {
    this.dropdownSettingsName = {
      singleSelection: false,
      text: 'Select Provider',
      enableSearchFilter: true,
      autoPosition: false,
      badgeShowLimit: 1
    };

    this.dropdownSettingsSpecialty = {
      singleSelection: true,
      text: 'Select Specialty',
      enableSearchFilter: true,
      autoPosition: false,
      badgeShowLimit: 1
    };

    this.dropdownSettingsStateName = {
      singleSelection: false,
      text: 'Select State',
      enableSearchFilter: true,
      autoPosition: false,
      badgeShowLimit: 1
    };

    this.dropdownSettingsStatus = {
      singleSelection: true,
      text: 'Select Status',
      enableSearchFilter: true,
      autoPosition: false,
      badgeShowLimit: 1
    };

    this.dropdownSettingsCredentials = {
      singleSelection: true,
      text: 'Select Credentials',
      enableSearchFilter: true,
      autoPosition: false,
      badgeShowLimit: 1
    };

    var temp1: { id: string, itemName: string } = { id: 'upToDate', itemName: 'Up To Date' };
    this.statusList.push(temp1);
    var temp2: { id: string, itemName: string } = { id: 'aboutToExpire', itemName: 'Expiring Soon' };
    this.statusList.push(temp2);
    var temp3: { id: string, itemName: string } = { id: 'expired', itemName: 'Expired' };
    this.statusList.push(temp3);
    
    this.getExpirableByElement();
    this.statusWiseElementCount();
  }


  uuid:any;
  states: any[] = new Array();
  docType: any[] = new Array();
  attachmentLoaderToggle: boolean = false
  attachmentList: any[] = new Array();
  attachment: Attachment = new Attachment();
  filterAttachmentList: any[] = new Array();
  databaseHelperAttachment: DatabaseHelper = new DatabaseHelper();
  totalAttachment: number = 0;
  totalFilterAttachment: number = 0;
  enableSearch: boolean = false;

  getExpirableByElement() {
    debugger
    this.attachmentLoaderToggle = true;
    if (this.nameFilterToggle) {
      this.providerUuid = [];
      this.credentialstype = ''
      if (this.selectedProvider.length > 0) {
        this.selectedProvider.forEach(e => {
          this.providerUuid.push(e.id);
        })
      }
      this.nameFilterToggle = false;
    }

    if (this.stateFilterToggle) {
      this.states = [];
      this.credentialstype = '';
      if (this.selectedStateName.length > 0) {
        this.selectedStateName.forEach((element: any) => {
          this.states.push(element.itemName);
        })
      } else {
        this.states = [];
      }

      this.stateFilterToggle = false;
    }

    if (this.credentialsFilterToggle) {
      this.docType = [];
      this.credentialstype = '';
      if (this.selectedCredentials.length > 0) {
        this.selectedCredentials.forEach((element: any) => {
          this.docType.push(element.itemName);
        })
      } else {
        this.docType = [];
      }

      this.credentialsFilterToggle = false;
    }

    this.expirableService.getAllExpirables(this.credentialstype, this.databaseHelperAttachment, this.providerUuid, this.states, this.specialty, 0, this.docType).subscribe((response) => {
      if (response.object.length > 0) {
        this.enableSearch = true;
      }
      
      this.attachmentList = response.object;

      this.totalAttachment = response.totalItems;

      if (this.attachmentList != undefined) {
        this.attachmentList.forEach((e: any) => {
          const diff: any = moment(e.expirationDate).diff(moment().format('YYYY-MM-DD'), 'days')
          if (diff >= 90) {
            e.attachmentStatus = this.Constant.ATTACHMENT_UP_TO_DATE;
          } else if (diff > 0 && diff <= 90) {
            e.attachmentStatus = this.Constant.ATTACHMENT_EXPIRING_SOON;
          } else if (diff < 0) {
            e.attachmentStatus = this.Constant.ATTACHMENT_EXPIRED;
          }
        });
      }
      this.attachmentLoaderToggle = false
    }, error => {
      this.attachmentLoaderToggle = false
    });
  }


  uptodateCount:number=0;
  expiringSoonCount:number=0;
  expiredCount:number=0;
  statusWiseElementCount() {
    this.isTrackable = 1;
    this.expirableService.getExpirableCount().subscribe(response=>{
      this.uptodateCount = response.upToDate;
      this.expiringSoonCount = response.aboutToExpire;
      this.expiredCount = response.expired;
    },error=>{
      this.dataService.showToast('Network Error !');
    })
  }
  isTrackable :number =0;
  filterDatabaseHelper: DatabaseHelper = new DatabaseHelper();
  filterloadingToggle:boolean=false;
  filteredAttachment() {
    this.filterloadingToggle = true;
    this.isTrackable = 1;
    this.expirableService.getAllExpirables(this.credentialstype, this.filterDatabaseHelper, [], [], '', 0,'').subscribe(response=>{
      this.filterAttachmentList = response.object;
        this.totalFilterAttachment = response.totalItems;
      this.filterloadingToggle = false;
    },(error)=>{
      this.filterloadingToggle = false;
      this.dataService.showToast('Network Error !');
    })
  }

  pageChanged(event: any) {
    debugger
    if (event != this.databaseHelperAttachment.currentPage) {
      this.databaseHelperAttachment.currentPage = event;
      this.getExpirableByElement();
    }
  }

  filteredPageChanged(event: any) {
    debugger
    if (event != this.filterDatabaseHelper.currentPage) {
      this.filterDatabaseHelper.currentPage = event;
      this.filteredAttachment();
    }
  }

  
  selectedImage: string = '';
  selectedExpirableUrl : SafeResourceUrl = "";
  urlSanitized:boolean=false;
  @ViewChild('openViewModal') openViewModal!:ElementRef;
  sanitizeUrl(url:any){
    this.selectedImage = url;
    url = url + "&embedded=true";
    this.selectedExpirableUrl = this.transform(url);
    this.urlSanitized = true;
    this.openViewModal.nativeElement.click();
  }

  transform(url: any) {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  closeActionImage(){
    this.selectedImage = '';
    this.selectedExpirableUrl = '';
    this.urlSanitized = false;
  }
  // -------------------- Filters-----------------

  nameFilterToggle:boolean = false;
  specialtyFilterToggle:boolean = false;
  stateFilterToggle:boolean = false;
  statusFilterToggle:boolean = false;
  credentialsFilterToggle:boolean = false;

  filterByName() {
    this.nameFilterToggle = !this.nameFilterToggle;
    this.getAllProviderName();
  }
  getAllProviderName() {
    this.providerNameList = [];
    this.providerSerivce.getAllProviderName().subscribe(response => {
      response.forEach((element: any) => {
        var temp: { id: string, itemName: string } = { id: element.uuid, itemName: element.firstName + " " + element.lastName }
        this.providerNameList.push(temp);
      });
      this.providerNameList = JSON.parse(JSON.stringify(this.providerNameList));
    })
  }
  filterBySpecialty() {
    this.specialtyFilterToggle = !this.specialtyFilterToggle;
    this.getExpirableSpecialty();
    this.credentialstype = '';
  }

  getExpirableSpecialty() {
    this.specialtyList = [];
    this.providerSerivce.getExpirableSpecialty().subscribe(response => {
      response.forEach((element: any) => {
        var temp: { id: string, itemName: string } = { id: element.description, itemName: element.description }
        this.specialtyList.push(temp);
      })
      this.specialtyList = JSON.parse(JSON.stringify(this.specialtyList));
    })
  }
  specialty: any;
  selectSpecialty(event: any) {
    if (this.specialtyFilterToggle) {
      this.specialty = '';
      this.credentialstype = '';
      if (event[0] != undefined) {
        this.specialty = event[0].itemName;
        this.getExpirableByElement();
      } else {
        this.specialty = '';
        this.getExpirableByElement();
      }
      this.specialtyFilterToggle = false;
    }
  }

  filterByState() {
    this.stateFilterToggle = !this.stateFilterToggle;
    this.getExpirableState();
    this.credentialstype = '';
  }

  getExpirableState() {
    this.stateNameList = [];
    this.providerSerivce.getExpirableState().subscribe(response => {
      response.forEach((element: any) => {
        if (element.stateCode != null || element.stateName != null) {
          var temp: { id: string, itemName: string } = { id: element.stateCode, itemName: element.stateName }
          this.stateNameList.push(temp);
        }
      })
      this.stateNameList = JSON.parse(JSON.stringify(this.stateNameList));
    })
  }

  filterByStatus() {
    this.statusFilterToggle = !this.statusFilterToggle;
    this.getExpirableState();
    this.credentialstype = '';
  }

  selectStatus(event: any) {
    if (this.statusFilterToggle) {
      this.credentialstype = '';
      if (event[0] != undefined) {
        this.credentialstype = event[0].id;
        this.getExpirableByElement();
      } else {
        this.credentialstype = '';
        this.getExpirableByElement();
      }

      this.statusFilterToggle = false;
    }
  }

  filterByCredentials(){
    this.credentialsFilterToggle = !this.credentialsFilterToggle
    this.getExpirableCredentials();
  }

  getExpirableCredentials(){
    this.credentialsList = [];
    this.providerSerivce.getDocType().subscribe(response=>{
      response.object.forEach((element:any)=>{
          var temp: { id: string, itemName: string } = { id: element.id, itemName: element.name }
          this.credentialsList.push(temp);
      })
      this.credentialsList = JSON.parse(JSON.stringify(this.credentialsList));
    })
  }
  // ------------------- Filters End-----------------------------


  credentialstype : string = "";
  cartToggle: boolean = false;
  toggleCartFunc(type:string) {
    this.credentialstype = type;
    this.cartToggle = !this.cartToggle;
    this.filteredAttachment();
  }

  togglecloseCartFunc() {
    this.cartToggle = false;
  }
}
