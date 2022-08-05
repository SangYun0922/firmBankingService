package com.inspien.fb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatementRequest {

/**
 * {
 * "org_code":"10000262","bank_code":"081",
 * "natv_tr_no":"090001","send_dt":"20220602","send_tm":"133243",
 * "account":"52212601","scf_dt":"20201222","scf_tm":"150550",
 * "rnd_gb_code":"21","scf_code":"30","amount":"5000","obc_amount":"0",
 * "after_sign":"-","after_amount":"10000","cms_code":"","can_tr_no":"000000","can_ogn_dt":"00000000",
 * "account_cntn":"XXXXXX","mid":null,
 * "cpt_rtu_org_code":"100000XX","cpt_rtu_ogn_tr_dt":"20220124",
 * "cpt_rtu_ogn_tr_no:":"000024","cpt_rtu_ogn_orr_no":"ORRNO20220124142358912582",
 * "rv_nm":"XXXXX","cpt_rtu_yn":"Y","cpt_rq_dt":"20201223","cpt_rq_no": "FR5829495665"
 * }
 */

    private String org_code; //더즌기관코드
    private String bank_code; //은행코드
    private String natv_tr_no; //거래고유번호
    private String send_dt; //전송일자
    private String send_tm; //전송시간
    private String account; //계좌번호, 명세 발생 계좌번호
    private String scf_dt; //명세거래일자, 명세발생일자
    private String scf_tm; //명세거래시간, 명세발생시간
    private String rnd_gb_code; //입출구분코드, 11.입금,21.출금 31.입금취소,32.출금취소,99.기타
    private String scf_code; //11.입금,21.출금 31.입금취소,32.출금취소,99.기타,20.입금,21.추심입금,30.지급,31.부도지급 40.입금이자,41.출금이자,81.입금압축기장 82.출금압축기장,61.기존계좌내역 계좌등록시,62.기존계좌내역 계좌해지시
    private long amount; //명세거래금액
    private long obc_amount; //타점권거래금액, 어음, 타점금액 이후 거래 가능
    private String after_sign; //거래후잔액부호,+/-
    private long after_amount; //거래후잔액, 명세 발생후 모계좌 잔액
    private String cms_code; //CMS 코드
    private String can_tr_no; //취소거래번호,취소처리된 원거래번호
    private String can_ogn_dt; //취소원거래일자,취소시 원거래 발생일자
    private String account_cntn; //적요
    private String mid; //기관 ID
    private String cpt_rtu_org_code; //자금반환기관코드,자금반환명세일경우 원거래 기관코드
    private String cpt_rtu_ogn_tr_dt; //자금반환원거래일자,자금반환명세일경우 송금 원거래 일자
    private String cpt_rtu_ogn_tr_no; //자금반환원거래번호, 자금반환명세일경우 송금 원거래 은행거래번호
    private String cpt_rtu_ogn_orr_no; //자금반환원주문번호, 자금반환명세일경우 송금 원거래주문번호
    private String rv_nm; //입금계좌성명, 자금반환명세일경우 입금계좌성명 신한은행 사용
    private String cpt_rtu_yn; //자금반환여부, 자금반환명세일경우 반환여부 신한은행 사용 Y/N
    private String cpt_rq_dt; //자금반환신청일자, 자금반환명세일경우 자금반환 신청일자
    private String cpt_rq_no; //자금반환신청번호, 자금반환명세일경우 자금반환 신청번호
}