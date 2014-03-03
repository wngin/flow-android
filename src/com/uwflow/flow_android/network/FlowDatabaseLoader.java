package com.uwflow.flow_android.network;

import android.content.Context;
import android.os.AsyncTask;
import com.j256.ormlite.dao.Dao;
import com.uwflow.flow_android.MainFlowActivity;
import com.uwflow.flow_android.dao.FlowDatabaseHelper;
import com.uwflow.flow_android.db_object.*;
import com.uwflow.flow_android.util.JsonToDbUtil;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This method fetches data from the network and populates the database
 */
public class FlowDatabaseLoader {
    protected FlowDatabaseHelper flowDatabaseHelper;
    protected Context context;

    public FlowDatabaseLoader(Context context, FlowDatabaseHelper flowDatabaseHelper) {
        this.context = context;
        this.flowDatabaseHelper = flowDatabaseHelper;
    }

    public void loadOrReloadProfileData(ResultCollectorCallback callback) {
        FlowResultCollector resultCollector = new FlowResultCollector(5, callback);
        reloadUserMe(0, resultCollector);
        reloadProfileCourses(1, resultCollector);
        reloadProfileExams(2, resultCollector);
        reloadProfileFriends(3, resultCollector);
        reloadProfileSchedule(4, resultCollector);
    }

    public void reloadUserMe(final int index, final FlowResultCollector flowResultCollector){
        FlowApiRequests.searchUser(new FlowApiRequestCallbackAdapter() {
            @Override
            public void onSuccess(JSONObject response) {
                new AsyncTask<JSONObject, Void, Object>(){

                    @Override
                    protected Void doInBackground(JSONObject... jsonObjects) {
                        try {
                            Dao<User, String> userDao = flowDatabaseHelper.getUserDao();
                            User user = JsonToDbUtil.getUserMe(jsonObjects[0]);
                            if (user != null)
                                userDao.createOrUpdate(user);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object obj) {
                        handleCallback(index, flowResultCollector);
                    }
                }.execute(response);
            }

            @Override
            public void onFailure(String error) {
                handleCallback(index, flowResultCollector);
            }
        });
    }

    public void reloadProfileFriends(final int index, final FlowResultCollector flowResultCollector){
        FlowApiRequests.searchUserFriends(new FlowApiRequestCallbackAdapter() {
            @Override
            public void onSuccess(JSONObject response) {
                new AsyncTask<JSONObject, Void, Object>(){
                    @Override
                    protected Void doInBackground(JSONObject... jsonObjects) {
                        UserFriends userFriends = JsonToDbUtil.getUserFriends(jsonObjects[0]);
                        try {
                            Dao<User, String> userDao = flowDatabaseHelper.getUserDao();
                            for (User u : userFriends.getFriends()) {
                                userDao.createOrUpdate(u);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object obj) {
                        handleCallback(index, flowResultCollector);
                    }
                }.execute(response);
            }

            @Override
            public void onFailure(String error) {
                handleCallback(index, flowResultCollector);
            }
        });
    }

    public void reloadProfileSchedule(final int index, final FlowResultCollector flowResultCollector){
        FlowApiRequests.searchUserSchedule(new FlowApiRequestCallbackAdapter() {
            @Override
            public void onSuccess(JSONObject response) {
                new AsyncTask<JSONObject, Void, Object>(){
                    @Override
                    protected Void doInBackground(JSONObject... jsonObjects) {
                        ScheduleCourses scheduleCourses = JsonToDbUtil.getUserSchedule(jsonObjects[0]);
                        try {
                            Dao<ScheduleCourse, String> userCourseSchedule = flowDatabaseHelper.getUserScheduleCourseDao();
                            for (ScheduleCourse sc : scheduleCourses.getScheduleCourses()) {
                                userCourseSchedule.createOrUpdate(sc);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object obj) {
                        handleCallback(index, flowResultCollector);
                    }
                }.execute(response);
            }

            @Override
            public void onFailure(String error) {
                handleCallback(index, flowResultCollector);
            }
        });
    }

    public void reloadProfileExams(final int index, final FlowResultCollector flowResultCollector){
        FlowApiRequests.searchUserExams(new FlowApiRequestCallbackAdapter() {

            @Override
            public void onSuccess(JSONObject response) {
                new AsyncTask<JSONObject, Void, Object>(){
                    @Override
                    protected Void doInBackground(JSONObject... jsonObjects) {
                        Exams userExams = JsonToDbUtil.getUserExams(jsonObjects[0]);
                        try {
                            Dao<Exam, Integer> userExamDao = flowDatabaseHelper.getUserExamDao();
                            for (Exam exam : userExams.getExams()) {
                                userExamDao.createOrUpdate(exam);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object obj) {
                        handleCallback(index, flowResultCollector);
                    }
                }.execute(response);
            }

            @Override
            public void onFailure(String error) {
                handleCallback(index, flowResultCollector);
            }
        });
    }

    public void reloadProfileCourses(final int index, final FlowResultCollector flowResultCollector){
        FlowApiRequests.searchUserCourses(new FlowApiRequestCallbackAdapter() {
            @Override
            public void onSuccess(JSONObject response) {
                new AsyncTask<JSONObject, Void, Object>(){
                    @Override
                    protected Void doInBackground(JSONObject... jsonObjects) {
                        UserCourseDetail userCourses = JsonToDbUtil.getUserCourseDetail(jsonObjects[0]);
                        try {
                            Dao<Course, String> courseDao = flowDatabaseHelper.getUserCourseDao();
                            for (Course c : userCourses.getCourses()) {
                                courseDao.createOrUpdate(c);

                            }
                            Dao<UserCourse, String> userCourseExtraDao = flowDatabaseHelper.getUserCourseExtraDao();
                            for (UserCourse c : userCourses.getUserCourses()) {
                                userCourseExtraDao.createOrUpdate(c);

                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object obj) {
                        handleCallback(index, flowResultCollector);
                    }
                }.execute(response);
            }

            @Override
            public void onFailure(String error) {
                handleCallback(index, flowResultCollector);
            }
        });
    }

    protected void handleCallback(int index, FlowResultCollector flowResultCollector){
        if (flowResultCollector != null){
            flowResultCollector.setState(index, true);
        }
    }
}