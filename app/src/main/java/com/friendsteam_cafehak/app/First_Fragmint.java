package com.friendsteam_cafehak.app;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Mohamed-A.Radwan on 08/10/2016 and 11:41 PM.
 */

public class First_Fragmint extends Fragment {
    View myView;
    TextView courseName;
    Button bSave;
    EditText changeCourse;
    DB_Helper db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.first_layout, container, false);
        courseName = (TextView) myView.findViewById(R.id.courseName);
        bSave = (Button) myView.findViewById(R.id.save);
        changeCourse = (EditText) myView.findViewById(R.id.changeCourse);
        while (!isAdded()) {
        }
        db = new DB_Helper(getActivity().getApplicationContext());
        final Bundle bundle = getArguments();
        final int index = bundle.getInt("Index");
        courseName.setText(MainScreen.List[index]);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = changeCourse.getText().toString();
                if (text.length() != 0) {
                    String temp = MainScreen.List[index];
                    MainScreen.List[index] = text;
                    courseName.setText(text);
                    boolean s = db.updateCouese(text, bundle.getString("id"), index + 1);
                    if (s)
                        Toast.makeText(getActivity(), temp + " Cource changed to " + text, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity(), temp + "Error happened ", Toast.LENGTH_SHORT).show();
                } else {
                    changeCourse.setError("Cant be Empty");
                }
            }
        });

        return myView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        db = new DB_Helper(context);
    }


}
