package com.akash.trivia.data;

import com.akash.trivia.controller.AppController;
import com.akash.trivia.model.Question;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Repositary {
    ArrayList<Question> questionArrayList = new ArrayList<>();
    String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    public List<Question> getQuestions(final AnswerListAsyncResponse callBack)
    {
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length() ; i++) {

                try {
                    Question question;
                    question = new Question(response.getJSONArray(i).get(0).toString(),
                            response.getJSONArray(i).getBoolean(1));
                    // Log.d("repo","start " + response.getJSONArray(i).get(0));
                    questionArrayList.add(question);
                    //Log.d("akash","start " + questionArrayList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } if (null != callBack) {
                callBack.processFinished(questionArrayList);
            }
        }, error -> {

        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        return questionArrayList;
    }

}
