package pjs.apps.blackjack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class SubActivity extends Activity  implements View.OnClickListener {

    private static final String TAG = "BlackJack";
    ImageButton start, end, menual, set, starShop;
    Intent intent;
    public Button btn_yes, btn_no, settingOn, settingOff, exit, buyMoney, buyRandom, menualClose;
    AlertDialog.Builder builder, starBuilder, menualBuilder;
    View view, starView, menualVeiw;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    AlertDialog dialog, starDialog, menualDialog;
    TextView settingStar;
    Random ran = new Random();

    int star;


    @Deprecated
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        intent = new Intent(this, MainActivity.class);
        start = findViewById(R.id.gameStart);
        start.setOnClickListener(this);
        end = findViewById(R.id.finish);
        end.setOnClickListener(this);
        menual = findViewById(R.id.menual);
        menual.setOnClickListener(this);
        set = findViewById(R.id.setting);
        set.setOnClickListener(this);
        starShop = findViewById(R.id.starShop);
        starShop.setOnClickListener(this);


        /***** 설정 사운드 온 오프 버튼 ******/
        builder = new AlertDialog.Builder(this);
        //builder.setTitle("insurance 에 배팅하겠습니까?");
        view = View.inflate(this, R.layout.setting_sound, null);
        builder.setView(view);
        settingOn = view.findViewById(R.id.settingOn);
        settingOn.setOnClickListener(this);
        settingOff = view.findViewById(R.id.settingOff);
        settingOff.setOnClickListener(this);
        dialog = builder.create();

        /***** 별 상점 ******/
        starBuilder = new AlertDialog.Builder(this);
        starView = View.inflate(this, R.layout.star_shop, null);
        starBuilder.setView(starView);
        exit = starView.findViewById(R.id.exit);
        buyMoney = starView.findViewById(R.id.buyMoney);
        buyRandom = starView.findViewById(R.id.buyRandom);
        exit.setOnClickListener(this);
        buyMoney.setOnClickListener(this);
        buyRandom.setOnClickListener(this);
        starDialog = starBuilder.create();

        settingStar = starView.findViewById(R.id.settingStar);

        setting = getSharedPreferences("setting", 0);

        //setContentView(R.layout.custom_dialog);
        //dialog.setCancelable(false);
        /***** 메뉴얼 ******/
        menualBuilder = new AlertDialog.Builder(this);
        menualVeiw = View.inflate(this, R.layout.menual_dialog, null);
        menualBuilder.setView(menualVeiw);
        menualClose = menualVeiw.findViewById(R.id.menualOff);
        menualClose.setOnClickListener(this);
        menualDialog = menualBuilder.create();
    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.gameStart : startActivity(intent); finish(); break;
            case R.id.menual : menual(); break;
            case R.id.finish : finish(); break;
            case R.id.setting : setting(); break;
            case R.id.settingOn : settingOn(); break;
            case R.id.settingOff : settingOff(); break;
            case R.id.starShop : Log.i(TAG, "starShop인입"); starShop(); break;
            case R.id.buyMoney : buyMoney(); break;
            case R.id.buyRandom : buyRandom(); break;
            case R.id.exit : exit(); break;
            case R.id.menualOff: menualOff(); break;
        }
    }
    //Toast.makeText(SubActivity.this, "yes??", Toast.LENGTH_SHORT).show(); break;

    public void setting(){
        dialog.show();

    }

    boolean flag;
    public void settingOn(){
        editor = setting.edit();
        editor.putBoolean("flag", flag); // 사운드 온은 flag false
        editor.commit();
        dialog.dismiss();
    }

    public void settingOff(){
        flag = true;
        editor = setting.edit();
        editor.putBoolean("flag", flag); // 사운드 오프는 true
        editor.commit();
        dialog.dismiss();
    }

    public void starShop(){
        starDialog.show();
        star = setting.getInt("star", 0);
        settingStar.setText("x"+star);


    }


    public void buyMoney(){
        int numb = 50;
        int uMoney = setting.getInt("uMoney", 0);
        star = setting.getInt("star", 0);
        if(star < 2){
            Toast.makeText(this, "게임에 승리하여 별을 모으세요!!", Toast.LENGTH_SHORT).show();
        }else {
            star = star - 2;
            editor = setting.edit();
            editor.putInt("uMoney", uMoney + numb);
            editor.putInt("star", star);
            editor.commit();
            Toast.makeText(this, "$50 추가 당신의 총 자산은"+(uMoney+50), Toast.LENGTH_SHORT).show();
            settingStar.setText("x" + star);
        }
    }

    public void buyRandom(){
        int numb = ran.nextInt(451)+50;
        int uMoney = setting.getInt("uMoney", 0);
        star = setting.getInt("star", 0);
        if(star < 10){
            Toast.makeText(this, "게임에 승리하여 별을 모으세요!!", Toast.LENGTH_SHORT).show();
        }else {
            star = star - 10;
            editor = setting.edit();
            editor.putInt("uMoney", uMoney + numb);
            editor.putInt("star", star);
            editor.commit();
            Toast.makeText(this, "$"+numb+"추가 당신의 총 자산은"+(uMoney+numb), Toast.LENGTH_SHORT).show();
            settingStar.setText("x" + star);
        }
    }

    public void exit(){
        starDialog.dismiss();
    }

    public void menual(){
        menualDialog.show();
    }

    public void menualOff(){
        menualDialog.dismiss();
    }

}

/*

딜러는 17이상이 될때까지 카드를 받아야만하며, 17이 넘으면 카드를 못받는다.
첫 2장으로 21을만들면 블랙잭이라 하며 건 금액의 2배를 받는다. 히트하여 21을 만들면 1.5배를 받는다.
 더블다운, 스플릿, 인셔런스, 이븐머니

더블다운 : 본래 합이 21이 넘지 않는 한 카드를 이후에 단 한장만 더받는 조건으로 돈을 2배로 걸 수 있다. 자신의 카드가 10이나 11일 경우에 많이 사용

스플릿: 처음 받은 2장의 카드가 같은 숫자일경우, 패를 2개로 나누어 게임을 두번 할 수 있는 시스템

인셔런스: 건돈의 절반을 보험에 들것인지를 제안한다. 딜러의 패가 a인 경우 제안받으며, 딜러가 블랙잭이 나오면 건돈을  잃지만 인슈런스에 들어간 돈을 두배로 타므로 비기는셈인거다.
         하지만 딜러가 블랙잭이 아닐경우 인셔런스에 배팅한금액을 잃게되며, 설상가상 오픈 숫자도 낮으면 건돈까지 모두 일게 된다.

이븐머니: 플레이어는 21이고 딜러는 a를 갖고있다면 딜러가 이븐머니를 할지 물어본다 만약 딜러도 21이면 비기지만 이경우 21인것으로 간주하여 1.5배 돈을 번다 하지만 딜러가 블랙잭이 아니라면 건돈의 반만 가져간다.
         즉 이븐머니는 인셔런스와 비슷하게 건돈의 반을 더 주거나 덜 받는뜻


*/
