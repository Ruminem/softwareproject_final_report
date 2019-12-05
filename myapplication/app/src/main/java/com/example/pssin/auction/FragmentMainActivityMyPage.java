package com.example.pssin.auction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentMainActivityMyPage extends Fragment {
    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "memberClassification";

    // TODO: Rename and change types of parameters
    private TextView idText;
    private TextView one_to_one_inquiry;
    private ImageView mypage_point;
    private ImageView mypage_wishlist;
    private ImageView mypage_bid_history;
    private ImageView mypage_bid_management;
    private TextView mypage_notice;
    private TextView mypage_event;
    private TextView mypage_adversting;
    private TextView mypage_cusomersafety;
    private TextView mypage_preferences;
    private String id;
    private String memberClassification;

    MainActivity activity;

    public FragmentMainActivityMyPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMainActivityMyPage.
     */
    // TODO: Rename and change types and number of parameters
    static FragmentMainActivityMyPage newInstance(String id, String memberClassification) {
        FragmentMainActivityMyPage fragment = new FragmentMainActivityMyPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, memberClassification);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id                      = getArguments().getString(ARG_PARAM1);
            memberClassification    = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //프래그먼트 메인을 인플레이트해주고 컨테이너에 붙여달라는 뜻임
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_my_page , container, false);

        getObject(rootView);
        idText.setText(id);

        onClickEvent();


        return rootView;
    }

    private void getObject(ViewGroup rootView) {
        idText                  = rootView.findViewById(R.id.idText);
        one_to_one_inquiry      = rootView.findViewById(R.id.one_to_one_inquiry);
        mypage_point            = rootView.findViewById(R.id.mypage_point);
        mypage_wishlist         = rootView.findViewById(R.id.mypage_wishlist);
        mypage_bid_history      = rootView.findViewById(R.id.mypage_bid_history);
        mypage_bid_management   = rootView.findViewById(R.id.mypage_bid_management);
        mypage_notice           = rootView.findViewById(R.id.mypage_notice);
        mypage_event            = rootView.findViewById(R.id.mypage_event);
        mypage_adversting       = rootView.findViewById(R.id.mypage_adversting);
        mypage_cusomersafety    = rootView.findViewById(R.id.mypage_cusomersafety);
        mypage_preferences      = rootView.findViewById(R.id.mypage_preferences);
    }

    private void onClickEvent() {
        idText.setOnClickListener(v -> {
            Intent intent = new Intent(activity, PersonalInformationActivity.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", memberClassification);
            activity.startActivity(intent);
            
        });

        one_to_one_inquiry.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MyPageSendMailActivity.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", memberClassification);
            activity.startActivity(intent);
            
        });

        mypage_point.setOnClickListener(v -> {

            Intent intent = new Intent(activity, Mypage_Point.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", memberClassification);
            activity.startActivity(intent);
        });

        mypage_wishlist.setOnClickListener(v -> {
            Intent intent = new Intent(activity, Mypage_Wishlist.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", memberClassification);
            activity.startActivity(intent);
        });

        mypage_bid_history.setOnClickListener(v -> {
            Intent intent = new Intent(activity, Mypage_Bid_History.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", memberClassification);
            activity.startActivity(intent);
        });

        mypage_bid_management.setOnClickListener(v -> {
            Intent intent = new Intent(activity, Mypage_Bid_Management.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", memberClassification);
            activity.startActivity(intent);
        });

        mypage_notice.setOnClickListener(v -> {
            Intent intent = new Intent(activity, Mypage_Notice.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", memberClassification);
            activity.startActivity(intent);
        });

        mypage_event.setOnClickListener(v -> {
            Intent intent = new Intent(activity, Mypage_Evnet.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", memberClassification);
            activity.startActivity(intent);
        });

        mypage_adversting.setOnClickListener(v -> {
            Intent intent = new Intent(activity, Mypage_Adversting.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", memberClassification);
            activity.startActivity(intent);
        });

        mypage_cusomersafety.setOnClickListener(v -> {
            Intent intent = new Intent(activity, Mypage_Cusomer_Safety.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", memberClassification);
            activity.startActivity(intent);
        });

        mypage_preferences.setOnClickListener(v -> {
            Intent intent = new Intent(activity, Mypage_Preferences.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", memberClassification);
            activity.startActivity(intent);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }
}
