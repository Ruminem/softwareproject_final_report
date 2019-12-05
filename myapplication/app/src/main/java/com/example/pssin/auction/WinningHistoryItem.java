package com.example.pssin.auction;

public class WinningHistoryItem {
    String bulletinNumber;  // 게시글 번호
    String mycontent;       // 나의 게시글 내용
    String startPrice;      // 경매시작가
    String currentPrice;    // 낙찰가격
    String mylatitude;      // 나의 위도
    String mylongitude;     // 나의 경도
    String yourid;          // 낙찰대상 id
    String youraddress;     // 낙찰대상 위치
    String yourlatitude;    // 낙찰대상 위도
    String yourlongitude;   // 낙찰대상 경도
    String yourcomment;     // 낙찰대상 게시글내용
    String yourphoneNumber; // 낙찰대상 전화번호
    String companyName;     // 낙찰회사이름
    public String getBulletinNumber() { return bulletinNumber; }
    public void setBulletinNumber(String bulletinNumber) { this.bulletinNumber = bulletinNumber; }

    public String getmycontent() { return mycontent; }
    public void setmycontent(String imycontent) { this.mycontent = mycontent; }

    public String getstartPrice() { return startPrice; }
    public void setstartPrice(String startPrice) { this.startPrice = startPrice; }

    public String getcurrentPrice() { return currentPrice; }
    public void setcurrentPrice(String currentPrice) { this.currentPrice = currentPrice; }

    public String getmylatitude() { return mylatitude; }
    public void setmylatitude(String mylatitude) { this.mylatitude = mylatitude; }

    public String getmylongitude() { return mylongitude; }
    public void setmylongitude(String mylongitude) { this.mylongitude = mylongitude; }

    public String getyourid() { return yourid; }
    public void setyourid(String yourid) { this.yourid = yourid; }

    public String getyouraddress() { return youraddress; }
    public void setyouraddress(String youraddress) { this.youraddress = youraddress; }

    public String getyourlatitude() { return yourlatitude; }
    public void setyourlatitude(String yourlatitude) { this.yourlatitude = yourlatitude; }

    public String getyourlongitude() { return yourlongitude; }
    public void setyourlongitude(String yourlongitude) { this.yourlongitude = yourlongitude; }

    public String getyourcomment() { return yourcomment; }
    public void setyourcomment(String yourcomment) { this.yourcomment = yourcomment; }

    public String getyourphoneNumber() { return yourphoneNumber; }
    public void setyourphoneNumber(String yourphoneNumber) { this.yourphoneNumber = yourphoneNumber; }

    public String getcompanyName() { return companyName; }
    public void setcompanyName(String companyName) { this.companyName = companyName; }

    public WinningHistoryItem(String bulletinNumber, String mycontent, String startPrice, String currentPrice, String mylatitude, String mylongitude, String yourid, String youraddress, String yourlatitude, String yourlongitude, String yourcomment, String yourphoneNumber, String companyName) {
        this.bulletinNumber = bulletinNumber;
        this.mycontent = mycontent;
        this.startPrice = startPrice;
        this.currentPrice = currentPrice;
        this.mylatitude = mylatitude;
        this.mylongitude = mylongitude;
        this.yourid = yourid;
        this.youraddress = youraddress;
        this.yourlatitude = yourlatitude;
        this.yourlongitude = yourlongitude;
        this.yourcomment = yourcomment;
        this.yourphoneNumber = yourphoneNumber;
        this.companyName = companyName;
    }
}
