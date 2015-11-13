package net.blaklizt.streets.android.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.USER_PREFERENCE;

import java.util.ArrayList;
import java.util.HashMap;

public class PreferenceUpdateDialogueListener implements DialogInterface.OnClickListener
{
    public class MultipleChoiceResponseListener implements DialogInterface.OnMultiChoiceClickListener
    {
        private MultipleChoiceResponseListener enableGPSOptionListener = new MultipleChoiceResponseListener();

        public MultipleChoiceResponseListener getInstance() { return enableGPSOptionListener; }

        @Override
        public void onClick(DialogInterface dialog, int option, boolean isChecked) {
            Log.i(TAG, "MultipleChoiceResponseListener onClick invoked.");
            Log.i(TAG, "Question: " + multipleChoiceQuestions.get(option));
            Log.i(TAG, "Setting preference: " + preferenceIndexes.get(option) + " = " + isChecked);
            AppContext.getStreetsCommon().setUserPreference(preferenceIndexes.get(option), isChecked ? "1" : "0");
        }
    }

	private static final String TAG = StreetsCommon.getTag(PreferenceUpdateDialogueListener.class);

	private AlertDialog.Builder alertBuilder;

	private HashMap<Integer, USER_PREFERENCE> preferenceIndexes = new HashMap<>();

	private ArrayList<String> multipleChoiceQuestions = new ArrayList<>();

	private ArrayList<Boolean> questionDefaults = new ArrayList<>();

	private String mainQuestion;
    private USER_PREFERENCE mainPreference;

	private ArrayList<DialogInterface.OnClickListener> additionalCallbacks = new ArrayList<>();

	public PreferenceUpdateDialogueListener(Context context, String question, USER_PREFERENCE preference) {
		this(context, question, preference, "Yes", "No");
	}

	public PreferenceUpdateDialogueListener(Context context, String question,
				USER_PREFERENCE preference, String positiveButtonText, String negativeButtonText) {
		this.mainQuestion = question;
		this.mainPreference = preference;
        alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(question).setPositiveButton(positiveButtonText, this).setNegativeButton(negativeButtonText, this);
	}

	public PreferenceUpdateDialogueListener addPreferenceQuestion(String question,
			USER_PREFERENCE prefToUpdateOnPositiveResponse, boolean isCheckedDefault)
	{
		multipleChoiceQuestions.add(question);
		questionDefaults.add(isCheckedDefault);
		preferenceIndexes.put(multipleChoiceQuestions.size() - 1, prefToUpdateOnPositiveResponse);
		return this;
	}

	public PreferenceUpdateDialogueListener setPositiveButtonText(String positiveButtonText) {
        alertBuilder.setPositiveButton(positiveButtonText, this);
		return this;
	}

	public PreferenceUpdateDialogueListener setNegativeButtonText(String negativeButtonText) {
        alertBuilder.setNegativeButton(negativeButtonText, this);
		return this;
	}

    public PreferenceUpdateDialogueListener addResponseCallback(DialogInterface.OnClickListener onClickListener) {
        additionalCallbacks.add(onClickListener);
        return this;
    }

	public PreferenceUpdateDialogueListener show() {
		CharSequence[] questions = multipleChoiceQuestions.toArray(new CharSequence[multipleChoiceQuestions.size()]);
		Boolean[] defaultResponses = questionDefaults.toArray(new Boolean[questionDefaults.size()]);
		if (questions.length > 0) {
			alertBuilder.setMultiChoiceItems(questions, toPrimitiveArray(defaultResponses), new MultipleChoiceResponseListener());
		}
		alertBuilder.create().show();
		return this;
	}

	@Override
	public void onClick(DialogInterface dialog, int selection) {
		Log.i(TAG, "PreferenceUpdateDialogueListener onClick invoked.");
		Log.i(TAG, "Response to question '" + mainQuestion + "' = " + selection);

        /* if option is linked to the dialogue, update it based on the response */
		if (mainPreference != null) {
			String newValue = (selection == 0 || selection == DialogInterface.BUTTON_POSITIVE) ? "1" : "0";
			Log.i(TAG, "Updating " + mainPreference + " to " + newValue);
			AppContext.getStreetsCommon().setUserPreference(mainPreference, newValue);
		}

        /* call any additional callbacks setup by the user */
		for (DialogInterface.OnClickListener callback : additionalCallbacks) {
			callback.onClick(dialog, selection);
		}
	}

	private boolean[] toPrimitiveArray(final Boolean[] booleanList) {
		final boolean[] primitives = new boolean[booleanList.length];
		for (int index = 0; index < booleanList.length; index++) {
			primitives[index] = booleanList[index];
		}
		return primitives;
	}

}
