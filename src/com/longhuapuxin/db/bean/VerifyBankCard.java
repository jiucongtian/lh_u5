package com.longhuapuxin.db.bean;

/**
 * Created by ZH on 2016/1/14.
 * Email zh@longhuapuxin.com
 */
public class VerifyBankCard  {
    public String mFirstName;
    public String mLastName;
    public String mIdCard;
    public String mBankCard;


    public int mVerifiedTimes;
    public Double mAmount;
    public String mId;
    public String BankName;
    public String BankBranch;
    public String Province;
    public String City;
    public String toString(){
        return "mFirstName:"+mFirstName+"\n"
                +"mLastName:"+mLastName+"\n"
                +"mIdCard:"+mIdCard+"\n"
                +"mBankCard:"+mBankCard+"\n"
                +"mVerifiedTimes:"+String.valueOf(mVerifiedTimes)+"\n"
                +"mId:"+mId+"\n"
                +"BankName:"+BankName+"\n"
                +"BankBranch:"+BankBranch+"\n"
                +"Province:"+Province+"\n"
                +"City:"+City+"\n"
                +"mAmount:"+String.valueOf(mAmount)+"\n";


    }
    public  static  VerifyBankCard fromString(String text){
        VerifyBankCard card=new VerifyBankCard();
        String[] lines=text.split("\n");
        for (String line :lines){
            String[] parts=line.split(":");
            if(parts.length<2) continue;

            if(parts[0].equals("mFirstName")){
                card.mFirstName=parts[1];
            }
            else if(parts[0].equals("mLastName")){
                card.mLastName=parts[1];
            }
            else if(parts[0].equals("mIdCard")){
                card.mIdCard=parts[1];
            }
            else if(parts[0].equals("mBankCard")){
                card.mBankCard=parts[1];
            }
            else if(parts[0].equals("mVerifiedTimes")){
                card.mVerifiedTimes= Integer.valueOf( parts[1]);
            }
            else if(parts[0].equals("mId")){
                card.mId=parts[1];
            }
            else if(parts[0].equals("BankName")){
                card.BankName=parts[1];
            }
            else if(parts[0].equals("BankBranch")){
                card.BankBranch=parts[1];
            }
            else if(parts[0].equals("Province")){
                card.Province=parts[1];
            }
            else if(parts[0].equals("City")){
                card.City=parts[1];
            }
            else if(parts[0].equals("mAmount")){
                card.mAmount=Double.valueOf( parts[1]);
            }
        }
        return card;
    }
}
