import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AccountService} from '../service/account.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, AfterViewInit {

  @ViewChild('fileInput') fileInput: ElementRef;

  state: string;

  constructor(private accountService: AccountService) {

    this.state = 'base';
  }

  ngOnInit() {
  }

  ngAfterViewInit() {

    this.fileInput.nativeElement.addEventListener('change', this.readFile.bind(this), false);
  }

  clickLogin() {

    this.fileInput.nativeElement.click();
  }

  readFile(e) {

    let file = e.target.files[0];
    if (!file) return;

    let reader = new FileReader();
    reader.onload = this.readerOnLoad.bind(this);
    reader.readAsText(file);
  }

  verifyKeyfile(fileContent: string): boolean {

    return true;
  }

  readerOnLoad(e: any) {

    let content = e.target.result;
    if(this.verifyKeyfile(content)) this.accountService.keyfileContent = content;
    this.state = 'login';
  }

}
