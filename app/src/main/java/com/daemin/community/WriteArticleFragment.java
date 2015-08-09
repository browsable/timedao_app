package com.daemin.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daemin.common.BasicFragment;
import com.daemin.timetable.R;

/**
 * Created by Jun-yeong on 2015-08-09.
 */
public class WriteArticleFragment extends BasicFragment {
    private View root;
    private Button btWriteArticle;
    private EditText etArticleTitle, etArticleContent;
    private class mOnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (etArticleTitle.getText().length() <= 0){
                Toast.makeText(getActivity(), "제목을 입력하세요", Toast.LENGTH_LONG).show();
                return;
            }
            else if(etArticleContent.getText().length() <= 0){
                Toast.makeText(getActivity(), "내용을 입력하세요", Toast.LENGTH_LONG).show();
                return;
            }




        }
    }

    public WriteArticleFragment(){
        super(R.layout.fragment_write_article, "WriteArticleFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = super.onCreateView(inflater, container, savedInstanceState);

        if (layoutId > 0) {
            etArticleTitle = (EditText) root.findViewById(R.id.etArticleTitle);
            etArticleContent = (EditText) root.findViewById(R.id.etArticleContent);
            btWriteArticle = (Button) ((View)container.getParent().getParent().getParent().getParent()).findViewById(R.id.btWriteArticle);
            btWriteArticle.setText("확인");
            btWriteArticle.setOnClickListener(new mOnClick());


        }
        return root;
    }
}