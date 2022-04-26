package com.razo.contacttracingapp.animation;

public class Mybounce implements android.view.animation.Interpolator
{
    private double mam = 2;
    private double mfra =10;

    public Mybounce (double amplitude, double frequency)
    {
        mam = amplitude;
        mfra = frequency;
    }

    public float getInterpolation(float time)
    {
        return (float) (-1 * Math.pow(Math.E, -time/mam) * Math.cos(mfra * time) + 1);
    }
}