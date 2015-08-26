
package com.multimedia.room.fragment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommandManager;
import com.multimedia.room.CommonUtil;
import com.multimedia.room.ISwitch;
import com.multimedia.room.MediaMessage;
import com.multimedia.room.R;
import com.multimedia.room.SwitchManager;

public class ExamStandardFragment extends BaseFragment implements ISwitch {

    private TextView mStatusView, mExamInfoView, mExamTimeView,mExamOverView;
    private TextView mExamContentView;
    private String reveiver;
    private String params;
    private LinearLayout mAnswerView;
    private Map<Integer, String> mAnswerMap;
    private SwitchManager mSwitchManager;
    private EditText mAnswerEditText;
    private int mCount;
    private int mExamTime;
    private int mScore;
    private Button mSubmitBtn;
    private TimeCount mTime;

    private static final int COUNTDOWN_FLAG = 1;
    private ExamEntity mExamEntity;
    private TextView  mAnswerWriteLabel;
    private static final String EXAM_INFO = "exam_info";

    private static final String EXAM_CONTENT = "exam_content";
    private static MediaMessage mCurrentMessage;
    public static ExamStandardFragment newInstance(MediaMessage message) {
        mCurrentMessage = message;
        ExamStandardFragment fragment = new ExamStandardFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exam_standard_layout, null);
        mStatusView = (TextView) rootView
                .findViewById(R.id.exam_standard_status);
        mExamContentView = (TextView) rootView
                .findViewById(R.id.exam_standard_content);
        mExamInfoView = (TextView) rootView.findViewById(R.id.exam_info);
        mExamTimeView = (TextView) rootView.findViewById(R.id.exam_time);
        mAnswerView = (LinearLayout) rootView.findViewById(R.id.answer_layout);
        mAnswerWriteLabel = (TextView) rootView.findViewById(R.id.answer_write_alert);
        mAnswerEditText = (EditText) rootView.findViewById(R.id.answer_content);
        mSubmitBtn = (Button) rootView.findViewById(R.id.submit_btn);
        mAnswerWriteLabel.setVisibility(View.GONE);
        mAnswerEditText.setVisibility(View.GONE);
        mSubmitBtn.setVisibility(View.GONE);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 mExamOverView.setVisibility(View.VISIBLE);
			        StringBuilder selectAnswer = new StringBuilder();
			        Iterator iterator = mAnswerMap.keySet().iterator();
			        while (iterator.hasNext()) {
			            Object key = iterator.next();
			            String value = mAnswerMap.get(key);
			            selectAnswer.append(key).append(":").append(value).append(",");
			        }
			        selectAnswer.deleteCharAt(selectAnswer.lastIndexOf(","));

