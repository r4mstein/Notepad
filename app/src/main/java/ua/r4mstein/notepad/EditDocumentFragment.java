package ua.r4mstein.notepad;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

public class EditDocumentFragment extends Fragment {

    private EditText mEditText;
    private FloatingActionButton mSaveFAB;

    private String mFileName;

    public EditDocumentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_document, container, false);

        mEditText = (EditText) view.findViewById(R.id.fragmentEditDocEditText);

        String content = getActivity().getIntent().getExtras().getString("CONTENT");
        mFileName = getActivity().getIntent().getExtras().getString("FILENAME");

        mEditText.setText(content);
        mEditText.setSelection(mEditText.getText().length());

        mSaveFAB = (FloatingActionButton) view.findViewById(R.id.editDocSaveFAB);
        mSaveFAB.setOnClickListener(saveFABClicked);

        return view;
    }

    private final View.OnClickListener saveFABClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String content = mEditText.getText().toString();

            FileOutputStream outputStream;

            try {
                outputStream = getActivity().openFileOutput(mFileName, Context.MODE_PRIVATE);
                outputStream.write(content.getBytes());

                outputStream.close();
                Toast.makeText(getActivity().getApplicationContext(), "Файл " + mFileName + " сохранён",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };



}
