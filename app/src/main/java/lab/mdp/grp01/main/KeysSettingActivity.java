package lab.mdp.grp01.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static lab.mdp.grp01.main.Utils.*;

public class KeysSettingActivity extends DialogFragment {

    private static final String TAG = "KeySettingActivity";

    SharedPreferences spf;
    EditText settingExplore, settingRace, settingStop, settingUp, settingDown, settingLeft, settingRight, settingCmd1, settingCmd2, settingCmd3, settingCmd4, settingCmd5;
    Button btnSaveSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_list, container, false);

        settingExplore = (EditText) view.findViewById(R.id.settingExplore);
        settingRace = (EditText) view.findViewById(R.id.settingRace);
        settingStop = (EditText) view.findViewById(R.id.settingStop);
        settingUp = (EditText) view.findViewById(R.id.settingUp);
        settingDown = (EditText) view.findViewById(R.id.settingDown);
        settingLeft = (EditText) view.findViewById(R.id.settingLeft);
        settingRight = (EditText) view.findViewById(R.id.settingRight);
        settingCmd1 = (EditText) view.findViewById(R.id.settingCmd1);
        settingCmd2 = (EditText) view.findViewById(R.id.settingCmd2);
        settingCmd3 = (EditText) view.findViewById(R.id.settingCmd3);
        settingCmd4 = (EditText) view.findViewById(R.id.settingCmd4);
        settingCmd5 = (EditText) view.findViewById(R.id.settingCmd5);


        btnSaveSettings = (Button) view.findViewById(R.id.btnSaveSettings);

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getDialog().setTitle("Preferences");
        spf = getActivity().getSharedPreferences(PREF_DB, Context.MODE_PRIVATE);

        settingExplore.setText(spf.getString(SET_EXPLORE, SET_EXPLORE_DEFAULT));
        settingRace.setText(spf.getString(SET_RACE, SET_RACE_DEFAULT));
        settingStop.setText(spf.getString(SET_STOP, SET_STOP_DEFAULT));
        settingUp.setText(spf.getString(SET_UP, SET_UP_DEFAULT));
        settingDown.setText(spf.getString(SET_DOWN, SET_DOWN_DEFAULT));
        settingLeft.setText(spf.getString(SET_LEFT, SET_LEFT_DEFAULT));
        settingRight.setText(spf.getString(SET_RIGHT, SET_RIGHT_DEFAULT));
        settingCmd1.setText(spf.getString(SET_CMD1, SET_CMD1_DEFAULT));
        settingCmd2.setText(spf.getString(SET_CMD2, SET_CMD2_DEFAULT));
        settingCmd3.setText(spf.getString(SET_CMD3, SET_CMD3_DEFAULT));
        settingCmd4.setText(spf.getString(SET_CMD4, SET_CMD4_DEFAULT));
        settingCmd5.setText(spf.getString(SET_CMD5, SET_CMD5_DEFAULT));


        btnSaveSettings.setOnClickListener(saveSettings);
    }

    private View.OnClickListener saveSettings = new View.OnClickListener(){
        public void onClick(View v){
            savePrefs();
        }
    };

    private void savePrefs(){
        String ex = settingExplore.getText().toString();
        ex = ex.length() > 0 ? ex : SET_EXPLORE_DEFAULT;
        String ra = settingRace.getText().toString();
        ra = ra.length() > 0 ? ra : SET_RACE_DEFAULT;
        String so = settingStop.getText().toString();
        so = so.length() > 0 ? so : SET_STOP_DEFAULT;
        String up = settingUp.getText().toString();
        up = up.length() > 0 ? up : SET_UP_DEFAULT;
        String down = settingDown.getText().toString();
        down = down.length() > 0 ? down : SET_DOWN_DEFAULT;
        String le = settingLeft.getText().toString();
        le = le.length() > 0 ? le : SET_LEFT_DEFAULT;
        String ri = settingRight.getText().toString();
        ri = ri.length() > 0 ? ri : SET_RIGHT_DEFAULT;
        String c1 = settingCmd1.getText().toString();
        c1 = c1.length() > 0 ? c1 : SET_CMD1_DEFAULT;
        String c2 = settingCmd2.getText().toString();
        c2 = c2.length() > 0 ? c2 : SET_CMD2_DEFAULT;
        String c3 = settingCmd3.getText().toString();
        c3 = c3.length() > 0 ? c3 : SET_CMD3_DEFAULT;
        String c4 = settingCmd4.getText().toString();
        c4 = c4.length() > 0 ? c4 : SET_CMD4_DEFAULT;
        String c5 = settingCmd5.getText().toString();
        c5 = c5.length() > 0 ? c5 : SET_CMD5_DEFAULT;


        SharedPreferences.Editor editor = spf.edit();
        editor.putString(SET_EXPLORE, ex);
        editor.putString(SET_RACE, ra);
        editor.putString(SET_STOP, so);
        editor.putString(SET_UP, up);
        editor.putString(SET_DOWN, down);
        editor.putString(SET_LEFT, le);
        editor.putString(SET_RIGHT, ri);
        editor.putString(SET_CMD1, c1);
        editor.putString(SET_CMD2, c2);
        editor.putString(SET_CMD3, c3);
        editor.putString(SET_CMD4, c4);
        editor.putString(SET_CMD5, c5);
        editor.commit();

        Toast.makeText(getActivity(), "Preferences saved.", Toast.LENGTH_SHORT).show();
        getDialog().dismiss();
    }
}
