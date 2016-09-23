package ua.r4mstein.notepad;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DocumentListFragment extends Fragment {

    private TextInputLayout mNewFileNameTIL;
    private EditText mNewFileNameEditText;
    private FloatingActionButton mSaveFAB;

    private SimpleRecyclerAdapter mSimpleRecyclerAdapter;
    private RecyclerView mRecyclerView;

    private String[] mSavedFile;

    private PopupWindow mPopupWindow;
    private LayoutInflater mLayoutInflater;

    public DocumentListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_list, container, false);

        LinearLayout focusLayout = (LinearLayout) view.findViewById(R.id.focusLinearLayout);
        focusLayout.requestFocus();

        mNewFileNameEditText = (EditText) view.findViewById(R.id.newFileNameEditText);
        mNewFileNameTIL = (TextInputLayout) view.findViewById(R.id.TextInputLayoutDocList);
        mNewFileNameTIL.getEditText().addTextChangedListener(newFileNameListener);

        mSaveFAB = (FloatingActionButton) view.findViewById(R.id.saveFAB);
        mSaveFAB.setOnClickListener(saveFABClicked);
        updateSaveFAB();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewDocList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        listUpdate();
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                openFile(mSavedFile[position]);
            }

            @Override
            public void onLongClick(View view, final int position) {
                mLayoutInflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.popupLinearLayout);
                ViewGroup container = (ViewGroup) mLayoutInflater.inflate(R.layout.popup_window, linearLayout);

                mPopupWindow = new PopupWindow(container, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.showAtLocation(container, Gravity.CENTER_HORIZONTAL, 0, 0);

                Button popupButtonOk = (Button) container.findViewById(R.id.popupOkButton);
                popupButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMyFile(mSavedFile[position]);
                        mPopupWindow.dismiss();

                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                    }
                });
                Button popupButtonCancel = (Button) container.findViewById(R.id.popupCancelButton);
                popupButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupWindow.dismiss();
                    }
                });
            }
        }));

        return view;
    }

    private final TextWatcher newFileNameListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        // Кнопка saveFAB видна, если имя нового файла не пусто
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateSaveFAB();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void updateSaveFAB() {
        String input = mNewFileNameTIL.getEditText().getText().toString();

        if (input.trim().length() != 0)
            mSaveFAB.show();
        else
            mSaveFAB.hide();
    }

    private final View.OnClickListener saveFABClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String fileName = mNewFileNameEditText.getText().toString();
            String content = " ";

            FileOutputStream outputStream;

            try {
                outputStream = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(content.getBytes());

                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(getContext(), EditDocumentActivity.class);
            intent.putExtra("FILENAME", fileName);
            intent.putExtra("CONTENT", content);
            startActivity(intent);
        }
    };

    private void listUpdate() {

        mSavedFile = getActivity().getApplicationContext().fileList();

        if (mSimpleRecyclerAdapter == null) {
            mSimpleRecyclerAdapter = new SimpleRecyclerAdapter(mSavedFile);
            mRecyclerView.setAdapter(mSimpleRecyclerAdapter);
        } else {
            mSimpleRecyclerAdapter.notifyDataSetChanged();
        }
    }

    public void openFile(String file) {
        FileInputStream inputStream;
        String content = "";

        try {
            inputStream = getActivity().openFileInput(file);
            byte[] input = new byte[inputStream.available()];
            while (inputStream.read(input) != -1) {
                content += new String(input);
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getActivity().getApplicationContext(), EditDocumentActivity.class);
        intent.putExtra("FILENAME", file);
        intent.putExtra("CONTENT", content);
        startActivity(intent);
    }

    private void deleteMyFile(String file) {
        getActivity().deleteFile(file);
//        listUpdate();
        Toast.makeText(getActivity().getApplicationContext(), file + " удалён", Toast.LENGTH_SHORT).show();
    }

    public static interface ClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector mGestureDetector;
        private ClickListener mClickListner;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView,
                                     final ClickListener clickListener) {

            this.mClickListner = clickListener;

            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);

                    View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null && clickListener != null) {
                        clickListener.onLongClick(childView, recyclerView.getChildPosition(childView));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());

            if (childView != null && mClickListner != null && mGestureDetector.onTouchEvent(e)) {
                mClickListner.onClick(childView, mRecyclerView.getChildPosition(childView));
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

}
