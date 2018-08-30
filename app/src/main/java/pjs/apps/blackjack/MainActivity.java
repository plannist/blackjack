package pjs.apps.blackjack;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;
import android.view.View.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.TreeSet;



public class MainActivity extends AppCompatActivity implements
        OnClickListener, OnSeekBarChangeListener, ViewFactory {

    private static final String TAG = "BlackJack";
    public static LinearLayout layout;

    private final String saveFileName = "savingHighScoreOfBlackJack";

    // 모든 엘리먼츠 선언
    public static TextView starCount, userMoney, userBet, dCount, uCount, txt_count, even_count;
    public ImageSwitcher dilCard1, dilCard2, dilCard3, dilCard4, dilCard5, win_lose, dealerMasage, betMasage, coin1, coin10, coin100;
    public ImageSwitcher userCard1, userCard2, userCard3, userCard4, userCard5;
    public Vibrator vibrator;
    public ImageButton btStand, btBack;
    public SeekBar betting;
    public Intent intent;
    public Button btn_yes, btn_no, db_btn_yes, db_btn_no, even_btn_yes, even_btn_no;
    AlertDialog dialog, dbDialog, evenDialog;
    AlertDialog.Builder builder, dbBuilder, evenBuilder;
    View view, dbView, evenView;
    SharedPreferences setting; //저장하기 위한 프리페어런스 선언
    SharedPreferences.Editor editor; //값을 실제로 저장하는 부분
    ProgressBar pb, evPb;

    SoundPool sound = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
    int cardFlip, bridgingCards, dealingCard, coin, victory, losed, gameStart, selectButton;


    // 모든 변수 선언
    public int value; // 프로그래스바의 최대값, 스레드안에서 사용됨
    public int bet = 0; //배팅금액
    public int uMoney; //플레이어자산
    public int star;
    //public int dMoney; //딜러자산
    public int getMoney; //프리퍼런스에서 가져온 유저 돈
    public int count; //유저 카드번호 합
    public int dealerCount; // 딜러 카드번호 합
    // 0번 4번까지 플레이어 카드 5번부터 7번까지 딜러카드 9번은 더블 10번은 초기화중 막 눌렀을때, 11번은 INSURANCE, 12번은 EVEN MONEY, 13번은 stand 연속으로 못들어가게, 14번은 더블버튼 연속해서 못누르게
    boolean [] flag = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};

    Random ran = new Random();
    TreeSet<String> getCardNumber = new TreeSet<String>(); // 랜덤값 저장 (중복 안되기 위하여)
    ArrayList<String> list = new ArrayList<String>(); // 트리셋에서 뺀 값 저장 (shuffle 및 편하게 가져오기 위하여)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent(this, SubActivity.class);
        setting = getSharedPreferences("setting", 0); //저장되어있는 프리페어런스 값을 불러온다. 0번은 MODE_PREVATE와 동일
        star = setting.getInt("star", 0);

        init();
        setVariables();
        setImageSwitcher();
        formatAll();
    }

    public void init(){
        /**** SeekBar (배팅 금액 설정 바)*****/
        betting = findViewById(R.id.betting);
        betting.setOnSeekBarChangeListener(this);

        /**** 텍스트 뷰 (배팅 금액)*****/
        userBet = findViewById(R.id.userBet);

        /**** 텍스트 뷰 (플레이어 자산)*****/
        userMoney = findViewById(R.id.userMoney);

        /**** 텍스트 뷰 (별 카운트)*****/
        starCount = findViewById(R.id.starCount);

        /**** 텍스트 뷰 (플레이어, 딜러 카운트)*****/
        uCount = findViewById(R.id.userCount);
        dCount = findViewById(R.id.dealerCount);

        /***** 버튼 (스타트, 카드더줘, 스탑 버튼) ******/
        btBack = findViewById(R.id.btBack);
        btStand = findViewById(R.id.btStand);
        btBack.setOnClickListener(this);
        btStand.setOnClickListener(this);

        /***** 이미지 스위쳐 (플레이어 카드, 딜러 카드) ******/
        dilCard1 = findViewById(R.id.dilCard1);
        dilCard2 = findViewById(R.id.dilCard2);
        dilCard3 = findViewById(R.id.dilCard3);
        dilCard4 = findViewById(R.id.dilCard4);
        dilCard5 = findViewById(R.id.dilCard5);

        userCard1 = findViewById(R.id.userCard1);
        userCard2 = findViewById(R.id.userCard2);
        userCard3 = findViewById(R.id.userCard3);
        userCard4 = findViewById(R.id.userCard4);
        userCard5 = findViewById(R.id.userCard5);

        win_lose = findViewById(R.id.win_lose);
        dealerMasage = findViewById(R.id.dealerMasage);
        betMasage = findViewById(R.id.betMasage);


        /***** 이미지 스위쳐 클릭 (플레이어 카드) ******/
        userCard1.setOnClickListener(this);
        userCard2.setOnClickListener(this);
        userCard3.setOnClickListener(this);
        userCard4.setOnClickListener(this);
        userCard5.setOnClickListener(this);

        betMasage.setOnClickListener(this);

        /***** 이미지 스위쳐 클릭 (배팅 코인) ******/
        coin1 = findViewById(R.id.coin1);
        coin10 = findViewById(R.id.coin10);
        coin100 = findViewById(R.id.coin100);


        coin1.setOnTouchListener(new RepeatListener(300, 150, new OnClickListener() {
            @Override
            public void onClick(View view) {

                coin1();
            }
        }));
        coin10.setOnTouchListener(new RepeatListener(300, 150, new OnClickListener() {
            @Override
            public void onClick(View view) {

                coint10();
            }
        }));
        coin100.setOnTouchListener(new RepeatListener(300, 150, new OnClickListener() {
            @Override
            public void onClick(View view) {

                coin100();
            }
        }));




        /***** INSURANCE 다이얼로그 창 ******/
        builder = new AlertDialog.Builder(this);
        //builder.setTitle("INSURANCE 에 배팅하겠습니까?");
        view = View.inflate(this, R.layout.custom_dialog, null);
        builder.setView(view);
        btn_yes = view.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(this);
        btn_no = view.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(this);
        pb = view.findViewById(R.id.ProgressBar);

        builder.setCancelable(false);
        dialog = builder.create();

        /**** 텍스트 뷰 (보험 가입 여부 카운트)*****/
        txt_count = view.findViewById(R.id.txt_count);

        /***** 다이얼 로그 출력 ******/
//        setContentView(R.layout.custom_dialog);
//        dialog.setCancelable(false);  ////

        /***** DOUBLE BETTING 다이얼로그 창 ******/
        dbBuilder = new AlertDialog.Builder(this);
        //builder.setTitle("INSURANCE 에 배팅하겠습니까?");
        dbView = View.inflate(this, R.layout.double_dialog, null);
        dbBuilder.setView(dbView);
        db_btn_yes = dbView.findViewById(R.id.db_btn_yes);
        db_btn_yes.setOnClickListener(this);
        db_btn_no = dbView.findViewById(R.id.db_btn_no);
        db_btn_no.setOnClickListener(this);
        dbBuilder.setCancelable(false);
        dbDialog = dbBuilder.create();

        /***** EVEN MONEY 다이얼로그 창 ******/
        evenBuilder = new AlertDialog.Builder(this);
        //builder.setTitle("INSURANCE 에 배팅하겠습니까?");
        evenView = View.inflate(this, R.layout.even_dialog, null);
        evenBuilder.setView(evenView);
        even_btn_yes = evenView.findViewById(R.id.even_btn_yes);
        even_btn_yes.setOnClickListener(this);
        even_btn_no = evenView.findViewById(R.id.even_btn_no);
        even_btn_no.setOnClickListener(this);
        evenBuilder.setCancelable(false);
        evPb = evenView.findViewById(R.id.ProgressBar);
        evenDialog = evenBuilder.create();
        even_count = evenView.findViewById(R.id.txt_count);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        /***** SOUND LOAD ******/
        cardFlip = sound.load(this, R.raw.cardflip, 1);
        bridgingCards = sound.load(this, R.raw.bridgingcards, 1);
        gameStart = sound.load(this, R.raw.bridgingcards, 1);
        dealingCard = sound.load(this, R.raw.gamestart, 1);
        selectButton = sound.load(this, R.raw.selectbutton, 1);
        coin = sound.load(this, R.raw.coin, 1);
        victory = sound.load(this, R.raw.victory, 1);
        losed = sound.load(this, R.raw.youlose, 1);

        boolean soundFlag = setting.getBoolean("flag", false);
        Log.i(TAG, "soundFlag: "+soundFlag);
        if(soundFlag){
            sound.autoPause();
            sound.release();
        }

        try {
            Thread.sleep(1000);
        }catch(InterruptedException ex){}

        sound.play(gameStart, 1.0F, 1.0F, 1, 0, 1.0F);
    }

    public void setVariables(){
        userMoney.setText("$ "+uMoney);
        starCount.setText("x"+star);
    }

    public void setImageSwitcher() {

        dilCard1.setFactory(this);
        dilCard2.setFactory(this);
        dilCard3.setFactory(this);
        dilCard4.setFactory(this);
        dilCard5.setFactory(this);

        userCard1.setFactory(this);
        userCard2.setFactory(this);
        userCard3.setFactory(this);
        userCard4.setFactory(this);
        userCard5.setFactory(this);

        win_lose.setFactory(this);
        dealerMasage.setFactory(this);
        betMasage.setFactory(this);

        coin1.setFactory(this);
        coin10.setFactory(this);
        coin100.setFactory(this);

        dilCard1.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        dilCard1.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        dilCard2.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        dilCard2.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        dilCard3.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        dilCard3.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        dilCard4.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        dilCard4.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        dilCard5.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        dilCard5.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));

        userCard1.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        userCard1.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        userCard2.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        userCard2.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        userCard3.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        userCard3.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        userCard4.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        userCard4.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        userCard5.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        userCard5.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));

        win_lose.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        win_lose.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));

        dealerMasage.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        dealerMasage.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        betMasage.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        betMasage.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        coin1.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        coin1.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        coin10.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        coin10.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        coin100.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        coin100.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
    }

    public void formatAll(){
        list.removeAll(list);
        getCardNumber.removeAll(getCardNumber);
        bet = 0;
//        sound.stop(cardFlip);
//        sound.stop(bridgingCards); //브릿지, 딜링, 카드플립, 되는거확인
//        sound.stop(coin);
//        sound.stop(selectButton);
//        sound.stop(gameStart);
//        sound.stop(victory);
//        sound.stop(losed);

        getMoney = setting.getInt("uMoney", 500); //uMoney라는 키값으로 찾아온 인트 밸류값을 uMoney 변수에 저장. 값을 못찾을땐 디폴트값 = 500

        uMoney = getMoney;


        if(uMoney <= 0){
            Toast.makeText(this, "기본 금액 $2 충전, 별상점에서 $ 를 구매하세요", Toast.LENGTH_SHORT).show();
            uMoney = 2;
        }
        userBet.setText("Bet: $" +bet);
        userMoney.setText("$" +uMoney);


        sound.play(bridgingCards, 1.0F, 1.0F, 1, 0, 1.0F);

        dilCard1.setImageResource(R.drawable.blue_back);
        dilCard2.setImageResource(R.drawable.cardblock);
        dilCard3.setImageResource(R.drawable.cardblock);
        dilCard4.setImageResource(R.drawable.cardblock);
        dilCard5.setImageResource(R.drawable.cardblock);

        userCard1.setImageResource(R.drawable.gray_back);
        userCard2.setImageResource(R.drawable.cardblock);
        userCard3.setImageResource(R.drawable.cardblock);
        userCard4.setImageResource(R.drawable.cardblock);
        userCard5.setImageResource(R.drawable.cardblock);

        betting.setVisibility(View.VISIBLE);
        betting.setProgress(0);

        coin1.setImageResource(R.drawable.coin1);
        coin10.setImageResource(R.drawable.coin10);
        coin100.setImageResource(R.drawable.coin100);

        while(getCardNumber.size() <= 10){
            String str = "";
            int ranNum = ran.nextInt(51)+1;
            //Log.i(TAG, "ranNum: "+ranNum);
            if(ranNum /13 == 1) str = "d";
            else if(ranNum /13 == 2) str = "s";
            else if(ranNum /13 == 3) str = "h";
            else if(ranNum /13 == 4) str = "c";
            else if(ranNum /13 == 0) str = "c";

            if(ranNum %13 == 0) str = str+"1";
            else if(ranNum %13 == 1) str = str+"2";
            else if(ranNum %13 == 2) str = str+"3";
            else if(ranNum %13 == 3) str = str+"4";
            else if(ranNum %13 == 4) str = str+"5";
            else if(ranNum %13 == 5) str = str+"6";
            else if(ranNum %13 == 6) str = str+"7";
            else if(ranNum %13 == 7) str = str+"8";
            else if(ranNum %13 == 8) str = str+"9";
            else if(ranNum %13 == 9) str = str+"10";
            else if(ranNum %13 == 10) str = str+"11";
            else if(ranNum %13 == 11) str = str+"12";
            else if(ranNum %13 == 12) str = str+"13";

            getCardNumber.add(str);
            Log.i(TAG, "ranNum: "+ranNum+", str: "+str);
        }

        for(String str : getCardNumber){
            list.add(str);
        }
        Collections.shuffle(list);

//        list.add("h1");
//        list.add("h11");
//        list.add("d3");
//        list.add("c4");
//        list.add("c5");
//        list.add("d1");
//        list.add("s11");
//        list.add("h9");
//        list.add("c4");
//        list.add("h6");

        for(int i=0; i<list.size();i++){
            Log.i(TAG, "list: "+list.get(i)+", i: "+i);
        }

        count = 0;
        dealerCount = 0;

        for(int i =0; i<=14; i++) {
            flag[i] = false;
        }

        betMasage.setImageResource(R.drawable.betmasageblock);

        uCount.setVisibility(View.INVISIBLE);
        dCount.setVisibility(View.INVISIBLE);
}


    public void coin1(){
        String userBetText = (String)userBet.getText();
        String uMoneyText = (String)userMoney.getText();
        int bet = betAndMoney(userBetText);
        int uMoney = betAndMoney(uMoneyText);
        if(uMoney > 0 && !flag[0]) {
            sound.play(coin, 1.0F, 1.0F, 1, 0, 1.0F);
            int coin = 1;
            bet = bet+coin;
            uMoney = uMoney-coin;
            userBet.setText("Bet: $" +bet);
            userMoney.setText("$" +uMoney);
        }else{
            Toast.makeText(this, "더이상 배팅할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public int betAndMoney(String userBetMoney){
        int idx = userBetMoney.indexOf("$");
        String bet = userBetMoney.substring(idx+1);
        bet = bet.trim();
        int intUserBet = Integer.parseInt(bet);
        return intUserBet;
    }

    public void coint10(){
        String userBetText = (String)userBet.getText();
        String uMoneyText = (String)userMoney.getText();
        int bet = betAndMoney(userBetText);
        int uMoney = betAndMoney(uMoneyText);
        Log.i(TAG, "coin 에서 uMoney: "+uMoney);
        if(uMoney > 10 && !flag[0]) {
            sound.play(coin, 1.0F, 1.0F, 1, 0, 1.0F);
            int coin = 10;
            bet = bet+coin;
            uMoney = uMoney-coin;
            userBet.setText("Bet: $" +bet);
            userMoney.setText("$" +uMoney);
        }else{
            Toast.makeText(this, "더이상 배팅할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    public void coin100(){
        String userBetText = (String)userBet.getText();
        String uMoneyText = (String)userMoney.getText();
        int bet = betAndMoney(userBetText);
        int uMoney = betAndMoney(uMoneyText);
        Log.i(TAG, "coin 에서 uMoney: "+uMoney);
        if(uMoney > 100 && !flag[0]) {
            sound.play(coin, 1.0F, 1.0F, 1, 0, 1.0F);
            int coin = 100;
            bet = bet+coin;
            uMoney = uMoney-coin;
            userBet.setText("Bet: $" +bet);
            userMoney.setText("$" +uMoney);
        }else{
            Toast.makeText(this, "더이상 배팅할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btBack: btBack();break;
            case R.id.btStand: btStand();break;
            case R.id.userCard1: userCard1();break;
            case R.id.userCard2: userCard2();break;
            case R.id.userCard3: userCard3();break;
            case R.id.userCard4: userCard4();break;
            case R.id.userCard5: userCard5();break;
            case R.id.btn_yes : btnYes(); break;
            case R.id.btn_no : btnNo(); break;
            case R.id.betMasage : dbDialog(); break;
            case R.id.db_btn_yes : dbBtnYes(); break; //flag 14
            case R.id.db_btn_no : dbBtnNo(); break;
            case R.id.even_btn_yes : evenBtnYes(); break;
            case R.id.even_btn_no : evenBtnNo(); break;
        }
    }

    public void userCard1(){
        win_lose.setImageResource(R.drawable.trans);
        dealerMasage.setImageResource(R.drawable.dealerplay);
        String uMoneyText = (String)userMoney.getText();
        String betText = (String)userBet.getText();
        uMoney= betAndMoney(uMoneyText);
        bet = betAndMoney(betText);

            if(!flag[10]) {
                if (bet == 0) {
                    betMasage.setImageResource(R.drawable.betmasage);
                    Toast.makeText(MainActivity.this, "배팅을 하고 카드를 뒤집어!", Toast.LENGTH_SHORT).show();
                } else {
                    flag[0] = true;

                    Log.i(TAG, "card1뒤집을때 uMoney: "+uMoney+", bet: "+bet);
                    coin1.setImageResource(R.drawable.coinblock);
                    coin10.setImageResource(R.drawable.coinblock);
                    coin100.setImageResource(R.drawable.coinblock);
                    betting.setVisibility(View.INVISIBLE);
                    userCard1.setImageResource(getResources().getIdentifier(list.get(0), "drawable", this.getPackageName()));
                    userCard2.setImageResource(R.drawable.gray_back);
                    sound.play(cardFlip, 1.0F, 1.0F, 1, 0, 1.0F);
                    count = countNum(0, 0, 0);
                    Log.i(TAG, "count: " + count);
                    dealerCard1();

                }
            }
        }

    public void userCard2() {
        if(!flag[10]) {
            if (!flag[0]) {
                //Toast.makeText(MainActivity.this, "첫번째 카드부터 뒤집어라;", Toast.LENGTH_SHORT).show();
            } else {
                flag[1] = true;
                userCard2.setImageResource(getResources().getIdentifier(list.get(1), "drawable", this.getPackageName()));
                sound.play(cardFlip, 1.0F, 1.0F, 1, 0, 1.0F);
                betMasage.setImageResource(R.drawable.downdouble);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    Log.i(TAG, "ie: " + ie);
                }
                count = countNum(1, 0, 1);

                userCard3.setImageResource(R.drawable.gray_back);
                Log.i(TAG, "count: " + count);

                if(dealerCount == 11 && count == 21){
                    EvenDialog();
                }
            }
        }
    }

    public void userCard3(){
        if(!flag[10]) {
            if (!flag[0] || !flag[1]) {
                //Toast.makeText(MainActivity.this, "첫번째 카드부터 뒤집어라;", Toast.LENGTH_SHORT).show();
            } else {
                flag[2] = true;
                betMasage.setImageResource(R.drawable.betmasageblock);
                userCard3.setImageResource(getResources().getIdentifier(list.get(2), "drawable", this.getPackageName()));
                sound.play(cardFlip, 1.0F, 1.0F, 1, 0, 1.0F);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    Log.i(TAG, "ie: " + ie);
                }
                count = countNum(2, 0, 2);

                userCard4.setImageResource(R.drawable.gray_back);
                Log.i(TAG, "count: " + count);

                if(count > 21) {
                    youLose();
                }else if (flag[9] && count <= 21){
                    btStand(); //더블 배팅했으면
                }
            }
        }
    }

    public void userCard4() {
        if(!flag[10]) {
            if (!flag[0] || !flag[1] || !flag[2] || flag[9]) {
                //Toast.makeText(MainActivity.this, "첫번째 카드부터 뒤집어라;", Toast.LENGTH_SHORT).show();
            } else {
                flag[3] = true;
                userCard4.setImageResource(getResources().getIdentifier(list.get(3), "drawable", this.getPackageName()));
                sound.play(cardFlip, 1.0F, 1.0F, 1, 0, 1.0F);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    Log.i(TAG, "ie: " + ie);
                }
                count = countNum(3, 0, 3);
                userCard5.setImageResource(R.drawable.gray_back);
                Log.i(TAG, "count: " + count);
                if(count > 21) youLose();
            }
        }
    }

    public void userCard5() {
        if(!flag[10]) {
            if (!flag[0] || !flag[1] || !flag[2] || !flag[3] || flag[9]) {
                //Toast.makeText(MainActivity.this, "첫번째 카드부터 뒤집어라;", Toast.LENGTH_SHORT).show();
            } else {
                flag[4] = true;
                userCard5.setImageResource(getResources().getIdentifier(list.get(4), "drawable", this.getPackageName()));
                sound.play(cardFlip, 1.0F, 1.0F, 1, 0, 1.0F);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    Log.i(TAG, "ie: " + ie);
                }
                count = countNum(4, 0, 4);
                Log.i(TAG, "count: " + count);
                if(count > 21){
                    youLose();
                }else{
                    btStand();
                }
            }
        }
    }

    public void dealerCard1(){
        dilCard1.setImageResource(getResources().getIdentifier(list.get(5), "drawable", this.getPackageName()));
        dilCard2.setImageResource(R.drawable.blue_back);
        dealerCount = countNumDealer(5, 5, 5);
        Log.i(TAG, "첫번째 카드 dealerCount: "+dealerCount);

        if(dealerCount == 1 || dealerCount == 11){
            dialog();
        }
    }

    public void dealerCard2() {
        dilCard2.setImageResource(getResources().getIdentifier(list.get(6), "drawable", this.getPackageName()));
        dealerCount = countNumDealer(6, 5, 6);
        Log.i(TAG, "두번째 카드 dealerCount: " + dealerCount);
        if (!flag[12]) {
            if (17 <= dealerCount || (count == 21 && !flag[2])) {
                flag[5] = false;
            } else {
                flag[5] = true;
            }

        }else if(flag[12] && dealerCount == 21){
            Log.i(TAG, "두번째 카드 이븐 머니 배팅 했을때 dealerCount: " + dealerCount);
            btStand();
        }
    }
    //if (!flag[2] && count == 21 && dealerCount != 21) {

    public void dealerCard3(){
        if(flag[5]) {
            dilCard3.setImageResource(getResources().getIdentifier(list.get(7), "drawable", this.getPackageName()));
            dealerCount = countNumDealer(7, 5, 7);
            Log.i(TAG, "세번째 카드 dealerCount: " + dealerCount);

            if (17 <= dealerCount || (count == 21 && !flag[2])) {
                flag[6] = false;
            } else {
                flag[6] = true;
            }
        }
    }

    public void dealerCard4(){
        if(flag[6]){
            dilCard4.setImageResource(getResources().getIdentifier(list.get(8), "drawable", this.getPackageName()));
            dealerCount = countNumDealer(8, 5, 8);
            Log.i(TAG, "네번째 카드 dealerCount: "+dealerCount);

            if (17 <= dealerCount || (count == 21 && !flag[2])){
                flag[7] = false;
            }else{
                flag[7] = true;
            }
        }
    }

    public void dealerCard5(){
        if(flag[7]){
            dilCard5.setImageResource(getResources().getIdentifier(list.get(9), "drawable", this.getPackageName()));
            dealerCount = countNumDealer(9, 5, 9);
            Log.i(TAG, "다섯번째 카드 dealerCount: "+dealerCount);

        }
    }

    ArrayList<Integer> sumAll = new ArrayList<Integer>();
    public int countNum(int idx, int start, int end){
        //Log.i(TAG, "countNum의 인입");
        int sum = 0;
        int count = 0;
        int aceCount=0;

        for(int i=start; i <= end; i++){
            sum =Integer.parseInt(list.get(i).substring(1));
            if(sum ==11 || sum==12 || sum==13){
                sum = 10;
            }
            Log.i(TAG, "userSum["+i+"] : "+sum);
            if(sum == 1){ //ace 여부 및 갯수 파악
                aceCount++;
                sumAll.add(11); //ace가 있으면 11으로 저
            }else{
                sumAll.add(sum); //ace가 없으면 sum 으로 저장
            }
        }

        //Log.i(TAG, "sumAll size: "+sumAll.size());

        for(int j=0; j<sumAll.size(); j++){ //count에 저장
            count += sumAll.get(j);
            Log.i(TAG, "유저카운트에서 에이처리안된 count값: "+count);
        }

        if(count > 21 && aceCount>0){ //count가 21이상이고 ace가 한개 있을때
            count = count-10;
            Log.i(TAG, "에이 한개의 카운트 count: "+count);
            if(count >21 && aceCount>1){
                count = count-10;
                Log.i(TAG, "에이 두개의 카운트 count: "+count);
                if(count >21 && aceCount>2){
                    count = count-10;
                    Log.i(TAG, "에이 세개의 카운트 count: "+count);
                }
            }
        }
        //if(count > 21)youLose();
        //else if(count == 21 && dealerCount != 21)youWin();
        sumAll.removeAll(sumAll);
        return count;
    }


    ArrayList<Integer> sumAllDealer = new ArrayList<Integer>();
    public int countNumDealer(int idx, int start, int end){
        //Log.i(TAG, "countNum의 인입");
        int sum = 0;
        int dealerCount = 0;
        int aceCount=0;
        for(int i=start; i <= end; i++){
            sum =Integer.parseInt(list.get(i).substring(1));
            if(sum ==11 || sum==12 || sum==13){
                sum = 10;
            }
            //Log.i(TAG, "dealerSum["+i+"], "+sum);
            if(sum == 1){ //ace 여부 및 갯수 파악
                aceCount++;
                sumAllDealer.add(11); //ace가 있으면 11로 저장
            }else{
                sumAllDealer.add(sum); //ace가 없으면 sum 으로 저장
            }
        }
        //Log.i(TAG, "sumAllDealer size: "+sumAllDealer.size());

        for(int j=0; j<sumAllDealer.size(); j++){ //count에 저장
            dealerCount += sumAllDealer.get(j);
        }

        if(dealerCount > 21 && aceCount>0){ //count가 21이상이고 ace가 한개 있을때
            dealerCount = dealerCount-10;
            if(dealerCount >21 && aceCount>1){
                dealerCount = dealerCount-10;
                if(dealerCount >21 && aceCount>2){
                    dealerCount = dealerCount-10;
                }
            }
        }

        //if(dealerCount == 21 && count != 21) youLose();
        //else if(dealerCount > 21) youWin();
        sumAllDealer.removeAll(sumAllDealer);
        return dealerCount;
    }

    public void youLose(){
        Log.i(TAG, "졌어 인입");

        flag[10] = true;
        if(flag[11] && dealerCount == 21) { //INSURANCE 성공 여부
            Toast.makeText(this, "INSURANCE 성공 비겼습니다!", Toast.LENGTH_SHORT).show();
            draw();
        }else{
            sound.play(losed, 1.0F, 1.0F, 1, 0, 1.0F);
            new CountDownTimer(3000, 1000){
                @Override
                public void onTick(long l) {

                }
                @Override
                public void onFinish() {
                    uCount.setVisibility(View.VISIBLE);
                    dCount.setVisibility(View.VISIBLE);
                    uCount.setText("Count : "+count);
                    dCount.setText("Count : "+dealerCount);
                }
            }.start();
            vibrator.vibrate(300);
            betMasage.setImageResource(R.drawable.betformatmasage);
            win_lose.setImageResource(R.drawable.lose);
            dealerMasage.setImageResource(R.drawable.dealerwin);
            //Toast.makeText(MainActivity.this, "당신의 패배 Count: "+count, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "youLose의 uMoney: "+uMoney+", bet: "+bet);
            //dMoney = dMoney+bet;
            //uMoney = uMoney-bet;

            userMoney.setText("$ "+uMoney);
            //dealerMoney.setText("$ "+dMoney);
            editor = setting.edit();
            editor.putInt("uMoney", uMoney);
            editor.commit();
            new CountDownTimer(5000, 1000){
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    vibrator.vibrate(300);
                    formatAll();
                }
            }.start();
        }

    }

    public void youWin(){
        Log.i(TAG, "이겼어 인입");
        sound.play(victory, 1.0F, 1.0F, 1, 0, 1.0F);
        flag[10] = true;
        star++;
        starCount.setText("x"+star);
        new CountDownTimer(3000, 1000){
            @Override
            public void onTick(long l) {

            }
            @Override
            public void onFinish() {
                uCount.setVisibility(View.VISIBLE);
                dCount.setVisibility(View.VISIBLE);
                uCount.setText("Count : "+count);
                dCount.setText("Count : "+dealerCount);
            }
        }.start();

        vibrator.vibrate(300);
        win_lose.setImageResource(R.drawable.win);
        dealerMasage.setImageResource(R.drawable.dealerlose);
        //Toast.makeText(MainActivity.this, "당신의 승리 Count: "+count, Toast.LENGTH_SHORT).show();
        bet = bet*2;
        uMoney = uMoney+bet;
        //dMoney = dMoney-bet;
        Log.i(TAG, "youWin의 uMoney: "+uMoney+", bet: "+bet);
        userMoney.setText("$ "+uMoney);
        //dealerMoney.setText("$ "+dMoney);
        editor = setting.edit();
        editor.putInt("uMoney", uMoney);
        editor.commit();

        new CountDownTimer(5000, 1000){
            @Override
            public void onTick(long l) {

            }
            @Override
            public void onFinish() {
                vibrator.vibrate(300);
                formatAll();
            }
        }.start();

    }

    public void draw(){
        flag[10] = true;
        new CountDownTimer(5000, 1000){
            @Override
            public void onTick(long l) {

            }
            @Override
            public void onFinish() {
                uCount.setVisibility(View.VISIBLE);
                dCount.setVisibility(View.VISIBLE);
                uCount.setText("Count : "+count);
                dCount.setText("Count : "+dealerCount);

            }
        }.start();
        vibrator.vibrate(300);
        uMoney = bet+uMoney;
        userMoney.setText("$ "+uMoney);
        //dealerMoney.setText("$ "+dMoney);
        win_lose.setImageResource(R.drawable.draw);
        editor = setting.edit();
        editor.putInt("uMoney", uMoney);
        editor.commit();
        new CountDownTimer(5000, 1000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                vibrator.vibrate(300);
                formatAll();
            }
        }.start();

    }

    public void btBack() {
        Log.i(TAG, "btBack 인입");
        sound.play(selectButton, 1.0F, 1.0F, 1, 0, 1.0F);
        startActivity(intent);
        finish();
    }

    public void btStand(){
        if(!flag[13]) {
            flag[13] = true;
            Log.i(TAG, "btStand 인입");
            sound.play(selectButton, 1.0F, 1.0F, 1, 0, 1.0F);
            if (!flag[10]) {
                if (flag[9]) {
                    if (!flag[2]) {
                        Toast.makeText(this, "DOUBLE BETTING후 한장은 무조건 오픈해야합니다.", Toast.LENGTH_SHORT).show();
                        userCard3();
                    }
                }

                if (flag[1]) {
                    if (dealerCount <= 16) dealerCard2();
                    if (dealerCount <= 16) dealerCard3();
                    if (dealerCount <= 16) dealerCard4();
                    if (dealerCount <= 16) dealerCard5();

                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            if (dealerCount < 21 && count < 21 && count > dealerCount) youWin();
                            else if (dealerCount < 21 && count < 21 && count < dealerCount) youLose();
                            else if (dealerCount < 21 && count < 21 && count == dealerCount) draw();
                                //else if (flag[9] && count > 21) youLose(); //더블 배팅 후, 3번째 카드를 까면 스탠드로 들어오고 그랬을때 유저 카운트가 21이 넘는지 확인
                            else if (flag[12] && dealerCount == 21) {//evenMoney 성공시
                                uMoney = bet+uMoney;
                                bet = (int)((bet/2)*1.5);
                                //Toast.makeText(this, "EVENMONEY성공 배팅액의 1.5배를 획득합니다.!", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "딜러 두번째카드의 이븐머니 성공 uMoney : " + uMoney + ", bet: " + bet);
                                youWin();
                            } else if (flag[12] && dealerCount != 21) { //이븐머니 실패시
                                uMoney = bet+uMoney;
                                bet = bet / 2;
                                //Toast.makeText(this, "EVENMONEY실패 배팅액의 반만 획득합니다.!", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "딜러 두번째카드의 이븐머니 실패 uMoney : " + uMoney + ", bet: " + bet);
                                youWin();
                            } else if (!flag[2] && count == 21 && dealerCount != 21) { // 내가 블랙잭일때 //
                                Log.i(TAG, "블랙잭일때 bet: " + bet + ", uMoney: " + uMoney);
                                bet = bet + (bet / 2);
                                Log.i(TAG, "블랙잭일때 배팅 2배 적용 후,  bet: " + bet + ", uMoney: " + uMoney);

                                youWin();
                            } else if (dealerCount == 21 && count != 21) { // 상대가 블랙잭일때
                                uMoney = uMoney - bet; //youLose에는 uMoney 가 베팅만큼 마이너스 되는 메서드가없다.
                                bet = bet * 2;
                                Log.i(TAG, "딜러 두번째카드의 딜러 블랙잭 uMoney : " + uMoney + ", bet: " + bet);
                                youLose();
                            } else if (dealerCount > 21 && count != 21) { //딜러 카운트가 21이 넘을때
                                Log.i(TAG, "딜러 두번째카드의 딜러 오버 uMoney : " + uMoney + ", bet: " + bet);
                                youWin();
                            } else if (count == 21 && dealerCount != 21 && flag[2]) { //블랙잭은 아니지만 21이 나왔을때 //
                                uMoney = uMoney+bet;
                                bet = (int)((bet/2)*1.5); //1.5배 이긴다로 들어가면 다 이 식을 사용해야함
                                Log.i(TAG, "딜러 두번째카드의 유저 21 uMoney : " + uMoney + ", bet: " + bet);
                                youWin();
                            } else if (dealerCount == 21 && count == 21) {
                                Log.i(TAG, "딜러 플레이어 모두 블랙잭 혹은 21 uMoney : " + uMoney + ", bet: " + bet);
                                draw();
                            } else if(dealerCount == 21 && count != 21 && flag[6]){ // 딜러가 블랙잭은 아니지만 21로 이겼을때
                                Log.i(TAG, "딜러가 블랙잭은 아니지만 21로 이겼을때 uMoney : " + uMoney + ", bet: " + bet);
                                uMoney = uMoney-(bet/2);
                                youLose();
                            }


                        }
                    }.start();
                } else {
                    Toast.makeText(MainActivity.this, "카드를 최소2장 뒤집으세요", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    int setMax;
    int setBet;
    int betTxt;
    int uMoneyTxt;
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        //Log.i(TAG, "SeekBar 인입");
        sound.play(coin, 1.0F, 1.0F, 1, 0, 0.5F);
        setMax = getMoney;
        seekBar.setMax(setMax);
        setBet = betTxt+progress;

//        if(bet == 0){ //추후 시작 버튼으로 옴겨야 할 듯 하다. 최소 1원은 배팅해야한다는 로직이지만 SeekBar를 아예 안움직일경우 발생안함.
//            bet = 1;
//        }
        userBet.setText("Bet : $"+setBet); //배팅한 금액 표시
        userMoney.setText("$"+(setMax-progress)); //배팅한 금액 만큼 자금에서 마이너스 표시
        Log.i(TAG, "progressChanged의 uMoney: "+uMoney+", bet: "+bet);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
//        String betText = (String)userBet.getText();
//        String uMoneyText = (String)userMoney.getText();
//        betTxt = betAndMoney(betText);
//        uMoneyTxt = betAndMoney(uMoneyText);
//        seekBar.setProgress(bet);
//        userBet.setText("Bet: $" +bet);
//        userMoney.setText("$" +uMoney);
    }

    public void onStopTrackingTouch(SeekBar seekBar){
//        String betText = (String)userBet.getText();
//        String uMoneyText = (String)userMoney.getText();
//        int bet = betAndMoney(betText);
//        int uMoney = betAndMoney(uMoneyText);
//        seekBar.setProgress(bet);
//        userBet.setText("Bet: $" +bet);
//        userMoney.setText("$" +uMoney);

        //userBet.setText("Bet : $"+setBet); //배팅한 금액 표시
        //userMoney.setText("$"+(getMoney-setBet)); //배팅한 금액 만큼 자금에서 마이너스 표시
    }

    @Override
    public View makeView() {
        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setLayoutParams(new ImageSwitcher.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return iv;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "onPause()");
    }
    @Override
    public void onStop(){ //파일 저장 메서드 구현 필요
        super.onStop();
        Log.i(TAG, "onStop()");
        editor = setting.edit(); //셋팅의 에디트 메서드를 호출해서 editor에 저장함.
        editor.putInt("uMoney", uMoney); // 에디터에 인트값 저장
        editor.putInt("star", star);
        //editor.putInt("dMoney", dMoney);
        editor.commit(); // 데이터 베이스 커밋과 같은 역할.
    }

    public void dialog(){ //INSURANCE 배팅 구현 필요
        dialog.show();
        CountTask countTask = new CountTask();
        countTask.execute();
    }

    public void btnYes(){  //INSURANCE 배팅 구현 필요
        Log.i(TAG, "btnYes 인입");
        if(uMoney < bet/2){
            Toast.makeText(this, "INSURANCE 배팅할 충분한 돈이 없습니다.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }else{
            flag[11] = true; // flag[11]은 INSURANCE 적용 여부판단용.
            Toast.makeText(this, "INSURANCE 적용 완료 딜러가 21이 나온다면 draw !!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            int insurance = bet/2;
            uMoney = uMoney-insurance;
            bet = bet+insurance;
            userMoney.setText("$"+uMoney);
            userBet.setText("Bet:$"+bet);
        }
    }

    public void btnNo(){  //INSURANCE 배팅 구현 필요
        Log.i(TAG, "btnYes 인입");
        dialog.dismiss();

    }


    public void dbDialog(){  //DOUBLE BETTING 구현 필요
        Log.i(TAG, "dialog에 인입");

        Log.i(TAG, "flag[2]"+flag[2]);
        Log.i(TAG, "flag[3]"+flag[3]);
        Log.i(TAG, "flag[4]"+flag[4]);
        Log.i(TAG, "flag[0]"+flag[0]);
        Log.i(TAG, "flag[10]"+flag[10]);

        if(flag[0] && flag[1] && !flag[10] && !flag[2] && !flag[3] && !flag[4] && !flag[14]){
            flag[14] = true;
            dbDialog.show();
        }
    }

    public void dbBtnYes(){ //DOUBLE BETTING 구현 필요
        if(uMoney < bet){
            Toast.makeText(this, "DOUBLE 배팅할 충분한 돈이 없습니다.", Toast.LENGTH_SHORT).show();
            dbDialog.dismiss();
        }else{
            Toast.makeText(this, "DOUBLE 배팅 성공 한장의 카드만 더받을 수 있습니다.", Toast.LENGTH_SHORT).show();
            flag[9] = true; ///
            //Log.i(TAG, "더블 yes 버튼 uMoney: "+uMoney+", bet: "+bet);
            uMoney = uMoney-bet;
            bet = bet*2;
            userMoney.setText("$ "+uMoney);
            userBet.setText("Bet : $"+bet);
            dbDialog.dismiss();
        }
    }

    public void dbBtnNo(){  //DOUBLE BETTING 취소버튼
        dbDialog.dismiss();
    }

    public void EvenDialog(){
        evenDialog.show();
        CountTask countTask = new CountTask();
        countTask.execute();
    }

    public void evenBtnYes(){
        Toast.makeText(this, "딜러가 21이면 배당 1.5배 아니면 0.5배", Toast.LENGTH_SHORT).show();
        flag[12] = true; //evenmoney 상태확인
        dealerCard2();
        evenDialog.dismiss();
    }

    public void evenBtnNo(){
        evenDialog.dismiss();
    }

    class CountTask extends AsyncTask<Integer, Integer, Integer>{
        @Override
        protected Integer doInBackground(Integer... values) {
            while (isCancelled() == false){
                value--;
                if(value <= 0){
                    break;
                }else{
                    publishProgress(value);
                }
                try{
                    Thread.sleep(100);
                }catch(InterruptedException ex){}
            }
            return value;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            evPb.setProgress(values[0].intValue());
            pb.setProgress(values[0].intValue());
            txt_count.setText("남은 시간: "+values[0].intValue()/10+"초");
            even_count.setText("남은 시간: "+values[0].intValue()/10+"초");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            value = 100;
            pb.setMax(value);
            pb.setProgress(value);
            pb.setVisibility(View.VISIBLE);
            evPb.setMax(value);
            evPb.setProgress(value);
            evPb.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Integer result) {
            //Toast.makeText(MainActivity.this, "시간초과 다음기회에~", Toast.LENGTH_SHORT).show();
            pb.setProgress(100);
            evPb.setProgress(100);
            try{
                Thread.sleep(1300);
            }catch(InterruptedException ex){}
            dialog.dismiss();
            evenDialog.dismiss();


////            pb.setVisibility(View.GONE);
        }

    }


}

