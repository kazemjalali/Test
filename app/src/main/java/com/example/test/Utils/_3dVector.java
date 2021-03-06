package com.example.test.Utils;

public class _3dVector {
    public double x, y, z;

    public _3dVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void vectorAddition(_3dVector newVec) {
        this.x += newVec.x;
        this.y += newVec.y;
        this.z += newVec.z;
    }

    public _3dVector multiplyVectorByNum(double number) {
        return new _3dVector(this.x*number, this.y*number, this.z*number);
    }

    public double getSize() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }
}
