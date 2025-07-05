package com.effanville.curvedata;

/** 
 * The type of the curve bucket, determining the type of the 
 * session where the curve volume is attributed to.
 */
public enum BucketType {
    UNKNOWN, MARKET_CLOSED, OPEN_AUCTION, CONT_TRADING, INTRADAY_CLOSE, INTRADAY_AUCTION, CLOSE_AUCTION, POST_CLOSE;
}
