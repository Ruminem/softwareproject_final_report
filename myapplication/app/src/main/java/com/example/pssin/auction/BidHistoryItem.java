package com.example.pssin.auction;

public class BidHistoryItem {
    String bulletinNumber;  // 게시글 번호
    String id;              // 입찰자 아이디
    String bidPrice;        // 입찰가
    String address;         // 입찰자 주소
    String latitude;        // 위도
    String longitude;       // 경도
    String comment;         // 댓글
    String phoneNumber;     // 휴대폰 번호
    String companyName;     // 회사 이름

    public String getBulletinNumber() { return bulletinNumber; }
    public void setBulletinNumber(String bulletinNumber) { this.bulletinNumber = bulletinNumber; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBidPrice() { return bidPrice; }
    public void setBidPrice(String bidPrice) { this.bidPrice = bidPrice; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public BidHistoryItem(String bulletinNumber, String id, String bidPrice, String address, String latitude, String longitude,
            String comment, String phoneNumber, String companyName) {
        this.bulletinNumber = bulletinNumber;
        this.id             = id;
        this.bidPrice       = bidPrice;
        this.address        = address;
        this.latitude       = latitude;
        this.longitude      = longitude;
        this.comment        = comment;
        this.phoneNumber    = phoneNumber;
        this.companyName    = companyName;
    }
}
