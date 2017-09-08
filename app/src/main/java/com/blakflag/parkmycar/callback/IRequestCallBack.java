package com.blakflag.parkmycar.callback;

import com.blakflag.parkmycar.model.ParkingRequst;

/**
 * Created by Amit on 8/25/2017.
 */

public interface IRequestCallBack {
    void requestStatus(ParkingRequst parkingRequst,boolean isRequstAccepted);

}
