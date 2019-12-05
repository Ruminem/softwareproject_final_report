package com.example.pssin.auction;

public class BulletinListViewItem {
    String bulletinNumber;  // 게시글 번호
    String bulletinID;      // 게시글 작성자 아이디
    String content;         // 내용
    String uploadTime;      // 게시글 작성시간
    String timeLimit;       // 입찰 제한시간
    String countdown;       // 남은 입찰시간
    String startPrice;      // 입찰시작가
    String currentPrice;    // 현재 입찰가
    String latitude;        // 위도
    String longitude;       // 경도
    String address;         // 지번 주소

    public String getBulletinNumber() { return bulletinNumber; }

    public void setBulletinNumber(String bulletinNumber) { this.bulletinNumber = bulletinNumber; }

    public String getbulletinID()                           { return bulletinID;                    }

    public void setbulletinID(String id)                    { this.bulletinID = id;                 }

    public String getContent()                              { return content;                       }

    public void setContent(String content)                  { this.content = content;               }

    public String getUploadTime()                           { return uploadTime;                    }

    public void setUploadTime(String uploadTime)            { this.uploadTime = uploadTime;         }

    public String getTimeLimit()                            { return timeLimit;                     }

    public void setTimeLimit(String timeLimit)              { this.timeLimit = timeLimit;           }

    public String getCountdown()                            { return countdown;                     }

    public void setCountdown(String countdown)              { this.countdown = countdown;           }

    public String getStartPrice()                           { return startPrice;                    }

    public void setStartPrice(String startPrice)            { this.startPrice = startPrice;         }

    public String getCurrentPrice()                         { return currentPrice;                  }

    public void setCurrentPrice(String currentPrice)        { this.currentPrice = currentPrice;     }

    public String getLatitude()                             { return latitude;                      }

    public void setLatitude(String latitude)                { this.latitude = latitude;             }

    public String getLongitude()                            { return longitude;                     }

    public void setLongitude(String longitude)              { this.longitude = longitude;           }

    public String getAddress()                              { return address;                       }

    public void setAddress(String address)                  { this.address = address;               }

    public BulletinListViewItem(String bulletinNumber, String bulletinID, String content, String uploadTime, String timeLimit, String countdown,
            String startPrice, String currentPrice, String latitude, String longitude, String address) {
        this.bulletinNumber = bulletinNumber;
        this.bulletinID = bulletinID;
        this.content = content;
        this.uploadTime = uploadTime;
        this.timeLimit = timeLimit;
        this.countdown = countdown;
        this.startPrice = startPrice;
        this.currentPrice = currentPrice;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
