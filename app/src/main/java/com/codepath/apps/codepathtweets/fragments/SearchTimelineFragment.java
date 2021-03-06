package com.codepath.apps.codepathtweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.codepathtweets.TwitterApplication;
import com.codepath.apps.codepathtweets.TwitterClient;
import com.codepath.apps.codepathtweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by qiming on 8/9/2016.
 */
public class SearchTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    String mQuery;

    public SearchTimelineFragment() {
        this.setArguments(new Bundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();    // singleton client

    }

    @Override
    protected void populateTimeline(long maxId) {
        populateTimeline(maxId, mQuery);
    }

    private void populateTimeline(long maxId, String query) {
        client.lock();
        client.getSearchTimeline(maxId, query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                client.unlock();
                Toast.makeText(getActivity(), "JSON success", Toast.LENGTH_SHORT).show();
                Log.d("TWITTER SEARCH", jsonObject.toString());
                List<Tweet> tweets = null;
                try {
                    tweets = Tweet.fromJson(jsonObject.getJSONArray("statuses"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addAll(tweets);
//                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                client.unlock();
                Toast.makeText(getActivity(), "JSON failure", Toast.LENGTH_SHORT).show();
                Log.d("TWITTER SEARCH", errorResponse.toString());
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQuery = getArguments().getString("query");
        populateTimeline(0);
    }
}
