package com.example.gradproject.data;

public class TutorRating
{
    private  int ratingId;
    private int tutorIdRating;
    private  int numberOfRatings;
    private float tutorExplanation;
    private float tutorRespectingTime;
    private float tutorAttitude;
    private float generalTutorRating;

    public TutorRating() {
    }

    public TutorRating(int ratingId, int tutorIdRating, int numberOfRatings, float tutorExplanation, float tutorRespectingTime, float tutorAttitude, float generalTutorRating) {
        this.ratingId = ratingId;
        this.tutorIdRating = tutorIdRating;
        this.numberOfRatings = numberOfRatings;
        this.tutorExplanation = tutorExplanation;
        this.tutorRespectingTime = tutorRespectingTime;
        this.tutorAttitude = tutorAttitude;
        this.generalTutorRating = generalTutorRating;
    }

    public float getGeneralTutorRating() {
        return generalTutorRating;
    }

    public int getRatingId() {
        return ratingId;
    }

    public int getTutorIdRating() {
        return tutorIdRating;
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
}
