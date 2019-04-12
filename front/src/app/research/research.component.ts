import { StarSystem } from 'src/app/game-objects/star-system';
import { Component, OnInit } from '@angular/core';
import { Store } from '../store';
import { ResearchOrder } from '../game-objects/research-order';
import { TextService } from '../services/text.service';

@Component({
  selector: 'app-research',
  templateUrl: './research.component.html',
  styleUrls: ['./research.component.css']
})
export class ResearchComponent implements OnInit {

  now = 1;

  constructor(public store: Store, public ts: TextService) { }

  ngOnInit() {
    setInterval(() => {
      this.now = new Date().getTime();
    }, 87);
  }

  getProgress(order: ResearchOrder) {
    const totalTime = order.finishTime - order.startedTime;

    return (100 * (this.store.gameTime - order.startedTime) / totalTime).toFixed(1);
  }

  getStarSystemName(order: ResearchOrder): string {
    const s = this.store.getObjectById(order.starSystem) as StarSystem;
    return s.name;
  }
}
