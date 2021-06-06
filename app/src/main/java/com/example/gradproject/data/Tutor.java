package com.example.gradproject.data;

public class Tutor extends Account implements Comparable
{

    private String tutorDescription;
    private int numberOfRatings;
    private float tutorExplanation;
    private float tutorRespectingTime;
    private float tutorAttitude;
    private float generalTutorRating;

    public Tutor()
    {

    }

    public Tutor(String accountId, String fullName, String email, String phoneNumber, String tutorDescription, int numberOfRatings, float tutorExplanation, float tutorRespectingTime, float tutorAttitude, float generalTutorRating) {
        super(accountId, fullName, email, phoneNumber);
        this.tutorDescription = tutorDescription;
        this.numberOfRatings = numberOfRatings;
        this.tutorExplanation = tutorExplanation;
        this.tutorRespectingTime = tutorRespectingTime;
        this.tutorAttitude = tutorAttitude;
        this.generalTutorRating = generalTutorRating;
    }

    public String getTutorDescription() {
        return tutorDescription;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public float getTutorExplanation() {
        return tutorExplanation;
    }

    public float getTutorRespectingTime() {
        return tutorRespectingTime;
    }

    public float getTutorAttitude() {
        return tutorAttitude;
    }

    public float getGeneralTutorRating() {
        return generalTutorRating;
    }

    @Override
    public int compareTo(Object compareTutor)
    {
        float compareRate =((Tutor)compareTutor).getGeneralTutorRating();
        /* For Ascending order*/
        //return this.generalTutorRating-compareRate;

        /* For Descending order do like this */
        //return compareRate-this.generalTutorRating;

        return Float.compare(compareRate, this.generalTutorRating);
    }
}
