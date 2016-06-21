// IMyService.aidl
package com.example.kashif.lab3;

// Declare any non-default types here with import statements

interface IMyService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    double getLatitude();
    double getLongitude();
    double getDistance();
    double getSpeed();
}
