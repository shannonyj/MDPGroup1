package lab.mdp.grp01.main;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import static lab.mdp.grp01.main.Utils.*;

public class RobotSettingActivity extends DialogFragment {

    private static final String TAG = "RobotSettingActivity";

    private EditText robotX, robotY;
    private RadioGroup robotHead;
    private Button btnRobotSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.robot_init, container, false);

        robotX = (EditText) view.findViewById(R.id.robotInitX);
        robotY = (EditText) view.findViewById(R.id.robotInitY);
        robotHead = (RadioGroup) view.findViewById(R.id.robotInitHead);
        btnRobotSave = (Button) view.findViewById(R.id.robotInitSave);

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getDialog().setTitle("Robot Initialization");
        btnRobotSave.setOnClickListener(initRobot);
    }

    private View.OnClickListener initRobot = new View.OnClickListener(){
        public void onClick(View v){
            String x = robotX.getText().toString();
            String y = robotY.getText().toString();
            checkPos(x, y);
        }
    };

    private void checkPos(String x, String y){
        int posX = 1, posY = 1, headX, headY;
        String sHead;

        Log.d(TAG, "X: " + x);
        if(x != null && (x.length()>0)) {
            posX = Integer.valueOf(x);
            if (posX < 1 || posX > 20)
                posX = 1;
        }
        if(y != null && (y.length()>0)) {
            posY = Integer.valueOf(y);
            if (posY < 1 || posY > 20)
                posY = 1;
        }
        switch(robotHead.getCheckedRadioButtonId()){
            case R.id.robotUp:
                headX = posX;
                headY = posY - 1;
                sHead = "up";
                break;
            case R.id.robotDown:
                headX = posX;
                headY = posY + 1;
                sHead = "down";
                break;
            case R.id.robotLeft:
                headX = posX -1;
                headY = posY;
                sHead = "left";
                break;
            case R.id.robotRight:
                headX = posX + 1;
                headY = posY;
                sHead = "right";
                break;
            default:
                headX = posX;
                headY = posY + 1;
                sHead = "";
                break;
        }

        String mapInfo = "GRID " + MAP_ROWS + " " + MAP_COLS + " " + posX + " " + posY + " " + headX + " " + headY + defaultMap.substring(18);
        ((MainActivity)getActivity()).updateMap(mapInfo, false);
        ((MainActivity)getActivity()).SendStartPos(posX, posY, sHead);

        Toast.makeText(getActivity(), "Robot set: "+mapInfo, Toast.LENGTH_SHORT).show();
        getDialog().dismiss();
    }
}
