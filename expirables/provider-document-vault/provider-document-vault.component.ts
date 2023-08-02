import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DatabaseHelper } from 'src/app/models/DatabaseHelper';
import { DataService } from 'src/app/services/data.service';
import { ImageUpload } from 'src/app/models/common-json/ImageUpload';
import * as _ from 'lodash';
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs/operators';
import { Attachment } from 'src/app/models/common-json/Attachment';
import { AttachmentService } from 'src/app/services/attachment.service';
import { AngularFireStorage, createStorageRef } from '@angular/fire/compat/storage';
import * as moment from 'moment';
import { ProviderService } from 'src/app/services/provider.service';
import { saveAs } from 'file-saver';
import { Constant } from 'src/app/constants/Constant';
import { HttpClient } from '@angular/common/http';
import { Subject } from 'rxjs';
import { Route } from 'src/app/constants/Route';

@Component({
  selector: 'app-provider-document-vault',
  templateUrl: './provider-document-vault.component.html',
  styleUrls: ['./provider-document-vault.component.css']
})
export class ProviderDocumentVaultComponent implements OnInit {

  dropdownSettingsfileType!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, position:string };

  readonly Constant=Constant;
  readonly Route = Route;
  documentSearch = new Subject<string>();
  constructor(public dataService: DataService,
    public attachmentService: AttachmentService,
    private _routeParams: ActivatedRoute,
    private firebaseStorage: AngularFireStorage,
    private providerService: ProviderService,
    private http: HttpClient
  ) {
    this.documentSearch.pipe(
    debounceTime(600),
    distinctUntilChanged())
    .subscribe(value => {
      this.databaseHelper.currentPage=1;
      this.getAllProviderAttachment();
    });
  }

  uuid: any;
  ngOnInit() {
    this.dropdownSettingsfileType = {
      singleSelection: true,
      text: 'Select File Type',
      enableSearchFilter: true,
      autoPosition: false,
      position:'bottom'
    };
    if (this._routeParams.snapshot.queryParamMap.has('uuid')) {
      this.uuid = this._routeParams.snapshot.queryParamMap.get('uuid');
    } else {
      this.uuid = this.dataService.getUserUUID();
    }
    // this.getfileType();
    this.getAllProviderAttachment();
  }

  databaseHelper: DatabaseHelper = new DatabaseHelper();
  attachments: Attachment[] = new Array();
  totalItems: number = 0;
  loadAllDatatoggle:boolean = false;
  getAllProviderAttachment() {
    this.loadAllDatatoggle = true;
    this.attachmentService.getAttachmentByPage(this.uuid, this.databaseHelper).subscribe((response: any) => {
      this.attachments = response.object;
      this.totalItems = response.totalItems;
      this.loadAllDatatoggle = false;
    },(error:any)=>{
      this.loadAllDatatoggle = false;
    })
  }

  uploadedToggle: boolean = false;
  uploadingToggle: boolean = false;
  urrentCount: any;
  currentUpload: any;
  map = new Map();
  private basePath: string = "credily-v3/";
  uploadPercent: any;
  urls: any[] = new Array();
  files: any[] = new Array();
  selectedFiles: any[] = new Array();
  tempProgress : number=0;
  progress : number = 0;
  fileName: string = "";

  onDrop(event: any) {
    debugger
    this.uploadingToggle = true;
    this.urls = new Array();
    this.files = new Array();
    this.selectedFiles = event;
    this.files = event[0];
    this.urls = event[0];
    this.uploadMulti();
  }



  file:any;
  onSelectFile(event: any) {
    debugger
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
    }
  }


  uploadMulti() {
    debugger
    this.attachment.fileName = '';
    this.progress = 0;
    let files = this.selectedFiles;
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
      let imageName = this.basePath + this.uuid + "/" + "I" + "/" + name + new Date().toString();
      const fileRef = this.firebaseStorage.ref(imageName);
      this.uploadPercent = this.firebaseStorage.upload(imageName, this.currentUpload.file).percentageChanges();
      console.log(JSON.stringify(this.uploadPercent));
      this.firebaseStorage.upload(imageName, this.currentUpload.file).snapshotChanges().pipe(
        finalize(async () => {
          fileRef.getDownloadURL().subscribe((url: any) => {
            var urlString = url;
            this.attachment.fileUrl = urlString;
            this.attachment.fileName = name;
            this.urlErrorMessage = false;
            if(!Constant.EMPTY_STRINGS.includes(this.attachment.fileUrl)){
              this.extractDate(this.attachment.fileUrl, this.currentUpload.file.type);
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

  extractDate(url:string, type:any){
    this.attachmentService.extractDate(url, type).subscribe((response:any) =>{
      if(response.status && response.object != null){
        this.attachment.issueDate = moment(response.object.effectiveDate).format('YYYY-MM-DD');
        this.attachment.expirationDate = moment(response.object.expirationDate).format('YYYY-MM-DD');
      }
      this.uploadingToggle = false;
    }, error=>{
      this.uploadingToggle = false;
    })
  }

  // fileTypeObj: fileTypeJsonData = new fileTypeJsonData();
  selectedfileType: any[] = new Array();
  fileTypeListing: any[] = new Array();
  fileType: any[] = new Array();
  getfileType() {
    debugger
    this.providerService.getDocType().subscribe((response)=>{
      this.fileType = [];
      response.object.forEach((element:any)=>{
        var temp : {id : 0, itemName : ""} = { id: element.id , itemName: element.name};
        this.fileType.push(temp);
      })

      /************ add one extra type Other only in vault **************/
      var temp : {id : any, itemName : any} = { id: 0 , itemName: ""};
      temp.id = 10;
      temp.itemName = "Other"
      this.fileType.push(temp);

      this.fileType = JSON.parse(JSON.stringify(this.fileType));
    })
  }

  selectFileType(event: any) {
    this.attachment.fileType = '';
    if (event[0] != undefined) {
      this.attachment.fileType = event[0].itemName;
    }
  }
  expirableCloseModel(){
    debugger
    this.disableIssueDateField = false;
    this.disableExpDateField = false;
    this.issueDateToggle = false;
    this.uploadedToggle = false;
    this.expirationDateToggle = false;
    this.attachment = new Attachment(); 
  }

  disableIssueDateField:boolean = false;
  issueDateToggle:boolean = false
  deSelectIssueField(event:any){
    debugger
    this.attachment.isTrackable = 0;
    this.attachment.stateCode='';
    this.attachment.stateName='';
    if(event.target.checked){
      this.disableIssueDateField = true;
      this.issueDateToggle = true;
      this.attachment.issueDate = null;
    }else{
      this.disableIssueDateField = false;
      this.issueDateToggle = false;
    }
  }

  disableExpDateField:boolean = false;
  expirationDateToggle:boolean = false
  deSelectExpField(event:any){
    this.attachment.isTrackable = 0;
    this.attachment.stateCode='';
    this.attachment.stateName='';
    if(event.target.checked){
      this.disableExpDateField = true;
      this.expirationDateToggle = true;
      this.attachment.expirationDate = null;
    }else{
      this.disableExpDateField = false;
      this.expirationDateToggle = false;
    }
  }
  resetUpload(){
    this.uploadedToggle = false;
    this.attachment.fileName = '';
    this.attachment.fileUrl = '';
  }

  isTrackable(event:any){
    if(event.target.checked){
      this.attachment.isTrackable = 1;
    }else{
      this.attachment.isTrackable = 0;
      this.attachment.stateCode = null;
      this.attachment.stateName = null;
    }
  }

  attachment: Attachment = new Attachment();
  fileCreateToggle: boolean = false;
  @ViewChild('fileFormCloseButton') fileFormCloseButton!: ElementRef;

  urlErrorMessage:boolean=false;
  invalidFileForm:boolean = false;
  @ViewChild('fileForm') fileForm!: any;
  createAttachment() {
    debugger

    this.urlErrorMessage=false;
    if(this.uploadingToggle){
      return;
    }
    if(Constant.EMPTY_STRINGS.includes(this.attachment.fileUrl)){
      this.urlErrorMessage=true;
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

    this.fileCreateToggle = true;
    this.attachment.providerUUID = this.uuid;
    this.attachmentService.createProviderAttachment(this.attachment).subscribe((response: any) => {
      this.fileFormCloseButton.nativeElement.click();
      this.selectedfileType = [];
      this.attachment = new Attachment();
      this.dataService.showToast('Added Successfully');
      this.getAllProviderAttachment();
      this.fileCreateToggle = false;
      this.uploadingToggle = false
    }, (error: any) => {
      this.dataService.showToast('Something went wrong!');
      this.fileCreateToggle = false;
    });
  }

  @ViewChild('attachmentDeleteModal') attachmentDeleteModal!: ElementRef;
  @ViewChild('closeDeleteModel') closeDeleteModel!: ElementRef;

  attachmentId: number = 0;
  openDeleteModal(obj: any) {
    this.attachmentId = obj.id;
    this.attachmentDeleteModal.nativeElement.click();
  }

  deleteLoading: boolean = false;
  delete() {
    debugger
    this.deleteLoading = true;
    this.attachmentService.deleteAttachmentById(this.attachmentId).subscribe((response: any) => {
      this.deleteLoading = false;
      this.dataService.showToast('Deleted Successfully');
      this.closeDeleteModel.nativeElement.click();
      this.getAllProviderAttachment();
    }, (error: any) => {
      this.deleteLoading = false;
      this.dataService.showToast('Something went wrong!');
    })

  }

  openUpdateModal(obj:any){
    this.attachmentId = obj.id;
    this.getfileType();
    this.getAttachmentById();
  }

  clearField(){
    this.attachment = new Attachment();
    this.issueDateToggle=false;
    this.expirationDateToggle=false;
    this.selectedfileType = [];
    this.getfileType();
  }

  loadToggle:boolean = false;
  getAttachmentById(){
    this.loadToggle = true;
    this.attachmentService.getAttachmentById(this.attachmentId).subscribe((response:any)=>{
      this.attachment = response.object;
      if(this.attachment.fileType != null){
        this.selectedfileType = [];
        var temp:{id:"",itemName:""} = {id:this.attachment.fileType,itemName:this.attachment.fileType}
        this.selectedfileType.push(temp);
      } 
      if(Constant.EMPTY_STRINGS.includes(this.attachment.issueDate)){
        this.issueDateToggle=true;
      }else{
        this.attachment.issueDate = moment(this.attachment.issueDate).format('YYYY-MM-DD');
      }
      if(Constant.EMPTY_STRINGS.includes(this.attachment.expirationDate)){
        this.expirationDateToggle=true;
      }else{
        this.attachment.expirationDate = moment(this.attachment.expirationDate).format('YYYY-MM-DD');
      }
      
      
      this.loadToggle = false;
    },(error:any)=>{
      this.loadToggle = false; 
    });
  }

  @ViewChild('fileUpdateCloseButton') fileUpdateCloseButton!:ElementRef;
  updateToggle:boolean = false;
  updateAttachment(){
    debugger
    this.updateToggle = true;
    this.attachment.providerUUID = this.uuid;
    this.attachmentService.updateProviderAttachment(this.attachment).subscribe((response:any)=>{
      this.fileUpdateCloseButton.nativeElement.click();
      this.updateToggle = false; 
      this.getAllProviderAttachment();
      this.dataService.showToast(response.object);
    },(error:any)=>{
      this.updateToggle = false; 
    });

  }

  pageChanged(event:any){
    if(event!=this.databaseHelper.currentPage){
      this.databaseHelper.currentPage = event;
      this.getAllProviderAttachment();
    }
   
  }

  searchAttachment:string='';
  search(name:any, event:any) {

    if (event != undefined && event != null) {
      var inp = String.fromCharCode(event.keyCode);

      if (name.length != 0 && /[a-zA-Z0-9 ]/.test(inp)) {
        this.databaseHelper.search = name;
        this.getAllProviderAttachment();
      } 
    }
    if (name.length == 0) {
      this.databaseHelper.search = "";
      this.searchAttachment='';
      this.getAllProviderAttachment();
    }
  }

  resetSearchRole(){
    this.searchAttachment='';
    this.getAllProviderAttachment();
  }
  
  downloadSingleImage(imageUrl: any, name: any){
    debugger
     var blob =null;
     var splittedUrl=imageUrl.split("/firebasestorage.googleapis.com/v0/b/credily-v3.appspot.com/o/");
     splittedUrl=splittedUrl[1].split("?alt");
     splittedUrl=splittedUrl[0].replace("https://","");
     splittedUrl=decodeURIComponent(splittedUrl);
    this.firebaseStorage.storage.ref(splittedUrl).getDownloadURL().then((url:any) => {
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

    //-------------------------------Share Document Function start---------------------------------------->>

    @ViewChild('shareModalButton') shareModalButton!: ElementRef;
    fileUrl: string = '';
    selectedShareDocName:string='';
    openShareModel(obj: any) {
      debugger
      this.copying = false;
      this.fileUrl = obj.fileUrl
      this.selectedShareDocName = obj.fileType;
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
    shareGmailUrl: string = 'https://mail.google.com/mail/u/0/?view=cm&fs=1&to=&su=Document_Url&body=';
    // messageContent: string = "Visit our e-commerce website here " + this.fileUrl + ". Our website is designed to make your shopping experience as smooth and stress-free as possible. We offer multiple payment options, fast and reliable shipping, and a hassle-free return policy to ensure that you can shop with confidence. With our wide selection of quality products and unbeatable prices, you won't be disappointed."
  
  
  
    shareOnWhatsApp() {
      window.open(this.shareWhatsAppUrl + encodeURIComponent(this.fileUrl));
      return false;
    }
  
    shareOnGmail() {
      window.open(this.shareGmailUrl + encodeURIComponent(this.fileUrl));
      return false;
    }
  
    //-------------------------------Share Document Function end------------------------------------------<<

    minExpirationDate!: any;
    setMinExpirationDate(){
      this.minExpirationDate = moment(this.attachment.issueDate).add(1,'month').format('YYYY-MM-DD');
    }


    gotoPreviousPage(){
      window.history.back();
    }

    
}

