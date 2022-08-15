import React from 'react'
import {
    List,
    Datagrid,
    TextField,
    ShowButton,
    Show,
    SimpleShowLayout,
} from 'react-admin'

export const TxLogShow = (props) => (
    <Show title="TxLog View"  {...props}>
        <SimpleShowLayout>
            <TextField source="TxDate" />
            <TextField source="id" />
            <TextField source="CustNm" />
            <TextField source="OrgCd" />
            <TextField source="MsgId" />
            <TextField source="TelegramNo" />
            <TextField source="TxType" />
            <TextField source="BankNm" />
            <TextField source="Size" />
            <TextField source="RoundTrip" />
            <TextField source="StmtCnt" />
            <TextField source="Status" />
            <TextField source="StartDT" />
            <TextField source="EndDT" />
            <TextField source="NatvTrNo" />
            <TextField source="ErrCode" />
            <TextField source="ErrMsg" />
            <TextField source="EncData" />
        </SimpleShowLayout>
    </Show>
);

export const TxLogList = (props) => (
    <List {...props}>
        <Datagrid>
            <TextField source="TxDate" />
            <TextField source="CustNm" />
            <TextField source="OrgCd" />
            <TextField source="MsgId" />
            <TextField source="TelegramNo" />
            <TextField source="TxType" />
            <TextField source="BankNm" />
            <TextField source="StmtCnt" />
            <TextField source="Status" />
            <TextField source="StartDT" />
            <TextField source="EndDT" />
            <ShowButton />
        </Datagrid>
    </List>
);


