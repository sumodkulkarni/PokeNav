package com.sumod.pokenav.model;


import com.parse.ParseClassName;
import com.parse.ParseUser;


@ParseClassName(Feedback.PARSE_CLASSNAME)
public class Feedback extends BaseModel {
    public static final String PARSE_CLASSNAME = "Feedbacks";
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_SUBMITTEDBY = "submittedBy";


    public void setMessage(String message) {
        put(FIELD_MESSAGE, message);
    }


    public void setSubmittedBy(ParseUser user) {
        put(FIELD_SUBMITTEDBY, user);
    }
}
