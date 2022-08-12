// in src/App.js
import * as React from "react";
import { Admin, Resource } from "react-admin";

import UserIcon from "@material-ui/icons/Group";
import AssignmentIcon from "@material-ui/icons/Assignment";
import ContactMailIcon from "@material-ui/icons/ContactMail";
import LocalBarIcon from "@material-ui/icons/LocalBar";
import CommentIcon from "@material-ui/icons/Comment";

import dataProvider from "./dataProvider";
import { CustList, CustCreate, CustEdit } from './components/CustList'
import { BankList, BankCreate, BankEdit } from './components/BankList'
import TxLogList from './components/TxLogList'
import TxStatList from './components/TxStatList'
import TxTraceList from "./components/TxTraceList";

const App = () => (

  <Admin dataProvider={dataProvider}>
    <Resource name="Customer" list={CustList} edit={CustEdit} create={CustCreate} icon={UserIcon} />
    <Resource name="Bank" list={BankList} edit={BankEdit} create={BankCreate} icon={ContactMailIcon} />
    <Resource name="Log" list={TxLogList} icon={AssignmentIcon} />
    <Resource name="Stat" list={TxStatList} icon={LocalBarIcon} />
    <Resource name="Trace" list={TxTraceList} icon={CommentIcon} />
  </Admin>
);

export default App;