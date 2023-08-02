import { Component, ElementRef, ViewChild } from '@angular/core';
import { Constant } from 'src/app/constants/Constant';
import { ClientBusinessAddRequest } from 'src/app/models/ClientBusinessAddRequest';
import { ClientBusinessInfoRequest } from 'src/app/models/ClientBusinessInfoRequest';
import { ClientBusinessLocationRequest } from 'src/app/models/ClientBusinessLocationRequest';
import { ClientContactRequest } from 'src/app/models/ClientContactRequest';
import { EntityTypeJsonData } from 'src/app/models/common-json/EntityType';
import { FacilityTypeJsonData } from 'src/app/models/common-json/FacilityType';
import { PracticeTypeJsonData } from 'src/app/models/common-json/PracticeType';
import { Taxonomy } from 'src/app/models/common-json/Taxonomy';
import { DatabaseHelper } from 'src/app/models/DatabaseHelper';
import { ProviderSpeciality } from 'src/app/models/ProviderSpeciality';
import { ResponseModel } from 'src/app/models/Response-Model';
import { BusinessInfoService } from 'src/app/services/business-info.service';
import { DataService } from 'src/app/services/data.service';
import { ProviderService } from 'src/app/services/provider.service';
import { AngularFireStorage } from '@angular/fire/compat/storage';
import * as _ from 'lodash';
import { ImageUpload } from 'src/app/models/common-json/ImageUpload';
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs/operators';
import { EnterpriseUserService } from 'src/app/services/enterprise-user.service';
import { RoleService } from 'src/app/services/role.service';
import { Module } from 'src/app/models/Module';
import { RoleModulePrivilege } from 'src/app/models/RoleModulePrivilege';
import { ClientModuleRequest } from 'src/app/models/ClientModuleRequest';
import { Client } from 'src/app/models/Client';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {

  dropdownSettingsEntityType!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean };

  dropdownSettingsMultiselectInfo!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean ; position: string };
  dropdownSettingsPracticeType!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean };
  dropdownSettingsFacilityType!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean };

  facilityTypeObj: FacilityTypeJsonData = new FacilityTypeJsonData();
  selectedFacility: any[] = new Array();
  facilityList: any[] = new Array();

  loadingToggle: boolean = false;
  client: Client = new Client();

  progressStep:number=0;
  dropdownSettingsOwner: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean; };

  clientSearch = new Subject<string>();

  constructor(
    private businessInfoService: BusinessInfoService,
    private enterpriseUserService:EnterpriseUserService,
    private dataService: DataService,
    private providerService: ProviderService,
    private storage: AngularFireStorage,
    private _roleService: RoleService,
  ) {
    
    this.clientSearch.pipe(
      debounceTime(600),
      distinctUntilChanged())
      .subscribe(value => {
        this.clientDatabaseHelper.currentPage = 1;
        this.getAllClients();
      });

   }

  ngOnInit(): void {
    this.dropdownSettingsOwner = {
      singleSelection: true,
      text: 'Select Owner',
      enableSearchFilter: true,
      autoPosition: false
    };
    this.dropdownSettingsFacilityType = {
      singleSelection: true,
      text: 'Select Facility Type',
      enableSearchFilter: true,
      autoPosition: false
    };

    this.dropdownSettingsPracticeType = {
      singleSelection: true,
      text: 'Select Practice Type',
      enableSearchFilter: true,
      autoPosition: false
    };
    this.dropdownSettingsMultiselectInfo = {
      singleSelection: true,
      text: 'Select Taxonomy',
      enableSearchFilter: true,
      autoPosition: false,
      position: 'bottom'
    };

    this.dropdownSettingsEntityType = {
      singleSelection: true,
      text: 'Select Entity Type',
      enableSearchFilter: true,
      autoPosition: false
    };

    this.getFacilityType();
    this.getPracticeType();
    this.getEntityType();
    this.getAllClients();
    this.getOwnerList();
  }

  selectFacilityType(event: any) {
    this.locationReq.facilityType = '';
    if (event[0] != undefined) {
      this.locationReq.practiceType = event[0].itemName;
    }
  }

  facilityListing: any;
  getFacilityType() {
    this.facilityTypeObj.facilityType.forEach(element => {
      var temp: { id: any, itemName: any } = { id: '', itemName: '' };
      temp.id = element.id
      temp.itemName = element.type
      this.facilityList.push(temp);
    });
    this.facilityListing = JSON.parse(JSON.stringify(this.facilityList));
  }

  selectPracticeType(event: any) {
    this.clientBusinessInfoRequest.practiceType = '';
    if (event[0] != undefined) {
      this.clientBusinessInfoRequest.practiceType = event[0].itemName;
    }
  }

  practiceTypeObj: PracticeTypeJsonData = new PracticeTypeJsonData();
  selectedPracticeType: any[] = new Array();
  practiceList: any[] = new Array();
  practiceListing: any;
  getPracticeType() {
    this.practiceTypeObj.practiceType.forEach(element => {
      var temp: { id: any, itemName: any } = { id: '', itemName: '' };
      temp.id = element.id
      temp.itemName = element.type
      this.practiceList.push(temp);
    });
    this.practiceListing = JSON.parse(JSON.stringify(this.practiceList));
  }

  taxonomyList: Taxonomy[] = new Array();
  databaseHelper: DatabaseHelper = new DatabaseHelper();
  specialitiesList: ProviderSpeciality[] = new Array();
  specialities: any[] = new Array();
  readonly Constant = Constant;

  getTaxonomy(search: string) {
    this.businessInfoService.getClientTaxonomy(search).subscribe(response => {
      this.taxonomyList = [];
      this.taxonomyList = response.object;
    }, error => {
      this.dataService.showToast('Network Error..!!');
    })
  }

  onSearch(event: any) {
    debugger
    this.getTaxonomy(event.target.value);
  }

  state: any = '';
  async selectState(event: any, index: number) {
    this.state = event;
    // if (!Constant.EMPTY_STRINGS.includes(this.state)) {
    //   this.getTaxonomy(this.state, index);
    // } else {
    //   this.taxonomyList = [];
    // }
  }

  selectTaxonomy(event: any) {
    if (event[0] != undefined) {
      this.locationReq.priTaxonomy = event[0].itemName;
    } else {
      this.locationReq.priTaxonomy = '';
    }
  }

  secSpecialities: any[] = new Array();
  selectSecTaxonomy(event: any) {
    if (event[0] != undefined) {
      this.locationReq.secTaxonomy = event[0].itemName;
    } else {
      this.locationReq.secTaxonomy = '';
    }
  }

  addSpecialities: any[] = new Array();
  selectAddTaxonomy(event: any) {
    if (event[0] != undefined) {
      this.locationReq.addTaxonomy = event[0].itemName;
    } else {
      this.locationReq.addTaxonomy = '';
    }
  }

  @ViewChild('createModelButton') createModelButton!:ElementRef;
  businessInfoToggle: boolean = true;
  contactInfoToggle: boolean = false;
  locationInfoToggle: boolean = false;
  moduleToggle: boolean = false;

  openCreateClientModel(){
    this.goToBusinessForm();
    this.createModelButton.nativeElement.click();
  }


  goToBusinessForm() {
    this.progressStep=1;
    this.businessInfoToggle = true;
    this.contactInfoToggle = false;
    this.locationInfoToggle = false;
    this.moduleToggle = false;
  }

  @ViewChild('businessInfoForm') businessInfoForm!:any;
  contactFormToggle:boolean = false;
  goToContactForm() {
    // this.contactFormToggle = false;
    // if(this.businessInfoForm.invalid){
    //   this.contactFormToggle = true;
    //   return;
    // }
    this.businessInfoToggle = false;
    this.contactInfoToggle = true;
    this.locationInfoToggle = false;
    this.moduleToggle = false;
    this.progressStep=2;
  }

  @ViewChild('locationInfoForm') locationInfoForm!:any;
  locationFormToggle:boolean = false;
  goToLocationForm() {
    // this.locationFormToggle = false;
    // if(this.locationInfoForm.invalid){
    //   this.locationFormToggle = true;
    //   return;
    // }
    this.businessInfoToggle = false;
    this.contactInfoToggle = false;
    this.locationInfoToggle = true;
    this.moduleToggle = false;
    this.progressStep=3;
    this.locationReq.address1=this.clientBusinessInfoRequest.addressLine1
    this.locationReq.address2=this.clientBusinessInfoRequest.addressLine2
    this.locationReq.city=this.clientBusinessInfoRequest.city
    this.locationReq.stateName=this.clientBusinessInfoRequest.stateName
    this.getTaxonomy("");
    this.locationReq.stateCode=this.clientBusinessInfoRequest.stateCode
    this.locationReq.country=this.clientBusinessInfoRequest.country
    this.locationReq.zipcode=this.clientBusinessInfoRequest.zipcode
    this.locationReq.legalBusinessName=this.clientBusinessInfoRequest.legalBusinessName
    this.locationReq.phone = this.clientBusinessInfoRequest.phone
    this.locationReq.fax = this.clientBusinessInfoRequest.fax
    this.locationReq.email = this.clientBusinessInfoRequest.email
    this.locationReq.ein = this.clientBusinessInfoRequest.ein
    if(this.dbaFieldToggle){
      this.locationDbaToggle = true;
      this.locationReq.dba = this.clientBusinessInfoRequest.dba;
    }
    
  }

  goToModuleAccessForm() {
    this.businessInfoToggle = false;
    this.contactInfoToggle = false;
    this.locationInfoToggle = false;
    this.moduleToggle = true;
    this.progressStep=4;

    // this.selectedClientName = this.clientBusinessInfoRequest.legalBusinessName;
    this.selectedClientUuid = '';
    this.getClientModule();
  }

  dbaFieldToggle: boolean = false;
  toggleDbaField() {
    this.clientBusinessInfoRequest.dba = "";
    this.dbaFieldToggle = !this.dbaFieldToggle;
  }

  toggleMailingCheckBox: boolean = false;
  toggleMailingAddFields() {
    this.toggleMailingCheckBox = !this.toggleMailingCheckBox;
    if (this.toggleMailingCheckBox == false) {
      this.addressReq = new ClientBusinessAddRequest();
    }
  }

  toggleSecContact: boolean = false;
  toggleSecContactFields() {
    this.toggleSecContact = !this.toggleSecContact;
    this.secContactReq = new ClientContactRequest();
  }

  locationDbaToggle: boolean = false;
  locationDbaToggleField() {
    this.locationReq.dba=''
    this.locationDbaToggle = !this.locationDbaToggle;
  }

  haveSecTaxonomy: boolean = false
  toggleSecTaxonomyField() {
    this.haveSecTaxonomy = !this.haveSecTaxonomy;
  }

  haveAddTaxonomy: boolean = false;
  toggleAddTaxonomyField() {
    this.haveAddTaxonomy = !this.haveAddTaxonomy;
  }

  isCliaFacility: number = 0;
  cliaToggleFields() {
    if (this.isCliaFacility == 0) {
      this.isCliaFacility = 1;
    } else {
      this.isCliaFacility = 0;
    }
  }

  isCliaWaiverFacility: number = 0;
  cliaWaiverToggleFields() {
    if (this.isCliaWaiverFacility == 0) {
      this.isCliaWaiverFacility = 1;
    } else {
      this.isCliaWaiverFacility = 0;
    }
  }

  isFacilityStateLicense: number = 0;
  facilityStateLicenseToggleFields() {
    if (this.isFacilityStateLicense == 0) {
      this.isFacilityStateLicense = 1;
    } else {
      this.isFacilityStateLicense = 0;
    }
  }

  selectEntityType(event: any) {
    debugger
    this.clientBusinessInfoRequest.entityType = '';
    if (event[0] != undefined) {
      this.clientBusinessInfoRequest.entityType = event[0].itemName;
    }
  }

  entityTypeObj: EntityTypeJsonData = new EntityTypeJsonData();
  selectedEntity: any[] = new Array();
  entityList: any[] = new Array();
  entityListing: any[] = new Array();
  getEntityType() {
    this.entityTypeObj.entityType.forEach(element => {
      var temp: { id: any, itemName: any } = { id: '', itemName: '' };
      temp.id = element.id
      temp.itemName = element.type
      this.entityList.push(temp);
    });
    this.entityListing = JSON.parse(JSON.stringify(this.entityList));
  }

  clientBusinessInfoRequest: ClientBusinessInfoRequest = new ClientBusinessInfoRequest();
  locationReq: ClientBusinessLocationRequest = new ClientBusinessLocationRequest();
  addressReq: ClientBusinessAddRequest = new ClientBusinessAddRequest();
  priContactReq: ClientContactRequest = new ClientContactRequest();
  secContactReq: ClientContactRequest = new ClientContactRequest();
  @ViewChild('clientFormCloseButton') clientFormCloseButton!: ElementRef;
  cliaUrl: string = "";
  cliaWaiverUrl: string = "";
  createClientToggle: boolean = false;

  createClient() {
    this.createClientToggle = true;
    this.clientBusinessInfoRequest.locationReq = this.locationReq;
    if (this.toggleMailingCheckBox == true) {
      this.clientBusinessInfoRequest.addressReq = this.addressReq;
    }
    this.priContactReq.isPrimary = 1;
    this.clientBusinessInfoRequest.contactReqList.push(this.priContactReq);
    if (this.toggleSecContact == true) {
      this.clientBusinessInfoRequest.contactReqList.push(this.secContactReq);
    }
    this.clientBusinessInfoRequest.cliaUrlList.push(this.cliaUrl);
    this.clientBusinessInfoRequest.cliaWaiverUrlList.push(this.cliaWaiverUrl);

    this.businessInfoService.createClient(this.clientBusinessInfoRequest).subscribe((response: ResponseModel<ClientBusinessInfoRequest>) => {
      this.createClientToggle = false;
      this.clientFormCloseButton.nativeElement.click();
      this.clearCreateModel();
      this.getAllClients();
      // this.dataService.showToast("Client Created Successfully","success");
    }, (error: any) => {
      this.createClientToggle = false;
      this.dataService.showToast('Network Error..!!');
    });
  }

  clearCreateModel(){
    this.selectedPracticeType=[];
    this.selectedUser = [];
    this.clientBusinessInfoRequest = new ClientBusinessInfoRequest();
    this.priContactReq = new ClientContactRequest();
    this.secContactReq = new ClientContactRequest();
    this.locationReq = new ClientBusinessLocationRequest();
    this.cliaUrl='';
    this.cliaWaiverUrl='';
    this.modules = [];
  }

  loadClientListToggle: boolean = false;
  allClientList: any[] = new Array();
  totalItems: number = 0;
  clientDatabaseHelper:DatabaseHelper = new DatabaseHelper();
  getAllClients() {
    this.loadClientListToggle = true;
    this.businessInfoService.getAllClients(this.clientDatabaseHelper).subscribe((response: any) => {
      this.allClientList=[];
      response.object.forEach((element:any) => {
        element.selectedOwner=[];
        if(element.clientOwner!=null){
          element.selectedOwner=[element.clientOwner];
        }
        this.allClientList.push(element);
      });
      this.totalItems = response.totalItems;
      this.loadClientListToggle = false;
    }, (error: any) => {
      this.loadClientListToggle = false;
      this.dataService.showToast('Network Error..!!');
    })
  }

  resetSearchClient(){
    this.clientDatabaseHelper = new DatabaseHelper();
    this.clientDatabaseHelper.search = '';
    this.getAllClients();
  }

  pageChanged(event:any){
    if(event!=this.clientDatabaseHelper.currentPage){
      this.clientDatabaseHelper.currentPage = event;
      this.getAllClients();
    }
   
  }

  @ViewChild('deleteModelButton') deleteModelButton!: ElementRef;
  clientId: number = 0;
  openClientDeleteModal(obj: any) {
    this.clientId = obj.id;
    this.deleteModelButton.nativeElement.click();
  }

  deleteClientToggle: boolean = false;
  deleteClient() {
    this.deleteClientToggle = true;
    this.businessInfoService.deleteClient(this.clientId).subscribe((response: any) => {
      this.deleteClientToggle = false;
      this.getAllClients();
    }, (error: any) => {
      this.deleteClientToggle = false;
      this.dataService.showToast('Network Error..!!');
    })
  }

  public handleBusinessAddressChange(e: any) {
    debugger
    this.clientBusinessInfoRequest.addressLine1 = e.name;
    this.clientBusinessInfoRequest.addressLine2 = "";
    this.clientBusinessInfoRequest.city = "";
    this.clientBusinessInfoRequest.stateName = "";
    this.clientBusinessInfoRequest.stateCode = "";
    this.clientBusinessInfoRequest.country = "";
    this.clientBusinessInfoRequest.zipcode = "";

    e?.address_components?.forEach((entry: any) => {

      if (entry.types?.[0] === "route") {
        this.clientBusinessInfoRequest.addressLine2 = entry.long_name + ","
      }
      if (entry.types?.[0] === "sublocality_level_1") {
        this.clientBusinessInfoRequest.addressLine2 = this.clientBusinessInfoRequest.addressLine2 + entry.long_name
      }
      if (entry.types?.[0] === "locality") {
        this.clientBusinessInfoRequest.city = entry.long_name
      }
      if (entry.types?.[0] === "administrative_area_level_1") {
        this.clientBusinessInfoRequest.stateName = entry.long_name
        this.clientBusinessInfoRequest.stateCode = entry.short_name
      }
      if (entry.types?.[0] === "country") {
        this.clientBusinessInfoRequest.country = entry.long_name
      }
      if (entry.types?.[0] === "postal_code") {
        this.clientBusinessInfoRequest.zipcode = entry.long_name
      }
    });
  }

  public handleDiffAddressChange(e: any) {
    debugger
    this.addressReq.addressLine1 = e.name;
    this.addressReq.addressLine2 = "";
    this.addressReq.city = "";
    this.addressReq.stateName = "";
    this.addressReq.stateCode = "";
    this.addressReq.country = "";
    this.addressReq.zipcode = "";

    e?.address_components?.forEach((entry: any) => {

      if (entry.types?.[0] === "route") {
        this.addressReq.addressLine2 = entry.long_name + ","
      }
      if (entry.types?.[0] === "sublocality_level_1") {
        this.addressReq.addressLine2 = this.addressReq.addressLine2 + entry.long_name
      }
      if (entry.types?.[0] === "locality") {
        this.addressReq.city = entry.long_name
      }
      if (entry.types?.[0] === "administrative_area_level_1") {
        this.addressReq.stateName = entry.long_name
        this.addressReq.stateCode = entry.short_name
      }
      if (entry.types?.[0] === "country") {
        this.addressReq.country = entry.long_name
      }
      if (entry.types?.[0] === "postal_code") {
        this.addressReq.zipcode = entry.long_name
      }
    });
  }

  public handleLocBusiAddressChange(e: any) {
    debugger
    this.locationReq.address1 = e.name;
    this.locationReq.address2 = "";
    this.locationReq.city = "";
    this.locationReq.stateName = "";
    this.locationReq.stateCode = "";
    this.locationReq.country = "";
    this.locationReq.zipcode = "";

    e?.address_components?.forEach((entry: any) => {

      if (entry.types?.[0] === "route") {
        this.locationReq.address2 = entry.long_name + ","
      }
      if (entry.types?.[0] === "sublocality_level_1") {
        this.locationReq.address2 = this.locationReq.address2 + entry.long_name
      }
      if (entry.types?.[0] === "locality") {
        this.locationReq.city = entry.long_name
      }
      if (entry.types?.[0] === "administrative_area_level_1") {
        this.locationReq.stateName = entry.long_name
        this.locationReq.stateCode = entry.short_name
      }
      if (entry.types?.[0] === "country") {
        this.locationReq.country = entry.long_name
      }
      if (entry.types?.[0] === "postal_code") {
        this.locationReq.zipcode = entry.long_name
      }
    });
  }

  // Attachment upload Firebase Function ------->

  currentCount: any;
  currentUpload: any;
  map = new Map();
  private basePath: string = "credily-v3/";
  uploadPercent: any;
  urls: any[] = new Array();
  files: any[] = new Array();
  uploadedCliaToggle: boolean = false;
  uploadedCliaWaiverToggle: boolean = false;
  uploadingCliaToggle: boolean = false;
  uploadingCliaWaiverToggle: boolean = false;
  selectedFiles: any[] = new Array();
  tempProgress!: number;
  progress!: number;
  fileName: string = "";

  onSelectFile(event: any, fileName: string) {
    debugger
    this.fileName = fileName;
    if (this.fileName == 'clia') {
      this.uploadingCliaToggle = true;
    } else if (this.fileName == 'cliaWaiver') {
      this.uploadingCliaWaiverToggle = true;
    }
    this.urls = new Array();
    this.files = new Array();
    this.selectedFiles = event.target.files;
    if (event.target.files && event.target.files[0]) {
      var filesAmount = event.target.files.length;
      for (let i = 0; i < filesAmount; i++) {
        const element = event.target.files[i];
        this.files.push(element.name);
        var reader = new FileReader();
        reader.onload = (event2: any) => {
          this.urls.push(event2.target.result);
          if (this.urls.length == event.target.files.length) {
            this.uploadMulti();
          }
        }
        reader.readAsDataURL(event.target.files[i]);
      }
    }
  }

  uploadMulti() {
    debugger
    let files = this.selectedFiles;
    let filesIndex = _.range(files.length);
    this.pushUpload(filesIndex, files);
    this.urls = new Array();
  }

  pushUpload(filesIndex: any, files: any) {
    debugger
    _.each(filesIndex, async (idx: any) => {
      this.currentCount++;
      this.currentUpload = new ImageUpload(files[idx]);
      let x = this.currentUpload.file.name.split(" ");
      var name: string = "";
      x.forEach((element: any) => {
        name = name + element;
      });
      let imageName = this.basePath + this.locationReq.npi + "/" + this.fileName + "/" + "I" + "/" + name + new Date().toString();
      const fileRef = this.storage.ref(imageName);
      this.uploadPercent = this.storage.upload(imageName, this.currentUpload.file).percentageChanges();
      this.storage.upload(imageName, this.currentUpload.file).snapshotChanges().pipe(
        finalize(async () => {
          fileRef.getDownloadURL().subscribe((url: any) => {
            var urlString = url;
            if (this.fileName == 'clia') {
              this.clientBusinessInfoRequest.cliaUrlList.push(urlString);
              this.uploadingCliaToggle = false;
              this.uploadedCliaToggle = true;
            } else if (this.fileName == 'cliaWaiver') {
              this.clientBusinessInfoRequest.cliaWaiverUrlList.push(urlString);
              this.uploadingCliaWaiverToggle = false;
              this.uploadedCliaWaiverToggle = true;
            }
          })
        })
      ).subscribe((res: any) => {
        if (((res.bytesTransferred / res.totalBytes) * 100) == 100) {
          this.tempProgress = (res.bytesTransferred / res.totalBytes) * 100;;
          this.progress = this.tempProgress;
        }
        if (((res.bytesTransferred / res.totalBytes) * 100) < 100 && ((res.bytesTransferred / res.totalBytes) * 100) > this.progress) {
          this.progress = (res.bytesTransferred / res.totalBytes) * 100;
        }
        if (this.tempProgress == this.currentCount * 100) {
          this.progress = this.tempProgress;
        }
      })
    });
  }

  //--------------------------------------------<
  enterpriseDatabaseHelper:DatabaseHelper=new DatabaseHelper();
  ownerList:any[]=[];
  getOwnerList(){
    
    this.enterpriseUserService.getAllEnterpriseUsers(this.enterpriseDatabaseHelper).subscribe((response: any) => {
      if(response.status){
        
        response.object.forEach((element:any) => {
          var temp: { id: any, itemName: any } = { id: '', itemName: '' };
          temp.id = element.uuid;
          temp.itemName = element.fullName;
          this.ownerList.push(temp);
        });
      }
 
    }, (error: any) => {
 
      this.dataService.showToast(error.error.message);
    })
  }
  selectOwner(i:number,event:any){
    debugger
    this.allClientList[i].selectedOwner=event;
    this.businessInfoService.updateOwner(this.allClientList[i].uuid,event[0].id).subscribe((response: any) => {
      this.dataService.showToast(response.message);
 
    }, (error: any) => {
 
      this.dataService.showToast(error.error.message);
    })
  }
  searchOwner(i:number,event: any) {
    debugger
    this.enterpriseDatabaseHelper.search = event.target.value;
    this.getOwnerList();
  }


  /************************************ client permission *********************************/


  fetchingRole: boolean = false;

  modules: Module[] = new Array();

  // roleModulePrivileges: RoleModulePrivilege[] = new Array();

  selectedClientName:string='';
  editClientName:string='';
  selectedClientUuid:string='';

  @ViewChild('openRoleModelButton') openRoleModelButton !: ElementRef;
  @ViewChild('closeRoleCreateModel') closeRoleCreateModel !: ElementRef;

  openCreateModuleModel() {
    this.selectedClientName = this.clientBusinessInfoRequest.legalBusinessName;
    this.selectedClientUuid = '';
    this.modules = [];
    this.clientFormCloseButton.nativeElement.click();
    this.openRoleModelButton.nativeElement.click();
    this.getClientModule();
  }

  openEditModuleModel(obj:any) {
    debugger
    this.selectedClientName = obj.legalBusinessName;
    this.editClientName = obj.legalBusinessName;
    this.selectedClientUuid = obj.uuid;
    this.modules = [];
    this.openRoleModelButton.nativeElement.click();
    this.selectedUser = obj.selectedOwner;
    this.getClientModule(obj.uuid)
  }

  fetchingModule: boolean = false;
  getClientModule(uuid?: number) {
    return new Promise((resolve, reject) => {
      this.fetchingModule = true;
      this._roleService.getClientModule(uuid).subscribe(response => {

        if (response.object != null) {
          this.modules = response.object;
        }

        this.fetchingModule = false;
        resolve(true);
      }, (error) => {
        resolve(false);
        this.fetchingModule = false;
        this.dataService.showToast('Network error..!!');
      })

    });

  }

  

  setsubModulePrivilege(index: number, pvId: number) {
    this.modules[index].subModules.forEach(ele => {
      ele.privilegeId = pvId;
      if (pvId == 2) {
        ele.isAccessible = 1;
      } else {
        ele.isAccessible = pvId;
      }

    })
  }

  setModulePrivilege(index: number, privilegeId: number) {

    debugger

    if(this.modules[index].isAccessible == 1 && this.modules[index].privilegeId == privilegeId){
      this.modules[index].isAccessible = 0;
      this.modules[index].privilegeId = 0;
      this.setsubModulePrivilege(index, 0);
    }else{
      this.modules[index].isAccessible = 1;
      this.modules[index].privilegeId = privilegeId;
      this.setsubModulePrivilege(index, privilegeId);
    }

  }

  setSubModulePrivilege(moduleIndex: number, index: number, privilegeId: number, type: string) {

    if(this.modules[moduleIndex].subModules[index].isAccessible==1 && type == 'access'){
      this.modules[moduleIndex].subModules[index].isAccessible = 0;
      this.modules[moduleIndex].subModules[index].privilegeId = 0;
    }else{
      if(this.modules[moduleIndex].subModules[index].privilegeId == privilegeId){
        this.modules[moduleIndex].subModules[index].isAccessible = 0;
        this.modules[moduleIndex].subModules[index].privilegeId = 0;
      }else{
        this.modules[moduleIndex].subModules[index].isAccessible = 1;
        this.modules[moduleIndex].isAccessible = 1;
        this.modules[moduleIndex].subModules[index].privilegeId = privilegeId;
      }
      
    }

    var count = 0;
    var accessibleCount = 0;
    this.modules[moduleIndex].subModules.forEach(element => {
      
      if (element.privilegeId == privilegeId) {
        count++;
      }
      if(element.isAccessible == 1){
        accessibleCount++;
      }
    });
    if (count == this.modules[moduleIndex].subModules.length) {
      this.modules[moduleIndex].privilegeId = privilegeId;
    }else{
      this.modules[moduleIndex].privilegeId = 0;
    }
    if (accessibleCount == 0) {
      this.modules[moduleIndex].isAccessible = 0;
    }

  }

  saveModuleDetails() {
    debugger
    this.clientBusinessInfoRequest.moduleRequestList = [];

    this.modules.forEach(element => {
      if (element.isAccessible > 0) {
        var roleModule = new RoleModulePrivilege();
        roleModule.moduleId = element.id;
        roleModule.privilegeId = element.privilegeId;
        this.clientBusinessInfoRequest.moduleRequestList.push(roleModule);

        element.subModules.forEach(ele => {
          if (ele.privilegeId > 0) {
            var roleModule = new RoleModulePrivilege();
            roleModule.moduleId = element.id;
            roleModule.subModuleId = ele.id;
            roleModule.privilegeId = ele.privilegeId;
            this.clientBusinessInfoRequest.moduleRequestList.push(roleModule);
          }
        })

      }
    })

    if (Constant.EMPTY_STRINGS.includes(this.selectedClientUuid)) {
      this.createClient();
    } else {
      var req = new ClientModuleRequest();
      req.clientBusinessName = this.editClientName;
      req.clientUuid = this.selectedClientUuid;
      req.moduleRequestList = this.clientBusinessInfoRequest.moduleRequestList;
      req.userUuid = this.selectedUserUUid;
      this.updateClientModule(req);
    }
  }


  updateClientModule(req:ClientModuleRequest){
    debugger
    this.createClientToggle = true;
    this._roleService.updateClientModule(req).subscribe(response=>{
      if(response.status){
        
        this.dataService.showToast('Updated Successfully..!!');
        
        // this.selectedUser = [];
        // this.clearCreateModel(); 
      }
      this.getAllClients();
      this.closeRoleCreateModel.nativeElement.click();
      this.createClientToggle = false;
    }, error=>{
      this.createClientToggle = false;
    })
  }
  selectedUser:any[]=[];
  selectUser(event:any){
    debugger
    this.clientBusinessInfoRequest.userAccountUuid = '';
    if (event[0] != undefined) {
      this.clientBusinessInfoRequest.userAccountUuid = event[0].id;
    }
  }

  selectedUserUUid:any;
  updateSelectUser(event:any){
    debugger
    this.selectedUserUUid = '';
    if (event[0] != undefined) {
      this.selectedUserUUid = event[0].id;
    }
  }


  searchUser(event: any) {
    debugger
    this.enterpriseDatabaseHelper.search = event.target.value;
    this.getOwnerList();
  }

  @ViewChild('clientDetailmodalButton')clientDetailmodalButton!: ElementRef;
  viewClientDetail(obj: any) {
    debugger
    this.businessInfoService.getClientByUuid(obj.uuid).subscribe(response=>{
      if (response.status) {
        this.client = response.object;
        if (this.client.clientOwner != null) {
          this.client.clientOwner = this.client.clientOwner.itemName;
        }
        this.clientDetailmodalButton.nativeElement.click();
      } else {
        this.dataService.showToast(response.message);
      }
    },error=>{
      this.dataService.showToast('Network Error !');
    })
  }

  /************************************ client permission *********************************/

}
