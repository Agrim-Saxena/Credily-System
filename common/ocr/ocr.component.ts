import { Element } from '@angular/compiler';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import * as _ from 'lodash';
import { Constant } from 'src/app/constants/Constant';
import { AttachmentService } from 'src/app/services/attachment.service';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-ocr',
  templateUrl: './ocr.component.html',
  styleUrls: ['./ocr.component.css']
})
export class OcrComponent implements OnInit {

  constructor(
    private attachmentService: AttachmentService,
    private domSanitizer: DomSanitizer) { }

  
  readonly Constant = Constant;
  ngOnInit(): void {
  }

  uploadedToggle: boolean = false;
  uploadingToggle: boolean = false;
  currentFileUpload: File | null;
  selectFile(event: any) {
    debugger
    let fileList!: FileList;
    if (event != null) {
      fileList = event.target.files;
    }
    for (var i = 0; i < fileList.length; i++) {
      this.currentFileUpload = fileList.item(i);
    }
    if (this.currentFileUpload != null) {
 
      const formdata: FormData = new FormData();
      formdata.append('file', this.currentFileUpload);
      this.uploadFile(formdata,event);
    }

    const reader = new FileReader();
    this.fileurl = fileList;
    reader.readAsDataURL(fileList[0]); 
    reader.onload = (_event) => { 
      this.fUrl = reader.result; 
      }

  }
  
  fileurl:any
  fUrl:any
  resp:any;
  uploadFile(formdata: any,event:any) {
    debugger
    this.uploadingToggle = true;
    this.attachmentService.extractText(formdata).subscribe((response: any) => {
      //@ts-ignore
      console.log(response)
      
      if (response.status) {
        this.resp = response.response;
        this.uploadedToggle = true;
      }
      event.target.value = '';
      this.uploadingToggle = false;
    }, (error: any) => {
      this.uploadingToggle = false;
      event.target.value = '';

    });
  }
}
