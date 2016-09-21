package ua.r4mstein.notepad;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DocumentListFragment extends Fragment {

    private TextInputLayout mNewFileNameTIL;
    private EditText mNewFileNameEditText;
    private FloatingActionButton mSaveFAB;

    private SimpleRecyclerAdapter mSimpleRecyclerAdapter;
    private RecyclerView recyclerView;

    public DocumentListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_list, container, false);

        mNewFileNameEditText = (EditText) view.findViewById(R.id.newFileNameEditText);
        mNewFileNameTIL = (TextInputLayout) view.findViewById(R.id.TextInputLayoutDocList);
        mNewFileNameTIL.getEditText().addTextChangedListener(newFileNameListener);

        mSaveFAB = (FloatingActionButton) view.findViewById(R.id.saveFAB);
        mSaveFAB.setOnClickListener(saveFABClicked);
        updateSaveFAB();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewDocList);
        listUpdate();

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
            // Скрыть виртуальную клавиатуру
            ((InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    getView().getWindowToken(), 0);

            String fileName = mNewFileNameEditText.getText().toString();
            String content = "";

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
            startActivity(intent);
        }
    };

    private void listUpdate() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        String[] savedFile = getActivity().getApplicationContext().fileList();
        List<String> listDocuments = new ArrayList<>();
        Collections.addAll(listDocuments, savedFile);

        if (mSimpleRecyclerAdapter == null) {
            mSimpleRecyclerAdapter = new SimpleRecyclerAdapter(listDocuments);
        } else {
            mSimpleRecyclerAdapter.notifyDataSetChanged();
        }
    }

}
