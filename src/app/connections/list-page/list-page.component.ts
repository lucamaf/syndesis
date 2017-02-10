import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';

import { log, getCategory } from '../../logging';
import { ConnectionStore } from '../../store/connection/connection.store';
import { Connections, Connection } from '../../store/connection/connection.model';

const category = getCategory('Connections');

@Component({
  selector: 'ipaas-connections-list-page',
  templateUrl: './list-page.component.html',
  styleUrls: ['./list-page.component.scss'],
})
export class ConnectionsListPage implements OnInit {

  connections: Observable<Connections>;

  loading: Observable<boolean>;

  constructor(private store: ConnectionStore,
              private router: Router) {
    this.loading = store.loading;
    this.connections = store.list;
  }

  ngOnInit() {
    this.store.loadAll();
  }

  onSelected(connection: Connection) {
    this.router.navigate(['connections', connection.id]);
  }

}
