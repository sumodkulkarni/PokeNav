package com.sumod.pokenav.fragments;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.sumod.pokenav.BuildConfig;
import com.sumod.pokenav.R;
import com.sumod.pokenav.adapter.NavigationDrawerAdapter;
import com.sumod.pokenav.model.Feedback;
import com.sumod.pokenav.model.NavDrawerItem;
import com.sumod.pokenav.utils.AlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class FragmentDrawer extends InjectableFragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private RecyclerView recyclerView;
    private ImageView imageView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;
    private Dialog feedbackDialog;

    @Inject AlertDialogBuilder alertDialogBuilder;

    static final String PLAY_STORE_URL = "market://details?id=" + BuildConfig.APPLICATION_ID;


    public FragmentDrawer() {

    }


    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }


    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();

        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            data.add(navItem);
        }
        return data;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // drawer labels
        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        imageView = (ImageView) layout.findViewById(R.id.drawer_imageView);

        if (ParseUser.getCurrentUser().get("avatar") != null) {
            Uri imageUri = Uri.parse((String) ParseUser.getCurrentUser().get("avatar"));
            Picasso.with(getContext()).load(imageUri).into(imageView);
//            imageView.setImageURI(imageUri);
            Log.d(TAG, "Profile pic appliedll");
        } else Log.d(TAG, "Profile pic null");

        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }


            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }


            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }


    public void showFeedback() {
        showFeedbackDialog(getLayoutInflater(null).inflate(R.layout.dialog_feedback, mDrawerLayout, false));
        mDrawerLayout.closeDrawers();
    }


    /**
     * Function to handle the feedback form. It shows the dialog, validates it and send a request
     * to the Parse server.
     *
     * @param dialogView
     */
    private void showFeedbackDialog(View dialogView) {
        final TextView feedbackNote = (TextView) dialogView.findViewById(R.id.feedback_note);
        final TextInputLayout bodyLayout = (TextInputLayout)
                dialogView.findViewById(R.id.textinput_body);
        final EditText body = (EditText) dialogView.findViewById(R.id.edittext_body);
        final View sendButton = dialogView.findViewById(R.id.feedback_button);

        feedbackNote.setText(R.string.feedback_note);
        feedbackDialog = alertDialogBuilder
                .init(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.button_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlayStore();
                feedbackDialog.dismiss();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bodyLayout.setErrorEnabled(false);
                if (body.length() == 0) {
                    bodyLayout.setError(getString(R.string.feedback_message_required));
                    return;
                }

                sendButton.setEnabled(false);

                Feedback feedback = new Feedback();
                feedback.setMessage(body.getText().toString());
                feedback.setSubmittedBy(ParseUser.getCurrentUser());
                feedback.setACL(ParseUser.getCurrentUser().getACL());
                feedback.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        boolean success = e == null;

                        Toast.makeText(getContext(),
                                success ? R.string.feedback_sent : R.string.feedback_failed,
                                Toast.LENGTH_SHORT)
                                .show();

                        if (feedbackDialog == null || !feedbackDialog.isShowing()) return;

                        if (success) feedbackDialog.dismiss();
                        else feedbackDialog.findViewById(R.id.feedback_button).setEnabled(true);
                    }
                });
            }
        });
        feedbackDialog.show();
    }


    public void openPlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        else intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        try {
            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.feedback_no_playstore, Toast.LENGTH_SHORT).show();
        }
    }


    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }


    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;


        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }


                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }


        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }


        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }


        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }
}