			        String textStr = mAnswerEditText.getText().toString();
			        String allAnswer = selectAnswer.toString() + "-" +"["+ textStr.trim()+"]";
			        CommandManager.sendStandardExamMessage(allAnswer);
				
			}
		});
        mExamOverView = (TextView) rootView.findViewById(R.id.exam_standard_over);
        mExamOverView.setVisibility(View.GONE);
        mAnswerMap = new HashMap<Integer, String>();
        mSwitchManager = SwitchManager.getInstance();
        mSwitchManager.registerExamSwitch(this);
        return rootView;
    }

    private void refreshView(ExamEntity examEntity) {
        if(isAdded()){
//            getResources().getString(R.string.app_name);
            String examInfo = getString(R.string.count) + " " + examEntity.getCount() + " "
                    + getString(R.string.score) + " " + examEntity.getScore()
                    + getString(R.string.time) + " " + examEntity.getTime()
                    + getString(R.string.minutes);
            mTime = new TimeCount(examEntity.getTime() * 60 * 1000, 1000);// 构造CountDownTimer对象
            mTime.start();
            mExamInfoView.setText(examInfo);
            for (int i = 1; i <= examEntity.getCount(); i++) {
                mAnswerMap.put(i, "null");
                View child = initView(i);
                mAnswerView.addView(child);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reveiver = mCurrentMessage.getReveiver();
        params = mCurrentMessage.getParams();
        new MyTask().execute(params, EXAM_INFO);
    }

    class MyTask extends AsyncTask<String, Integer, String> {

        private String obtainType;

        @Override
        protected String doInBackground(String... arg0) {
            String url = arg0[0];
            obtainType = arg0[1];
           if (TextUtils.isEmpty(url)) {
                return "";
            }
            String result = CommonUtil.getData(url, CommonUtil.CHARSET_GBK);
            return result;
        }

        protected void onPostExecute(String result) {
            if (!TextUtils.isEmpty(result)) {
                Log.d("result ", result);
                if (obtainType.equals(EXAM_INFO)) {

                    mExamEntity = parseExamFrom(new ByteArrayInputStream(result.getBytes()));
                    Log.d("exam", mExamEntity.toString());
                    mExamTime = mExamEntity.getTime() * 60;
                    Log.d("exam", "exam "+mExamEntity.toString() );
                    new MyTask().execute(mExamEntity.getContent(), EXAM_CONTENT);
                    refreshView(mExamEntity);
                } else if (obtainType.equals(EXAM_CONTENT)) {
                    mExamContentView.setText(result);
                    mAnswerWriteLabel.setVisibility(View.VISIBLE);
                    mAnswerEditText.setVisibility(View.VISIBLE);
                    mSubmitBtn.setVisibility(View.GONE);
                }
            }
        };
    };

    private RelativeLayout initView(final int id) {
        LayoutInflater inflater = LayoutInflater.from(this.getActivity());
        RelativeLayout childView = (RelativeLayout) inflater.inflate(
                R.layout.answer_item, null);
        childView.setId(id);
        TextView answerNo = (TextView) childView.findViewById(R.id.answer_no);
        RadioGroup group = (RadioGroup) childView
                .findViewById(R.id.answer_group);
        answerNo.setText(String.valueOf(id));
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                if (arg1 == R.id.item_a) {
                    mAnswerMap.put(id, "A");
                } else if (arg1 == R.id.item_b) {
                    mAnswerMap.put(id, "B");
                } else if (arg1 == R.id.item_c) {
                    mAnswerMap.put(id, "C");
                } else if (arg1 == R.id.item_d) {
                    mAnswerMap.put(id, "D");
                }
                Log.d("answer", "answer" + id + " " + mAnswerMap.get(id));
            }
        });
        return childView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSwitchManager.unregisterExamSwitch(this);

    }

    @Override
    public void switchOn() {

    }

    @Override
    public void switchOff() {
        mExamOverView.setVisibility(View.VISIBLE);
        StringBuilder selectAnswer = new StringBuilder();
        Iterator iterator = mAnswerMap.keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            String value = mAnswerMap.get(key);
            selectAnswer.append(key).append(":").append(value).append(",");
        }
        selectAnswer.deleteCharAt(selectAnswer.lastIndexOf(","));

        String textStr = mAnswerEditText.getText().toString();
        String allAnswer = selectAnswer.toString() + "-" +"["+ textStr.trim()+"]";
        CommandManager.sendStandardExamMessage(allAnswer);

    }


    private ExamEntity parseExamFrom(InputStream inputStream) {
        ExamEntity examEntity = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int event = parser.getEventType();// 产生第一个事件
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
                        break;
                    case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
                        Log.d("start_tag", parser.getName());
                        if ("exam".equals(parser.getName())) {// 判断开始标签元素是否是file
                            examEntity = new ExamEntity();
                        } else if ("title".equals(parser.getName())) {
                            examEntity.setTitle(parser.nextText());
                        } else if ("type".equals(parser.getName())) {
                            examEntity.setType(parser.nextText());
                        } else if ("count".equals(parser.getName())) {
                            examEntity.setCount(Integer.parseInt(parser.nextText()));
                        } else if ("time".equals(parser.getName())) {
                            examEntity.setTime(Integer.parseInt(parser.nextText()));
                        } else if ("score".equals(parser.getName())) {
                            examEntity.setScore(Integer.parseInt(parser.nextText()));
                        } else if ("content".equals(parser.getName())) {
                            examEntity.setContent(parser.nextText());
                        }

                        break;
                    case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
                        Log.d("end_tag", parser.getName());
                        break;
                }
                event = parser.next();// 进入下一个元素并触发相应事件
            }// end while
        } catch (Exception ex) {
            Log.d("purser error", "parser xml error!", ex);
        }
        return examEntity;
    }

    class ExamEntity {
        String title;
        String type;
        int count;
        int score;
        int time;
        String content;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "ExamEntity [title=" + title + ", type=" + type + ", count=" + count
                    + ", score=" + score + ", time=" + time + ", content=" + content + "]";
        }

    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTick(long arg0) {
            mExamTimeView.setText(CommonUtil.convertTime(arg0));

        }
    }
    
    @Override
	public void onDetach() {
		super.onDetach();
	}

}
