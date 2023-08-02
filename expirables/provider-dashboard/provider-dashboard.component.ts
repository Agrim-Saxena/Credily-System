import { Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { Constant } from 'src/app/constants/Constant';
import * as moment from 'moment';
import { ImageUpload } from 'src/app/models/common-json/ImageUpload';
import { DatabaseHelper } from 'src/app/models/DatabaseHelper';
import { Expirable } from 'src/app/models/Expirable';
import { Provider } from 'src/app/models/Provider';
import { DataService } from 'src/app/services/data.service';
import { ProviderService } from 'src/app/services/provider.service';
import { AngularFireStorage } from '@angular/fire/compat/storage';
import * as _ from 'lodash';
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs/operators';
import { Attachment } from 'src/app/models/common-json/Attachment';
import { AttachmentService } from 'src/app/services/attachment.service';
import { Route } from 'src/app/constants/Route';
import { EntityTypeJsonData } from 'src/app/models/common-json/EntityType';
import { Monitoring } from 'src/app/models/Provider-Details/Monitoring';
import { MonitoringService } from 'src/app/services/monitoring.service';
import { saveAs } from 'file-saver';
import { Key } from 'src/app/constants/Key';
import { AngularFireDatabase } from '@angular/fire/compat/database';
import { Subject } from 'rxjs';
import { DocumnetShareRequest } from 'src/app/models/documentShareRequest';
import { DocumentShareService } from 'src/app/services/document-share.service';
import { CountryCode } from 'src/app/models/common-json/CountryCode';

@Component({
  selector: 'app-provider-dashboard',
  templateUrl: './provider-dashboard.component.html',
  styleUrls: ['./provider-dashboard.component.css']
})
export class ProviderDashboardComponent {


  readonly Constant = Constant;
  readonly Route = Route;
  docType: any[] = new Array();
  cartToggle: boolean = false;
  uuid: any;
  progressPer: number = 0;
  entityTypeObj: EntityTypeJsonData = new EntityTypeJsonData();
  dropdownSettingsCountryCode!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean };

  dropdownSettingsfrequencyType: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean } = { singleSelection: true, text: 'Select Frequency', enableSearchFilter: false, autoPosition: false };

  frequencyType = [
    {
      "id": 1,
      "type": "Weekly"
    },
    {
      "id": 2,
      "type": "Monthly"
    },
    {
      "id": 3,
      "type": "Yearly"
    }

  ]

  // getDocType() {
  //   this.entityListing = [];

  //   this.docType.forEach((element: any) => {
  //     var temp: { id: 0, itemName: "" } = { id: element.id, itemName: element.type }
  //     this.entityListing.push(temp);
  //   })
  // }

  getFrequencyType() {
    this.frequencyListing = [];
    this.frequencyType.forEach((element: any) => {
      var temp1: { id: 0, itemName: "" } = { id: element.id, itemName: element.type }
      this.frequencyListing.push(temp1);
    })
  }

  selectedFrequency: any[] = new Array();
  frequencyList: any[] = new Array();
  frequencyListing: any[] = this.frequencyType;
  toggleSsn:boolean = false;
  selectedDoc: any[] = new Array();
  entityList: any[] = new Array();


  providerDatabaseHelper: DatabaseHelper = new DatabaseHelper();
  provider: Provider = new Provider();
  expirable: Expirable = new Expirable();
  monitoring: Monitoring = new Monitoring();
  dropdownSettingsfileType!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean };

  dropdownSettingsDatabase!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean };
  selectedDatabase: any[] = new Array();
  databaseList: any[] = new Array();

  dropdownSettingsCredentials!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean };
  credentialList: any[] = new Array();
  selectedCredential: any[] = new Array();
  selectedImage: string = '';
  role: any;

  expirableSearch = new Subject<string>();
  documentSearch = new Subject<string>();
  monitoringSearch = new Subject<string>();

  dropdownSettingsType!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean };
  selectedType: any[] = new Array();
  typeList: any[] = new Array();

  constructor(public dataService: DataService,
    public router: Router,
    private _routeParams: ActivatedRoute,
    private providerSerivce: ProviderService,
    private attachmentService: AttachmentService,
    private firebaseStorage: AngularFireStorage,
    private firebaseDatabase: AngularFireDatabase,
    private monitoringService: MonitoringService,
    private documentShareService: DocumentShareService
  ) {

    this.role = dataService.getRole();

    if (this._routeParams.snapshot.queryParamMap.has('uuid')) {
      this.uuid = this._routeParams.snapshot.queryParamMap.get('uuid');
    }
    else {
      this.uuid = this.dataService.getUserUUID();
    }

    this.fireBase();

    this.expirableSearch.pipe(
      debounceTime(600),
      distinctUntilChanged())
      .subscribe(value => {
        this.expirableDatabaseHelper.currentPage = 1;
        this.expirableList = [];
        this.getExpirable();
      });

    this.documentSearch.pipe(
      debounceTime(600),
      distinctUntilChanged())
      .subscribe(value => {
        this.databaseHelperAttachment.currentPage = 1;
        this.attachmentList = [];
        this.getProviderAttachment();
      });

    this.monitoringSearch.pipe(
      debounceTime(600),
      distinctUntilChanged())
      .subscribe(value => {
        this.monitorDatbaseHelper.currentPage = 1;
        this.monitoringList = [];
        this.getOngoingMonitoring();
      });

  }

  ngOnInit() {

    this.dropdownSettingsCountryCode = {
      singleSelection: true,
      text: 'Code',
      enableSearchFilter: true,
      autoPosition: false
    };

    this.dropdownSettingsfileType = {
      singleSelection: true,
      text: 'Select File Type',
      enableSearchFilter: true,
      autoPosition: false
    };

    this.dropdownSettingsCredentials = {
      singleSelection: true,
      text: 'Select Credential',
      enableSearchFilter: true,
      autoPosition: false
    };

    this.dropdownSettingsType = {
      singleSelection: true,
      text: 'Select Type',
      enableSearchFilter: true,
      autoPosition: false
    };

    this.dropdownSettingsDatabase = {
      singleSelection: true,
      text: 'Select Database',
      enableSearchFilter: true,
      autoPosition: false
    };

    this.dropdownSettingsdocumentfileType = {
      singleSelection: true,
      text: 'Select File Type',
      enableSearchFilter: true,
      autoPosition: false
    };

    var temp1: { id: any, itemName: any } = { id: '', itemName: '' };
    temp1.id = 'Mail'
    temp1.itemName = 'Mail'
    this.typeList.push(temp1);

    var temp2: { id: any, itemName: any, stateCode: any } = { id: '', itemName: '', stateCode: '' };
    temp2.id = 'Phone'
    temp2.itemName = 'Phone'
    this.typeList.push(temp2);

    // this.getProviderAttachment();
    this.getDocType();
    this.getProviders();
    this.getFrequencyType();
    // this.getExpirable();
    // this.getOngoingMonitoring();
    this.getDocumentFileType();
  }

  viewProfile() {
    debugger
    let navigationExtras: NavigationExtras = {
      queryParams: { "uuid": this.uuid },
    };
    this.router.navigate([this.Route.PROVIDER_PROFILE], navigationExtras);
  }

  viewNotification() {

    let navigationExtras: NavigationExtras = {
      queryParams: { "uuid": this.uuid },
    };
    this.router.navigate([this.Route.EXPIRABLE_PROVIDER_NOTIFICATION_VIEW], navigationExtras);

  }

  manageNotification() {
    localStorage.setItem("providerUuid", this.uuid);
    let navigationExtras: NavigationExtras = {
      queryParams: { "uuid": this.uuid },
    };
    this.router.navigate([this.Route.EXPIRABLE_PROVIDER_NOTIFICATION_MANAGE], navigationExtras);
  }

  viewVault() {
    debugger
    let navigationExtras: NavigationExtras = {
      queryParams: { "uuid": this.uuid },
    };
    this.router.navigate([this.Route.EXPIRABLE_PROVIDER_VAULT], navigationExtras);
  }

  goToAudit(actionFrom: string) {
    debugger
    let navigationExtras: NavigationExtras = {
      queryParams: { "actionFrom": actionFrom, "uuid": this.uuid },
    };
    this.router.navigate([this.Route.EXPIRABLE_PROVIDER_AUDIT], navigationExtras);
  }

  //------------------------------Expirable Filter Start---------------------------------->>

  credentialToggle: boolean = false;
  filterCredentials() {
    this.credentialToggle = !this.credentialToggle;
    // this.expirableList = [];
    this.getExpirableDoc();
  }

  getExpirableDoc() {
    this.credentialList = [];
    this.providerSerivce.getExpirableDoc(this.uuid).subscribe(response => {
      if (response.status) {
        response.object.forEach((element: any) => {
          if (element.docType != null) {
            var temp: { id: string, itemName: string } = { id: element.docType, itemName: element.docType }
            this.credentialList.push(temp);
          }
        })
        this.credentialList = JSON.parse(JSON.stringify(this.credentialList));
      }
    })
  }

  documentType: string = '';
  selectCredential(event: any) {
    if (this.credentialToggle) {
      if (event[0] != undefined) {
        this.documentType = event[0].itemName;
        this.expirableList = [];
        this.getExpirable();
      } else {
        this.documentType = '';
        this.expirableList = [];
        this.getExpirable();
      }

      this.credentialToggle = false;
    }
  }

  //------------------------------Expirable Filter End---------------------------------<<

  //----------------------------Monitoring Filter start----------------------------------------->>

  monitoringDatabaseToggle: boolean = false;
  selectedMonitoringDB: any[] = new Array();
  monitoringDBList: any[] = new Array();
  filterMonitoringDatabase() {
    this.monitoringDatabaseToggle = !this.monitoringDatabaseToggle;
    // this.monitoringList = [];
    this.getMonitoringDB();
  }

  getMonitoringDB() {
    this.monitoringDBList = [];
    this.monitoringService.getMonitoringDB(this.uuid).subscribe(response => {
      if (response.status) {
        response.object.forEach((element: any) => {
          var temp: { id: string, itemName: string } = { id: element.monitoringDatabase, itemName: element.monitoringDatabase }
          this.monitoringDBList.push(temp);
        })
        this.monitoringDBList = JSON.parse(JSON.stringify(this.monitoringDBList));
      }
    })
  }

  monitoringDB: string = '';
  selectMonitoringDB(event: any) {
    if (this.monitoringDatabaseToggle) {
      if (event[0] != undefined) {
        this.monitoringDB = event[0].itemName;
        this.monitoringList = [];
        this.getOngoingMonitoring();
      } else {
        this.monitoringDB = '';
        this.monitoringList = [];
        this.getOngoingMonitoring();
      }

      this.monitoringDatabaseToggle = false;
    }
  }

  //-----------------------------Monitoring Filter end------------------------------------------<<

  //-----Attachment Function's ------------------->

  uploadedToggle: boolean = false;
  uploadingToggle: boolean = false;
  currentUpload: any;
  map = new Map();
  private basePath: string = "credily-v3/";
  uploadPercent: any;
  urls: any[] = new Array();
  files: any[] = new Array();
  selectedFiles: any[] = new Array();
  tempProgress : number = 0;
  progress : number = 0;
  fileName: string = "";

  uploadImageFor: string = '';

  onDrop(event: any, type: string) {
    debugger
    this.uploadImageFor = type;
    this.uploadingToggle = true;
    this.urls = new Array();
    this.files = new Array();
    this.selectedFiles = event;
    this.files = event[0];
    this.urls = event[0];
    this.uploadMulti();
  }


  onSelectFile(event: any, type: string) {
    debugger
    this.uploadImageFor = type;
    this.uploadingToggle = true;
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
    } else {
      this.uploadingToggle = false
      this.uploadedToggle = false
    }
  }

  uploadMulti() {
    debugger
    let files = this.selectedFiles;
    this.attachment.fileName = '';
    this.expirable.fileName = '';
    this.progress = 0;
    let filesIndex = _.range(files.length);
    this.pushUpload(filesIndex, files);
    this.urls = new Array();
  }
  currentCount: any;
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
      let imageName = this.basePath + this.uuid + "/" + "I" + "/" + new Date().toString() + name;
      const fileRef = this.firebaseStorage.ref(imageName);
      this.uploadPercent = this.firebaseStorage.upload(imageName, this.currentUpload.file).percentageChanges();
      this.firebaseStorage.upload(imageName, this.currentUpload.file).snapshotChanges().pipe(
        finalize(async () => {
          fileRef.getDownloadURL().subscribe((url: any) => {
            var urlString = url;
            console.log(this.uploadImageFor + "--" + this.attachment.fileName )
            if (this.uploadImageFor == Constant.DOC_TYPE_ATTACHMENT) {
              this.attachment.fileUrl = urlString;
              this.attachment.fileName = name;
            } else {
              this.expirable.imagaeUrl = urlString;
              this.expirable.fileName = name;
            }
            this.fileUrlErrorMessage = false;
            if(!Constant.EMPTY_STRINGS.includes(urlString)){
              this.extractDate(urlString, this.currentUpload.file.type);
            }

            // this.uploadingToggle = false;
            this.uploadedToggle = true;
          });
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

  extractDate(url:string, type:string){
    this.attachmentService.extractDate(url, type).subscribe((response:any) =>{
      if(response.status && response.object != null){
        if (this.uploadImageFor == Constant.DOC_TYPE_ATTACHMENT) {
          this.attachment.issueDate = moment(response.object.effectiveDate).format('YYYY-MM-DD');
          this.attachment.expirationDate = moment(response.object.expirationDate).format('YYYY-MM-DD');
        }else{
          this.expirable.effectiveDate = moment(response.object.effectiveDate).format('YYYY-MM-DD');
          this.expirable.expirationDate = moment(response.object.expirationDate).format('YYYY-MM-DD');
        }
        
      }
      this.uploadingToggle = false;
    }, error=>{
      this.uploadingToggle = false;
    })
  }

  fileType: any[] = new Array();
  selectedfileType: any[] = new Array();

  selectFileType(event: any) {
    this.attachment.fileType = '';
    if (event[0] != undefined) {
      this.attachment.fileType = event[0].itemName;
    }
  }

  providerLoaderToggle: boolean = false
  attachment: Attachment = new Attachment();
  fileCreateToggle: boolean = false;
  @ViewChild('fileFormCloseButton') fileFormCloseButton!: ElementRef;

  fileUrlErrorMessage:boolean = false;
  invalidFileForm:boolean = false;
  @ViewChild('fileForm') fileForm!: any;
  createAttachment() {
    debugger
    this.fileUrlErrorMessage = false;
    if(this.uploadingToggle){
      return;
    }
    if(Constant.EMPTY_STRINGS.includes(this.attachment.fileUrl)){
      this.fileUrlErrorMessage = true;
      return;
    }

    this.invalidFileForm = false;
    if(this.fileForm.invalid){
      this.invalidFileForm = true;
      return;
    }

    if(this.attachment.isTrackable == 1  && this.Constant.EMPTY_STRINGS.includes(this.attachment.stateName)){
      this.invalidFileForm = true;
      return;
    }

    this.providerLoaderToggle = true
    this.fileCreateToggle = true;
    this.attachment.providerUUID = this.uuid;
    var iaTrack = this.attachment.isTrackable;
    this.attachmentService.createProviderAttachment(this.attachment).subscribe((response: any) => {
      this.fileFormCloseButton.nativeElement.click();
      this.attachmentList = [];
      this.getProviderAttachment();
      if (iaTrack == 1) {
        this.expirableList = [];
        this.getExpirable();
      }
      this.fileCreateToggle = false;
      this.providerLoaderToggle = false;
      this.dataService.showToast('Added Successfully');
    }, (error: any) => {
      this.dataService.showToast('Something went wrong!');
      this.fileCreateToggle = false;
      this.providerLoaderToggle = false
    });
  }

  attachmentLoaderToggle: boolean = false
  attachmentList: Attachment[] = new Array();
  databaseHelperAttachment: DatabaseHelper = new DatabaseHelper();
  totalAttachment:  any;
  searchDocument: boolean = false;
  getProviderAttachment() {
    debugger
    this.attachmentLoaderToggle = true
    this.attachmentService.getProviderAttachment(this.uuid, this.databaseHelperAttachment, '', 0).subscribe((response) => {
      if (response.object.length > 0) {
        this.searchDocument = true;
      }
      this.attachmentList = [...this.attachmentList, ...response.object];
      // this.attachmentList = response.object;

      this.totalAttachment = response.totalItems;
      this.attachmentLoaderToggle = false
    }, error => {
      this.attachmentLoaderToggle = false
    });
  }

  //--------------------------------------------<

  selectDocType(event: any) {
    debugger
    this.expirable.docType = '';
    if (event[0] != undefined) {
      this.expirable.docType = event[0].itemName;
      this.getIssueAndExpirationDateFromDoc(this.expirable.docType, this.expirable.stateCode)
      // this.selectState(this.expirable.stateName, this.expirable.docType);
    }

  }

  selectFrequencyType(event: any) {
    this.expirable.frequency = '';
    if (event[0] != undefined) {
      this.expirable.frequency = event[0].itemName;
      if (event[0].itemName == 'Weekly') {
        //  this.expirable.effectiveDate = moment().format('YYYY-MM-DD');
        this.expirable.nextNotification = moment().add("day", 7).format('YYYY-MM-DD');
        // this.expirable.notificationDate = this.expirable.nextNotification
      }
      else if (event[0].itemName == 'Monthly') {
        //  this.expirable.effectiveDate = moment().format('YYYY-MM-DD');
        this.expirable.nextNotification = moment().add("month", 1).format('YYYY-MM-DD');
        // this.expirable.notificationDate = this.expirable.nextNotification
      }
      else if (event[0].itemName == 'Yearly') {
        // this.expirable.effectiveDate = moment().format('YYYY-MM-DD');
        this.expirable.nextNotification = moment().add("year", 1).format('YYYY-MM-DD');
        // this.expirable.notificationDate = this.expirable.nextNotification
      }
    }
  }


  selectMonitoringFrequencyType(event: any) {
    debugger
    this.monitoring.frequency = '';
    if (event[0] != undefined) {
      this.monitoring.frequency = event[0].itemName;
      if (event[0].itemName == 'Weekly') {
        //  this.expirable.effectiveDate = moment().format('YYYY-MM-DD');
        this.monitoring.nextCheckDate = moment().add("day", 7).format('YYYY-MM-DD');
      }
      else if (event[0].itemName == 'Monthly') {
        //  this.expirable.effectiveDate = moment().format('YYYY-MM-DD');
        this.monitoring.nextCheckDate = moment().add("month", 1).format('YYYY-MM-DD');
      }
      else if (event[0].itemName == 'Yearly') {
        // this.expirable.effectiveDate = moment().format('YYYY-MM-DD');
        this.monitoring.nextCheckDate = moment().add("year", 1).format('YYYY-MM-DD');
      }
    }
  }

  getProviders() {
    debugger
    this.providerLoaderToggle = true
    if (this.Constant.EMPTY_STRINGS.includes(this.uuid)) {
      this.dataService.showToast('provider not found');
      return;
    }
    this.providerSerivce.getProviderByUuid(this.uuid).subscribe((response) => {
      if (response.status) {
        //@ts-ignore
        this.provider = response.object;
      } else {
        // this.dataService.showToast(response.message);
      }

      this.providerLoaderToggle = false
    }, (error: any) => {
      this.dataService.showToast("Network error");
      this.providerLoaderToggle = false
    })
  }

  notificationFrequencyToggle: boolean = false;
  notify() {
    this.notificationFrequencyToggle = !this.notificationFrequencyToggle;
    if (this.notificationFrequencyToggle) {
      this.expirable.isNotify = 1;
    } else if (!this.notificationFrequencyToggle) {
      this.expirable.isNotify = 0;
    }
  }


  newExpirableToggle: boolean = true;
  newExpirableNotificationToggle: boolean = false
  reviewDetilsToggle: boolean = false
  addNewExpirablesToggle: boolean = false
  newOnGoingMonitoringToggle: boolean = false
  monitoringReviewToggle: boolean = false

  backToExpirable() {
    this.newExpirableToggle = true
    this.newExpirableNotificationToggle = false
    this.reviewDetilsToggle = false
    this.addNewExpirablesToggle = false
    this.newOnGoingMonitoringToggle = false
    this.monitoringReviewToggle = false
    this.progressPer = 33;
    this.step = 1;
  }

  goToExpirable() {
    debugger
    this.expirable = new Expirable();
    this.newExpirableToggle = true
    this.newExpirableNotificationToggle = false
    this.reviewDetilsToggle = false
    this.addNewExpirablesToggle = false
    this.newOnGoingMonitoringToggle = false
    this.monitoringReviewToggle = false
    this.progressPer = 33;
    this.selectedDoc = [];
    this.step = 1;
    this.profilEditmodalButton.nativeElement.click();
  }

  // goToMonitoring() {
  //   this.monitoring = new Monitoring();
  //   this.monitorValidtoggle=false
  //   // this.backToFreq = false
  //   // this.notifyMonitoring();
  //   // this.reviewDetilsToggle = false
  //   // this.addNewExpirablesToggle = false
  //   // this.newOnGoingMonitoringToggle = false
  //   // this.monitoringReviewToggle = false
  //   this.progressPer = 33;
  //   this.selectedDoc = [];
  //   this.step = 1;
  //   // this.profilEditmodalMonitoringButton.nativeElement.click();

  // }

  notificationOngoingrFrequencyToggle: boolean = false;
  notifyMonitoring() {
    this.notificationOngoingrFrequencyToggle = !this.notificationOngoingrFrequencyToggle;
    if (this.notificationOngoingrFrequencyToggle) {
      this.monitoring.isMonitoring = 1;
    } else if (!this.notificationOngoingrFrequencyToggle) {
      this.monitoring.isMonitoring = 0;
    }
  }


  formValidtoggle: boolean = false;
  goToNewNotification() {
    debugger
    this.formValidtoggle = false;
    if (this.newExpirableForm.invalid) {
      this.formValidtoggle = true;
      return;
    }
    if(this.uploadingToggle){
      return;
    }
    this.newExpirableToggle = false
    this.newExpirableNotificationToggle = true
    this.reviewDetilsToggle = false
    this.addNewExpirablesToggle = false
    this.newOnGoingMonitoringToggle = false
    this.monitoringReviewToggle = false
    this.progressPer = 66;
    this.step = 2;
  }

  goBackToNotification() {
    this.newExpirableToggle = false
    this.newExpirableNotificationToggle = true
    this.reviewDetilsToggle = false
    this.addNewExpirablesToggle = false
    this.newOnGoingMonitoringToggle = false
    this.monitoringReviewToggle = false
    this.progressPer = 66;
    this.step = 2;
  }

  @ViewChild('newExpirableNotificationForm') newExpirableNotificationForm!: any;
  formNotificationtoggle: boolean = false
  goToAddNewExpirable() {
    debugger
    this.formNotificationtoggle = false;
    if (this.newExpirableNotificationForm.invalid || (Constant.EMPTY_STRINGS.includes(this.expirable.frequency) && this.expirable.isNotify == 1)) {
      this.formNotificationtoggle = true;
      return;
    }
    this.newExpirableToggle = false
    this.newExpirableNotificationToggle = false
    this.reviewDetilsToggle = false
    this.addNewExpirablesToggle = true
    this.newOnGoingMonitoringToggle = false
    this.monitoringReviewToggle = false
    this.progressPer = 100;
    this.step = 3;
  }

  // @ViewChild('newExpirableNotificationForm') newExpirableNotificationForm!: any;
  // formNotificationtoggle: boolean = false
  goToAddNewMonitoring() {
    debugger
    this.formNotificationtoggle = false;
    if (this.newExpirableNotificationForm.invalid || (Constant.EMPTY_STRINGS.includes(this.expirable.frequency) && this.expirable.isNotify == 1)) {
      this.formNotificationtoggle = true;
      return;
    }
    this.newExpirableToggle = false
    this.newExpirableNotificationToggle = false
    this.reviewDetilsToggle = false
    this.addNewExpirablesToggle = true
    this.newOnGoingMonitoringToggle = false
    this.monitoringReviewToggle = false
    this.progressPer = 50;
    this.step = 2;
  }
  goToReviewDetails() {
    this.newExpirableToggle = false
    this.newExpirableNotificationToggle = false
    this.reviewDetilsToggle = true
    this.addNewExpirablesToggle = false
    this.newOnGoingMonitoringToggle = false
    this.monitoringReviewToggle = false
  }
  goToOngoingMonitoring() {
    this.newExpirableToggle = false
    this.newExpirableNotificationToggle = false
    this.reviewDetilsToggle = false
    this.addNewExpirablesToggle = false
    this.newOnGoingMonitoringToggle = true
    this.monitoringReviewToggle = false
  }

  goToMonitoringReview() {
    debugger
    this.newExpirableToggle = false
    this.newExpirableNotificationToggle = false
    this.reviewDetilsToggle = false
    this.addNewExpirablesToggle = false
    this.newOnGoingMonitoringToggle = false
    this.monitoringReviewToggle = true
  }


  createExpirable() {
    this.expirable.providerUuid = this.uuid;
    this.providerSerivce.createExpirable(this.expirable).subscribe((response) => {
      if (response.status) {
        if(!this.Constant.EMPTY_STRINGS.includes(this.expirable.imagaeUrl)){
          this.attachmentList = [];
          this.getProviderAttachment();
        }
        this.expirableCloseModel()
        this.expirableList = [];
        this.getExpirable();
      }
    }, error => {
      this.dataService.showToast('Network error');
      this.expirableCloseModel()
    })
  }
  dropdownSettingsDocType: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, position: string } = { singleSelection: true, text: 'Select Doc', enableSearchFilter: false, autoPosition: false, position: 'top' };
  getDocType() {
    this.providerSerivce.getDocType().subscribe((response) => {
      response.object.forEach((element: any) => {
        var temp: { id: 0, itemName: "" } = { id: element.id, itemName: element.name }
        this.docType.push(temp);
        // this.fileType.push(temp);
      })
      // this.fileType = JSON.parse(JSON.stringify(this.fileType));
      this.docType = JSON.parse(JSON.stringify(this.docType));
    })
  }

  docFileType: any[] = new Array();
  dropdownSettingsdocumentfileType!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean };

  getDocumentFileType() {
    this.providerSerivce.getDocumentFileType().subscribe((response) => {
      response.object.forEach((element: any) => {
        var temp: { id: 0, itemName: "" } = { id: element.id, itemName: element.name }
        this.docFileType.push(temp);
      })
      /************ add one extra type Other only in vault **************/
      var temp: { id: any, itemName: any } = { id: 0, itemName: "" };
      temp.id = 10;
      temp.itemName = "Other"
      this.docFileType.push(temp);

      this.docFileType = JSON.parse(JSON.stringify(this.docFileType))
    })
  }

  fileDocType: any[] = new Array();
  selectedfileDocType: any[] = new Array();

  selectFileDocType(event: any) {
    this.attachment.fileType = '';
    if (event[0] != undefined) {
      this.attachment.fileType = event[0].itemName;
    }
  }

  expirableCloseModel() {
    debugger
    this.resetUpload();
    this.expirable = new Expirable();
    this.uploadedToggle = false;
    this.selectedDoc = [];
    this.selectedFrequency = [];
    this.newExpirableNotificationToggle = false;
    this.notificationFrequencyToggle = false;
  }
  docAttachmentclose(){
    debugger
    this.attachment = new Attachment();
    this.uploadedToggle = false;
    this.selectedfileDocType = [];
    this.disableIssueDateField = false;
    this.disableExpDateField = false;
    this.issueDateToggle = false;
    this.expirationDateToggle = false;
  }

  monitoringCloseModel() {
    debugger
    this.monitoring = new Monitoring();
    this.uploadedToggle = false;
    this.selectedDoc = [];
    this.disableIssueDateField = false;
    this.disableExpDateField = false;
    this.issueDateToggle = false;
    this.notificationOngoingrFrequencyToggle = false;
    this.attachment = new Attachment();
  }

  disableIssueDateField: boolean = false;
  issueDateToggle: boolean = false
  deSelectIssueField(event: any) {
    debugger
    this.attachment.isTrackable = 0;
    this.attachment.stateCode = '';
    this.attachment.stateName = '';
    if (event.target.checked) {
      this.disableIssueDateField = true;
      this.issueDateToggle = true;
      this.attachment.issueDate = null;
    } else {
      this.disableIssueDateField = false;
      this.issueDateToggle = false;
    }
  }

  disableExpDateField: boolean = false;
  expirationDateToggle: boolean = false
  deSelectExpField(event: any) {
    this.attachment.isTrackable = 0;
    this.attachment.stateCode = '';
    this.attachment.stateName = '';
    if (event.target.checked) {
      this.disableExpDateField = true;
      this.expirationDateToggle = true;
      this.attachment.expirationDate = null;
    } else {
      this.disableExpDateField = false;
      this.expirationDateToggle = false;
    }
  }

  disableMonitoringDateField: boolean = false;
  monitoringDateToggle: boolean = false
  deSelectMonitoringField(event: any) {
    this.monitoring.isTrackable = 0;
    this.monitoring.stateCode = '';
    this.monitoring.stateName = '';
    if (event.target.checked) {
      this.disableMonitoringDateField = true;
      this.monitoringDateToggle = true;
      this.attachment.expirationDate = null;
    } else {
      this.disableMonitoringDateField = false;
      this.monitoringDateToggle = false;
    }
  }

  resetUpload() {
    this.uploadedToggle = false;
    this.attachment.fileName = '';
    this.attachment.fileUrl = '';
    this.expirable.fileName = '';
    this.expirable.imagaeUrl = '';
  }
  step: number = 1;

  @ViewChild('newExpirableForm') newExpirableForm!: any;

  makeExpirable() {
    console.log(this.newExpirableForm.value);
  }

  toggleCartFunc() {
    this.cartToggle = !this.cartToggle;
  }
  togglecloseCartFunc() {
    this.cartToggle = false;
  }

  expirableLoaderToggle: boolean = false
  expirableList: Expirable[] = [];
  selectedExpirable: Expirable = new Expirable();
  expirationCount: number = 0;
  expirableDatabaseHelper: DatabaseHelper = new DatabaseHelper();
  totalExpirable: number = 0;
  searchExpirable: boolean = false;

  diff: any;
  getExpirable() {
    this.expirableDatabaseHelper.searchBy = 'docType';
    this.expirableLoaderToggle = true
    this.providerSerivce.getExpirable(this.uuid, this.expirableDatabaseHelper, this.documentType).subscribe((response) => {

      if (response.object.length > 0) {
        this.searchExpirable = true;
      }

      this.expirableList = [...this.expirableList, ...response.object];
      // this.expirableList = response.object;

      this.totalExpirable = response.totalItems;
      this.expirationCount = 0;
      this.expirableList.forEach(e => {
        // e.effectiveDate = moment(e.effectiveDate);
        e.expirationDateTemp = moment(e.expirationDate);
        e.diff = moment(e.expirationDate).diff(moment().format('YYYY-MM-DD'), 'days')

      })

      this.expirableLoaderToggle = false
    }, (error: any) => {

      this.expirableLoaderToggle = false
    })
  }

  @ViewChild("viewExpirableModalButton") viewExpirableModalButton: ElementRef;
  viewExpirable(expirable: any) {
    debugger
    this.selectedExpirable = expirable;
    if (this.selectedExpirable.frequency == 'Weekly') {
      this.selectedExpirable.nextNotification = moment(this.selectedExpirable.notificationDate).add(1, 'week').format('MM-DD-YYYY');
    } else if (this.selectedExpirable.frequency == 'Monthly') {
      this.selectedExpirable.nextNotification = moment(this.selectedExpirable.notificationDate).add(1, 'month').format('MM-DD-YYYY');
    }
    else if (this.selectedExpirable.frequency == 'Yearly') {
      this.selectedExpirable.nextNotification = moment(this.selectedExpirable.notificationDate).add(1, 'year').format('MM-DD-YYYY');
    }

    this.viewExpirableModalButton.nativeElement.click();
  }
  minExpirationDate!: any;
  setMinExpirationDate() {
    debugger
    if(this.expirable.effectiveDate != null){
      this.minExpirationDate = moment(this.expirable.effectiveDate).add(1, 'month').format('YYYY-MM-DD');
    }
    else if(this.attachment.issueDate != null){
      this.minExpirationDate = moment(this.attachment.issueDate).add(1, 'month').format('YYYY-MM-DD');
    }
  }

  sort(sortBy: string, sortOrder: string) {
    this.expirableDatabaseHelper.sortBy = sortBy;
    this.expirableDatabaseHelper.sortOrder = sortOrder;
    this.expirableList = [];
    this.getExpirable();
  }

  sort1(sortBy: string, sortOrder: string) {
    debugger
    this.databaseHelperAttachment.sortBy = sortBy;
    this.databaseHelperAttachment.sortOrder = sortOrder;
    this.attachmentList = [];
    this.getProviderAttachment();
  }

  sort2(sortBy: string, sortOrder: string) {
    this.monitorDatbaseHelper.sortBy = sortBy;
    this.monitorDatbaseHelper.sortOrder = sortOrder;
    // this.getOngoingMonitoring();
  }

  //Ongoing Monitoring Function -------->

  monitoringLoaderToggle: boolean = false
  monitorDatbaseHelper: DatabaseHelper = new DatabaseHelper();
  monitoringList: Monitoring[] = new Array();
  monitoringCount: number = 0;
  totalMonitoring: number = 0;
  searchOnGoinig: boolean = false;
  getOngoingMonitoring() {
    debugger
    this.monitoringLoaderToggle = true;
    this.monitoringService.getOngoingMonitoring(0, this.uuid, this.monitorDatbaseHelper, this.monitoringDB, '').subscribe((response) => {
      if (response.object.length > 0) {
        this.searchOnGoinig = true;
      }
      this.monitoringList = [...this.monitoringList, ...response.object];
      this.monitoringList = response.object;

      this.monitoringList.forEach(e => {
        e.expirationDate = moment(e.expirationDate);
      })
      this.monitoringLoaderToggle = false;
    }, error => {
      this.monitoringLoaderToggle = false;
    });
  }

  resetMonitorForm() {
    this.backToFreq = false;
    this.monitoring = new Monitoring();
    this.selectedDatabase = [];
    this.selectedOngoingFrequency = [];
    this.monitoring.stateCode = ''
    this.monitoring.stateName = ''
    this.progressPer = 50;
    this.step = 1;
    this.selectedFrequency = [];
    this.getMonitoringDatabase();
  }

  @ViewChild('monitorForm') monitorForm!: any;
  monitorValidtoggle: boolean = false;
  backToFreq: boolean = false;
  goToOngoingReview() {
    debugger
    this.monitorValidtoggle = false;
    if (this.monitorForm.invalid) {
      this.monitorValidtoggle = true;
      return;
    }
    this.backToFreq = true;
    this.progressPer = 100;
    this.step = 2;
  }

  backToOngoing() {
    this.backToFreq = false;
    this.progressPer = 50;
    this.step = 1;
  }

  ongoingMonitor: Monitoring = new Monitoring();
  selectedOngoingFrequency: any[] = new Array();
  selectOngoingFrequencyType(event: any) {
    this.monitoring.frequency = '';
    if (event[0] != undefined) {
      this.monitoring.frequency = event[0].itemName;
      if (event[0].itemName == 'Weekly') {
        this.monitoring.nextCheckDate = moment().add("day", 7).format('YYYY-MM-DD');
      }
      else if (event[0].itemName == 'Monthly') {
        this.monitoring.nextCheckDate = moment().add("month", 1).format('YYYY-MM-DD');
      }
      else if (event[0].itemName == 'Yearly') {
        this.monitoring.nextCheckDate = moment().add("year", 1).format('YYYY-MM-DD');
      }
    }
  }


  selectDatabase(event: any) {
    this.monitoring.isNotify = 1;
    if (event[0] != undefined) {
      // this.monitoring.id = event[0].id;
      this.monitoring.database = event[0].itemName;
    } else {
      // this.monitoring.id = 0;
      this.monitoring.database = "";
    }
  }

  selectMonitoringDatabase(event: any) {
    if (event[0] != undefined) {
      this.monitoring.database = event[0].itemName;
    } else {
      this.monitoring.database = '';
    }
    this.checkMonitoringDuplicacy(this.monitoring.database, this.monitoring.stateName)
  }

  @ViewChild('closemonitorFormButton') closemonitorFormButton!: ElementRef;
  updateOngoingMonitor() {
    debugger
    // this.providerSerivce.addOngoingMonitor(this.ongoingMonitor).subscribe((response: any) => {
    //   this.dataService.showToast('Added');
    //   // this.getOngoingMonitoring();
    //   this.closemonitorFormButton.nativeElement.click();
    // });
  }
  //Harsh Sharma -04--03-2023
  providerDetails: any[] = new Array();
  type: any
  databaseHelper: DatabaseHelper = new DatabaseHelper();
  count: number = 0
  getProviderDetails() {
    this.attachmentService.getproviderNotify(this.type, this.databaseHelper).subscribe((response: any) => {
      this.providerDetails = response;
      // this.expirableList.forEach((e :any)=> {
      //   const diff:any = moment(e.expirationDate).diff(moment().format('YYYY-MM-DD'),'days')
      //   if(diff>=90){
      //     e.count = 0;
      //   }else if(diff> 0 && diff <= 90){
      //     e.count = 1;
      //   }else if(diff< 0){
      //     e.count = 2;
      //   }
      // });
      this.expirableList.forEach(e => {
        // e.effectiveDate = moment(e.effectiveDate);
        debugger
        e.expirationDate = moment(e.expirationDate);
        var diff = e.expirationDate - this.Constant.currentDate;
        if (diff <= 0) {
          this.expirationCount = 2;
        }
        if (diff <= this.Constant.nintyDaysMilisecond && this.expirationCount != 2) {
          this.expirationCount = 1;
        }

      })
    })
  }

  createOngoingMointoring() {
    this.monitoring.providerUuid = this.uuid;
    this.monitoringService.addOngoingMonitoring(this.monitoring).subscribe((response) => {
      this.monitoringList = [];
      this.getOngoingMonitoring()
    })
  }

  // getMonitoring(){
  //   this.monitoring.providerUuid = this.uuid;
  //   this.monitoringService.getOngoingMonitoring(0, this.databaseHelper).subscribe((response)=>{
  //     console.log(response);
  //   })
  // }
  // expriableview: Expirable = new Expirable();
  // expirableId: any;

  selectedMonitoring: Monitoring = new Monitoring();

  ViewOnGoingMointioring(id: any) {
    debugger
    this.monitoringService.getOngoingMonitoring(id, this.uuid, new DatabaseHelper(), '', '').subscribe((response: any) => {
      console.log(response);
      this.selectedMonitoring = response.object[0];
      if (this.selectedMonitoring.checkDate != null) {

        if (this.selectedMonitoring.frequency == 'Weekly') {
          this.selectedMonitoring.nextCheckDate= moment(this.selectedMonitoring.checkDate).add("day", 7).format('YYYY-MM-DD');
        }
        else if (this.selectedMonitoring.frequency == 'Monthly') {
          this.selectedMonitoring.nextCheckDate = moment(this.selectedMonitoring.checkDate).add("month", 1).format('YYYY-MM-DD');
        }
        else if (this.selectedMonitoring.frequency == 'Yearly') {
          this.selectedMonitoring.nextCheckDate = moment(this.selectedMonitoring.checkDate).add("year", 1).format('YYYY-MM-DD');
        }
      }
    }
    )
  }

  //Delete on Expirable

  @ViewChild('deleteOnExpirableModalButton') deleteOnExpirableModalButton!: ElementRef;
  @ViewChild('deleteOnExpirableModalCloseButton') deleteOnExpirableModalCloseButton!: ElementRef;



  @ViewChild("profilEditmodalButton") profilEditmodalButton: ElementRef;
  // @ViewChild("profilEditmodalMonitoringButton") profilEditmodalMonitoringButton:ElementRef;
  editExpirable(i: number) {
    debugger
    // this.expirable = new Expirable();
    this.notificationFrequencyToggle = false;
    this.newExpirableToggle = true
    this.newExpirableNotificationToggle = false
    this.reviewDetilsToggle = false
    this.addNewExpirablesToggle = false
    this.newOnGoingMonitoringToggle = false
    this.monitoringReviewToggle = false
    this.progressPer = 33;
    this.step = 1;
    this.expirable = this.expirableList[i];
    this.selectedDoc = [{ id: this.expirable.docType, itemName: this.expirable.docType }];
    this.selectedFrequency = [{ id: this.expirable.frequency, itemName: this.expirable.frequency }];
    if (this.expirable.isNotify == 1) {
      this.notificationFrequencyToggle = true;

      if (this.expirable.frequency == 'Weekly') {
        this.expirable.nextNotification = moment(this.expirable.notificationDate).add(1, 'week').format('MM-DD-YYYY');
      } else if (this.expirable.frequency == 'Monthly') {
        this.expirable.nextNotification = moment(this.expirable.notificationDate).add(1, 'month').format('MM-DD-YYYY');
      }
      else if (this.expirable.frequency == 'Yearly') {
        this.expirable.nextNotification = moment(this.expirable.notificationDate).add(1, 'year').format('MM-DD-YYYY');
      }
    }

    this.profilEditmodalButton.nativeElement.click();
  }
  isTrackable(event: any) {
    if (event.target.checked) {
      this.attachment.isTrackable = 1;
    } else {
      this.attachment.isTrackable = 0;
      this.attachment.stateCode = null;
      this.attachment.stateName = null;
    }

  }

  isTrackableMonitoring(event: any) {
    if (event.target.checked) {
      this.monitoring.isTrackable = 1;
    } else {
      this.monitoring.isTrackable = 0;
      this.monitoring.stateCode = '';
      this.monitoring.stateName = '';
    }

  }




  /*******************************************  infinite scroll *************************************/

  scrollDownExpirable(event: any) {
    debugger
    if (event.target.offsetHeight + event.target.scrollTop >= event.target.scrollHeight) {
      if (this.expirableList.length < this.totalExpirable && !this.expirableLoaderToggle &&
        this.expirableDatabaseHelper.currentPage <= (this.totalExpirable / this.expirableDatabaseHelper.itemsPerPage)) {
        this.expirableDatabaseHelper.currentPage++;
        this.getExpirable();
      }
    }
  }

  scrollDownProviderAttachment(event: any) {
    debugger
    if (event.target.offsetHeight + event.target.scrollTop >= event.target.scrollHeight) {
      if (this.attachmentList.length < this.totalAttachment && !this.attachmentLoaderToggle &&
        this.databaseHelperAttachment.currentPage <= (this.totalAttachment / this.databaseHelperAttachment.itemsPerPage)) {
        this.databaseHelperAttachment.currentPage++;
        this.getProviderAttachment();
      }
    }
  }

  scrollDownOngoingMonitoring(event: any) {
    debugger
    if (event.target.offsetHeight + event.target.scrollTop >= event.target.scrollHeight) {
      if (this.monitoringList.length < this.totalMonitoring && !this.monitoringLoaderToggle &&
        this.monitorDatbaseHelper.currentPage <= (this.totalMonitoring / this.monitorDatbaseHelper.itemsPerPage)) {
        this.monitorDatbaseHelper.currentPage++;
        this.getOngoingMonitoring();
      }
    }
  }

  /******************************************  infinite scroll end *************************************/



  //-------------------------- check duplicate expirable, attachment and monitoring -------------------------//
  
  checkExpirableDuplicacy(){
    
  }

  checkAttachmentDuplicacy(){
    
  }

  isMonitoringExist:boolean = false;
  checkingMonitoring:boolean = false;
  checkMonitoringDuplicacy(database:string, stateName:string){
    debugger
    this.isMonitoringExist = false;
    if(!this.Constant.EMPTY_STRINGS.includes(database) && !this.Constant.EMPTY_STRINGS.includes(stateName)){
      this.checkingMonitoring = true;
      this.monitoringService.checkMonitoringDuplicacy(database, stateName).subscribe(response=>{
        if(response.status){
          this.isMonitoringExist = true;
        }
        this.checkingMonitoring = false;
      }, (error)=>{
        this.checkingMonitoring = false;
      })
    }
  }
  


  //---------------------------- check duplicate expirable, attachment and monitoring end ---------------------//



  downloadSingleImage(imageUrl: any, name: any) {
    debugger
    var blob = null;
    var splittedUrl = imageUrl.split("/firebasestorage.googleapis.com/v0/b/credily-v3.appspot.com/o/");
    splittedUrl = splittedUrl[1].split("?alt");
    splittedUrl = splittedUrl[0].replace("https://", "");
    splittedUrl = decodeURIComponent(splittedUrl);
    this.firebaseStorage.storage.ref(splittedUrl).getDownloadURL().then((url: any) => {
      // `url` is the download URL for 'images/stars.jpg'

      // This can be downloaded directly:
      var xhr = new XMLHttpRequest();
      xhr.responseType = 'blob';
      xhr.onload = (event) => {
        blob = xhr.response;
        console.log(blob);
        saveAs(blob, name);
        // var file:any = new File([blob], 'image.png',

      };
      xhr.open('GET', url);
      xhr.send();
    })
      .catch((error) => {
        console.log(error);
        // Handle any errors
      });
  }

  updateExpirable() {
    this.expirable.providerUuid = this.uuid;
    this.providerSerivce.UpdateExpirable(this.expirable).subscribe((response) => {
      if (response.status) {
        this.expirableCloseModel();
        this.expirableList = [];
        this.getExpirable();
      }
    }, error => {
      this.expirableCloseModel()
      this.dataService.showToast('Network error');
    })
  }

  getMonitoringDatabase() {
    this.databaseList = [];
    this.monitoringService.getMonitoringDatabase().subscribe((response) => {
      response.forEach((element: any) => {
        var temp: { id: number, itemName: string } = { id: element.id, itemName: element.name }

        this.databaseList.push(temp);
      })

      this.databaseList = JSON.parse(JSON.stringify(this.databaseList));
    })
  }


  //-------------------------------Share Document Function start---------------------------------------->>


  documentShareRequest: DocumnetShareRequest = new DocumnetShareRequest();
  email: string = '';
  phone: string = '';
  @ViewChild('shareModalButton') shareModalButton!: ElementRef;
  fileUrl: string = '';
  key: Key = new Key();
  sharingToggle: boolean = false;
  documentId: number = 0;
  errorMessage: string = '';
  errorToggle: boolean = false;
  createToggle: boolean = false;

  selectedOption: string = 'Mail';

  selectType(event: any) {
    if (event[0] != undefined) {
      this.selectedOption = event[0].itemName;
    }
  }

  countryCodeObj: CountryCode = new CountryCode();
  countryCodeList: any[] = new Array();
  countryCode: any[] = new Array();
  getCountryCode() {
    this.countryCodeList=[];
    this.countryCodeObj.counrtyCodes.forEach(element => {
      var temp: { id: any, itemName: any } = { id: '', itemName: '' };
      temp.id = element.dial_code
      temp.itemName = element.dial_code
      this.countryCodeList.push(temp);
    });
    this.countryCodeList = JSON.parse(JSON.stringify(this.countryCodeList));
  }

  selectedCountryCode: any;
  invalidToggle: boolean = false;

  selectCountryCode(event: any) {
    if (event[0] != undefined) {
      this.selectedCountryCode = event[0].itemName;
    }
  }

  @ViewChild('shareDocForm') shareDocForm!: any;

  createDocumentShare() {
    this.invalidToggle = false;
    if (this.shareDocForm.invalid) {
      this.invalidToggle = true;
      return
    }

    this.errorToggle = false;
    this.createToggle = true;

    this.documentShareRequest.documentId = this.documentId;
    if (this.selectedOption == 'Mail') {
      this.documentShareRequest.email = this.email;
    } else {
      this.documentShareRequest.email = this.selectedCountryCode + this.email;
    }

    this.documentShareService.createDocumentShare(this.documentShareRequest).subscribe(response => {
      if (response.status && response.isEnable == 1) {
        if (this.selectedOption == 'Mail') {
          this.email = response.email;
        } else {
          this.email = '';
        }
        this.fileUrl = this.key.host_panel + "view-document?id=" + response.uuid + "&option=" + this.selectedOption;
        this.createToggle = false;
        this.sharingToggle = true;
      } else if (response.status && response.isEnable == 0) {
        this.errorToggle = true;
        this.errorMessage = 'This email id is disabled by you, please enable it to send url !!';
        this.createToggle = false;
        this.sharingToggle = false;
      }

      this.shareGmailUrl = 'https://mail.google.com/mail/u/0/?view=cm&fs=1&to=' + this.email + '&su=Document_Url&body=';
    })

  }

  shareSelectedDocName:any;
  openShareModel(obj: any) {
    debugger
    this.shareSelectedDocName = obj.fileType;
    this.selectedType = [];
    this.email = '';
    this.copying = false;
    this.errorMessage = '';
    this.countryCode = [];
    this.selectedOption = 'Mail';
    var temp: { id: any, itemName: any } = { id: '', itemName: '' };
    temp.id = 'Mail'
    temp.itemName = 'Mail'
    this.selectedType.push(temp);
    this.getCountryCode();
    this.documentId = obj.id;
    this.sharingToggle = false;
    this.shareModalButton.nativeElement.click();

  }

  copying: boolean = false;
  copy() {
    window.addEventListener('copy', (event) => {
      let clipboardEvent: ClipboardEvent = <ClipboardEvent>event;
      clipboardEvent.preventDefault(); //stop the browser overwriting the string
      clipboardEvent.clipboardData!.setData("text/plain", this.fileUrl); //encode the appropriate string with MIME type "text/plain"
    });
    this.copying = true;
    // this.dataService.showToast("Link copied");
    document.execCommand('copy');
  }

  shareWhatsAppUrl: string = 'https://web.whatsapp.com/send?text=';
  shareGmailUrl: string = '';
  // messageContent: string = "Visit our e-commerce website here " + this.fileUrl + ". Our website is designed to make your shopping experience as smooth and stress-free as possible. We offer multiple payment options, fast and reliable shipping, and a hassle-free return policy to ensure that you can shop with confidence. With our wide selection of quality products and unbeatable prices, you won't be disappointed."

  @ViewChild('otpModalButton') otpModalButton: ElementRef;
  otp: any;
  getOtp(obj: any) {
    this.documentId = obj.id;
    this.getAllDocumentShared();
    this.otpModalButton.nativeElement.click();
    // this.attachmentService.getOtp(obj.id).subscribe((response:any)=>{
    //   if(response.object != null){
    //     this.otp = response.object;
    //     this.otpModalButton.nativeElement.click();
    //   }else{
    //       this.dataService.showToast('Share To Generate OTP');
    //   }
    // });
  }

  shareOnWhatsApp() {
    window.open(this.shareWhatsAppUrl + encodeURIComponent(this.fileUrl));
    return false;
  }

  shareOnGmail() {
    debugger
    window.open(this.shareGmailUrl + encodeURIComponent(this.fileUrl));
    return false;
  }

  //-------------------------------Share Document Function end------------------------------------------<<

  // changeNotificationStatus() {
  //   // this.expirable = expirableObj;
  //   if (this.expirable.isNotify == 0) {
  //     this.expirable.isNotify = 1;
  //   } else {
  //     this.expirable.isNotify = 2;
  //   }
  //   this.updateStatus();
  // }

  updateProviderProfileStatus() {
    if (this.provider.statusId == 1) {
      this.provider.statusId = 2;
    } else {
      this.provider.statusId = 1;
    }
    this.providerSerivce.updateStatus(this.uuid).subscribe(response => {
      if (response.status) {
        this.dataService.showToast(response.message);
      }
    })
  }


  /******************************* firebase setup for on going monitoring status ****************************/

  fireBase() {
    debugger
    var url = Constant.FIREBASE_MONITORING_STATUS;
    console.log("/" + url + "/" + this.uuid);
    this.firebaseDatabase.object("/" + url + "/" + this.uuid).valueChanges().subscribe((res: any) => {

      console.log('firebase response : ' + JSON.stringify(res));

      //@ts-ignore
      if (res != null && res.status == "COMPLETED") {
        this.getOngoingMonitoring();
      }
    });

  }

  totalSharedDocument: number = 0;
  sharedDocLoadingToggle: boolean = false;
  sharedDocumentList: DocumnetShareRequest[] = new Array();
  sharedDocumentDataBaseHelper: DatabaseHelper = new DatabaseHelper();
  getAllDocumentShared() {
    this.sharedDocLoadingToggle = true;
    this.documentShareService.getAllDocumentShared(this.documentId, this.sharedDocumentDataBaseHelper).subscribe(response => {
      if (response.status) {
        this.sharedDocumentList = response.object;
        this.totalSharedDocument = response.totalItems;
        this.sharedDocLoadingToggle = false;
      }
    })
  }

  sharedDocumentPageChanged(event: any) {
    if (event != this.sharedDocumentDataBaseHelper.currentPage) {
      this.sharedDocumentDataBaseHelper.currentPage = event;
      this.getAllDocumentShared();
    }
  }

  changeStatus(obj: any) {
    this.documentShareService.changeStatus(obj.uuid).subscribe(response => {
      if (response.status) {
        // this.dataService.showToast(response.message);
        this.getAllDocumentShared();
      }
    })
  }


  //------------------------------------------Record Delete Function Start---------------------------------------->>

  deleteContentType: string = '';
  shareDocUUID: any;

  deleteLoading: boolean = false;
  deleteIndexs: number = -1;
  deleteIndex: number = -1;

  @ViewChild('closeModal') closeModal!: ElementRef;
  @ViewChild('deleteModalButton') deleteModalButton!: ElementRef;
  @ViewChild('deleteModalCloseButton') deleteModalCloseButton!: ElementRef;

  openDeleteModel(id: any, type: string) {
    this.deleteContentType = type;
    if (this.deleteContentType == 'Expirable') {
      this.deleteIndexs = id;
    } else if (this.deleteContentType == 'On-going Monitoring') {
      this.deleteIndex = id;
    } else if (this.deleteContentType == 'Shared Document') {
      this.shareDocUUID = id;
      this.closeModal.nativeElement.click();
    }
    this.deleteModalButton.nativeElement.click();
  }

  deleteSharedDoc() {
    this.documentShareService.deleteSharedDoc(this.shareDocUUID).subscribe(response => {
      if (response.status) {
        this.dataService.showToast(response.message);
        this.deleteModalCloseButton.nativeElement.click();
      }
    })
  }

  deleteOngoingMointoring() {
    debugger
    this.deleteLoading = true;
    this.providerSerivce.deleteOngMointoringById(this.monitoringList[this.deleteIndex].id, this.uuid).subscribe((response: any) => {
      this.deleteLoading = false;
      this.dataService.showToast('Deleted Successfully');
      this.monitoringList.splice(this.deleteIndex, 1);
      this.deleteModalCloseButton.nativeElement.click();
    }, (error: any) => {
      this.deleteLoading = false;
      this.dataService.showToast('Something went wrong!');
    })
  }

  deleteOnExpirable() {
    debugger
    this.deleteLoading = true;
    this.providerSerivce.DeleteOnExpirableById(this.expirableList[this.deleteIndexs].id, this.uuid).subscribe((response: any) => {
      this.deleteLoading = false;
      this.dataService.showToast('Deleted Successfully');
      this.expirableList.splice(this.deleteIndexs, 1);
      this.deleteModalCloseButton.nativeElement.click();
      // this.getOngoingMonitoring();
    }, (error: any) => {
      this.deleteLoading = false;
      this.dataService.showToast('Something went wrong!');
    })
  }

  //-------------------------------------------Record Delete Function End-----------------------------------------<<


  /************************* get isuue date and expiration date from doc type and state code ****************************/
  
  extractingDate:boolean=false;
  getIssueAndExpirationDateFromDoc(docType:string, stateCode:string){
    if(this.Constant.EMPTY_STRINGS.includes(docType) || this.Constant.EMPTY_STRINGS.includes(stateCode)){
      return;
    }
    this.extractingDate = true;
    this.providerSerivce.extractDateFromProviderDoc(this.uuid, docType, stateCode).subscribe(response=>{
      if(response.status && response.object != null){
        if (this.uploadImageFor == Constant.DOC_TYPE_ATTACHMENT) {
          this.attachment.issueDate = moment(response.object.effectiveDate).format('YYYY-MM-DD');
          this.attachment.expirationDate = moment(response.object.expirationDate).format('YYYY-MM-DD');
        }else{
          this.expirable.effectiveDate = moment(response.object.effectiveDate).format('YYYY-MM-DD');
          this.expirable.expirationDate = moment(response.object.expirationDate).format('YYYY-MM-DD');
        }
      }
      this.extractingDate = false;
    }, (error)=>{
      this.extractingDate = false;
    })
  }

  monitoringToggle: boolean = false;
  callMonitoringFunc() {
    this.monitoringToggle = !this.monitoringToggle;
    if (this.monitoringToggle) {
      this.monitoringList = [];
      this.getOngoingMonitoring();
    }
  }


  expirableToggle: boolean = false;
  callExpirableFunc() {
    this.expirableToggle = !this.expirableToggle;
    if (this.expirableToggle) {
      this.expirableList = [];
      this.getExpirable();
    }
  }

  documentToggle: boolean = false;
  callAttachmentFunc() {
    this.documentToggle = !this.documentToggle;
    if (this.documentToggle) {
      this.attachmentList = [];
      this.getProviderAttachment();
    }
  }


}

