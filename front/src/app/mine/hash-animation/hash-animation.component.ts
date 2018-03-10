import {AfterViewInit, Component, ElementRef, Input, OnInit, Renderer2, ViewChild} from '@angular/core';

@Component({
  selector: 'app-hash-animation',
  templateUrl: './hash-animation.component.html',
  styleUrls: ['./hash-animation.component.scss']
})
export class HashAnimationComponent implements OnInit, AfterViewInit {

  @ViewChild('canvas') canvas: ElementRef;
  context: CanvasRenderingContext2D;

  @Input() runAnimation: boolean;

  width: number;
  height: number;

  lines: Line[];

  lineHeight: number;
  speed: number;
  nextLineTimeoutCounter: number;
  lastClock: number;
  nextLineTreshold;

  constructor(private renderer: Renderer2) { }

  ngOnInit() {

    this.width = 250;
    this.height = 220;
    this.lineHeight = 20;
    this.speed = 200;
    this.nextLineTimeoutCounter = 0;
    this.lastClock = 0;
    this.lines = [];
    this.nextLineTreshold = 10;

    for(let i = 0; i < this.height; i += this.nextLineTreshold) this.lines.push(new Line(i));
  }

  ngAfterViewInit() {

    this.context = this.canvas.nativeElement.getContext('2d');

    this.context.font='12px Courier New';
    this.context.fillStyle = '#acaab9';
    this.context.strokeStyle = this.context.fillStyle;
    this.context.textAlign = 'right';
    if(this.runAnimation) this.startAnimation();
  }

  startAnimation() {

    setInterval(this.clean.bind(this), 2000);
    this.runAnimation = true;
    this.render(this.lastClock);
  }

  render(clock: number) {

    let ctx = this.context;

    let delta = clock - this.lastClock;
    this.lastClock = clock;

    if(this.nextLineTimeoutCounter > this.nextLineTreshold) {
      this.nextLineTimeoutCounter = 0;
      this.lines.push(new Line(this.height - 10));
    }

    let deltaPx = delta * this.speed / 1000;
    this.nextLineTimeoutCounter += deltaPx;

    ctx.clearRect(0, 0, this.width, this.height);

    this.lines.forEach(line => {

      line.position -= deltaPx;
      if(line.position < -this.lineHeight) line.active = false;
      ctx.fillText(line.hash, this.width, line.position, this.width);
    });

    ctx.fill();

    if(this.runAnimation) requestAnimationFrame(this.render.bind(this));
  }

  clean() {

    this.lines = this.lines.filter(line => line.active);
  }
}

function hexString(length) {

  let str = '';

  for(let i = 0; i < length; i++) {
    let num = Math.floor(Math.random() * 16);
    str += num.toString(16).toUpperCase();
  }

  return str;
}

class Line {

  hash: string;
  position: number;
  active: boolean;

  constructor(startPosition: number) {
    this.hash = hexString(35);
    this.position = startPosition;
    this.active = true;
  }
}
