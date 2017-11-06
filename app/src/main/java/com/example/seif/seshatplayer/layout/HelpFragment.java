package com.example.seif.seshatplayer.layout;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.seif.seshatplayer.MainActivity;
import com.example.seif.seshatplayer.R;
import com.example.seif.seshatplayer.model.Word;

import java.util.ArrayList;


public class HelpFragment extends Fragment {

    ImageButton helpiBtn, PrevlessoniBtn, CurrentlessoniBtn, AchievedlessoniBtn, PrevlessoniBtn_help, CurrentlessoniBtn_help, AchievedlessoniBtn_help;
    AlertDialog AchievedDialog;
    public static String HelpFragment_TAG ="HelpFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        helpiBtn = (ImageButton) getActivity().findViewById(R.id.imagebutton_moreInfo);
        helpiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).voiceoffer(helpiBtn, getActivity().getString(R.string.helpfragmenthelpbtninstr));
            }
        });

        PrevlessoniBtn = (ImageButton) view.findViewById(R.id.imagebutton_prevlesson);
        PrevlessoniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((MainActivity) getActivity()).updatelesson(-1, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("PrevlessoniBtn", e.toString());
                }
            }
        });
        CurrentlessoniBtn = (ImageButton) view.findViewById(R.id.imagebutton_currentlesson);
        CurrentlessoniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((MainActivity) getActivity()).updatelesson(0, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("CurrentlessoniBtn", e.toString());
                }
            }
        });
        AchievedlessoniBtn = (ImageButton) view.findViewById(R.id.imagebutton_achievedlessons);
        AchievedlessoniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> data_donewords = ((MainActivity) getActivity()).ReadArchiveWords();
                //  ArrayList<String> data_donewords =  ((MainActivity) getActivity()).getWords();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final AlertDialog dialog = builder.create();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.layout_listview, null);

                ListView listview_donewords = (ListView) dialogLayout.findViewById(R.id.listview);
                AchievedWordsListAdapter customAdapter = new AchievedWordsListAdapter(getActivity(), R.layout.layout_word_listview_item, data_donewords);
                listview_donewords.setAdapter(customAdapter);

                dialog.setView(dialogLayout);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.show();
                AchievedDialog = dialog;
            }
        });

        PrevlessoniBtn_help = (ImageButton) view.findViewById(R.id.imagebutton_prevlesson_help);
        PrevlessoniBtn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((MainActivity) getActivity()).voiceoffer(PrevlessoniBtn_help, getActivity().getString(R.string.backprevlesson));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("PrevlessoniBtn_help", e.toString());
                }
            }
        });
        CurrentlessoniBtn_help = (ImageButton) view.findViewById(R.id.imagebutton_currentlesson_help);
        CurrentlessoniBtn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((MainActivity) getActivity()).voiceoffer(CurrentlessoniBtn_help, getActivity().getString(R.string.backcurrentlesson));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("CurrentlessoniBtn_help", e.toString());
                }

            }
        });

        AchievedlessoniBtn_help = (ImageButton) view.findViewById(R.id.imagebutton_achievedlessons_help);
        AchievedlessoniBtn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((MainActivity) getActivity()).voiceoffer(AchievedlessoniBtn_help, getActivity().getString(R.string.urachievements));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("CurrentlessoniBtn_help", e.toString());
                }
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public class AchievedWordsListAdapter extends ArrayAdapter<String> {

        public AchievedWordsListAdapter(Context context, int resource, ArrayList<String> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.layout_word_listview_item, null);
            }

            String p = getItem(position);

            if (p != null) {
                TextView textView_word = (TextView) v.findViewById(R.id.textView_word_item_txt);
                ImageButton imageButton_sound_help = (ImageButton) v.findViewById(R.id.imageButton_word_item_soundhelp);
                ImageButton imageButton_photo_help = (ImageButton) v.findViewById(R.id.imageButton_word_item_photohelp);
                ImageButton imageButton_redo = (ImageButton) v.findViewById(R.id.imageButton_word_item_back_to);
                textView_word.setText(p);
                imageButton_photo_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("ArchiveListAdapter", getItem(position) + " imageButton_photo_help is clicked");
                        try {
                            ((MainActivity) getActivity()).helpbypic(imageButton_photo_help, getItem(position));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("imageButton_photo_help", e.toString());
                        }
                    }
                });
                imageButton_sound_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("ArchiveListAdapter", getItem(position) + " imageButton_sound_help is clicked");
                        try {
                            ((MainActivity) getActivity()).voiceoffer(imageButton_sound_help, getItem(position));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("imageButton_sound_help", e.toString());
                        }
                    }
                });
                imageButton_redo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("ArchiveListAdapter", getItem(position) + " imageButton_redo is clicked");
                        try {
                            AchievedDialog.dismiss();
                            AchievedDialog.cancel();
                            //
                            ((MainActivity) getActivity()).openLessonFragment(new Word(getItem(position)));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("imageButton_redo", e.toString());
                        }
                    }
                });
            }
            return v;
        }
    }
}
