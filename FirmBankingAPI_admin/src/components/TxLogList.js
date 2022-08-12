import React from 'react'
import {
    List,
    Datagrid,
    TextField,
} from 'react-admin'

export default function TxLogList(props) {
    return (
        <List {...props}>
            <Datagrid>
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
            </Datagrid>
        </List>
    )
}
