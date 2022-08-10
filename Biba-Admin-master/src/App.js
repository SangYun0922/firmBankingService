// in src/App.js
import * as React from "react";
import {
  Admin,
  Resource,
  ListGuesser,
  EditGuesser,
  fetchUtils,
  useCreate,
  ShowGuesser,
} from "react-admin";

import UserIcon from "@material-ui/icons/Group";
import AssignmentIcon from "@material-ui/icons/Assignment";
import ContactMailIcon from "@material-ui/icons/ContactMail";
import LocalBarIcon from "@material-ui/icons/LocalBar";
import CommentIcon from "@material-ui/icons/Comment";
// import authProvider from "./authProvider";
import { BeerList, BeerEdit, BeerCreate, BeerlistShow } from "./beerList";
import { UserCreate, UserShow, UserEdit, UserList } from "./userList";

import dataProvider from "./dataProvider";
import CustList from './components/CustList'
import BankList from './components/BankList'
import TxLogList from './components/TxLogList'
import TxStatList from './components/TxStatList'
// const dataProvider = jsonServerProvider("http://localhost:4000/admin");
console.log("::::::::dataProvider::::::", dataProvider);
const App = () => (
  <Admin
    dataProvider={dataProvider}
  // authProvider={authProvider}
  // dashboard={Dashboard}
  >
    <Resource name="Customer" list={CustList} />
    <Resource name="Bank" list={BankList} />
    <Resource name="Log" list={TxLogList} />
    <Resource name="Stat" list={TxStatList} />
    {/* <Resource
      name="beerlist"
      list={BeerList}
      // show={BeerlistShow}
      create={BeerCreate}
      edit={BeerEdit}
      icon={AssignmentIcon}
    />
    <Resource name="styles" />
    <Resource name="companies" />
    <Resource name="countries" />
    <Resource
      name="userlist"
      show={ShowGuesser}
      list={UserList}
      create={UserCreate}
      edit={UserEdit}
      icon={UserIcon}
    />
    <Resource name="report" list={ReportList} icon={ContactMailIcon} /> */}
    {/* <Resource
      name="posts"
      list={PostList}
      edit={PostEdit}
      create={PostCreate}
      icon={PostIcon}
    />
    <Resource name="users" list={UserList} icon={UserIcon} /> */}

  </Admin>
);

export default App;
