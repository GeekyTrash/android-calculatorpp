package org.solovyev.android.calculator.function;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import jscl.text.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionParamsView extends LinearLayout {

	@NotNull
	private final MutableInt paramsCount = new MutableInt(0);

	@NotNull
	private final List<Integer> paramIds = new ArrayList<Integer>(10);

	private static final String PARAM_TAG_PREFIX = "function_param_";

	public FunctionParamsView(Context context) {
		super(context);
	}

	public FunctionParamsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FunctionParamsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void init() {
		init(Collections.<String>emptyList());
	}

	public void init(@NotNull List<String> parameters) {
		this.setOrientation(VERTICAL);

		final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View addParamView = inflater.inflate(R.layout.function_add_param, null);

		final View addParamButton = addParamView.findViewById(R.id.function_add_param_button);

		addParamButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addParam(null);
			}
		});

		this.addView(addParamView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		for (String parameter : parameters) {
			addParam(parameter);
		}
	}

	public void addParam(@Nullable String name) {
		synchronized (paramsCount) {
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			final Integer id = paramsCount.intValue();

			final View editParamView = inflater.inflate(R.layout.function_edit_param, null);

			editParamView.setTag(getParamTag(id));

            final EditText paramNameEditText = (EditText) editParamView.findViewById(R.id.function_param_edit_text);
            paramNameEditText.setText(name);

            final View removeParamButton = editParamView.findViewById(R.id.function_remove_param_button);
			removeParamButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					removeParam(id);
				}
			});

			final View upParamButton = editParamView.findViewById(R.id.function_up_param_button);
			upParamButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					upParam(id);
				}
			});

			final View downParamButton = editParamView.findViewById(R.id.function_down_param_button);
			downParamButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					downParam(id);
				}
			});

			this.addView(editParamView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

			paramIds.add(id);
			paramsCount.increment();
		}
	}

	private void downParam(@NotNull Integer id) {
		synchronized (paramsCount) {
			int index = paramIds.indexOf(id);
			if (index < paramIds.size() - 1) {
				swap(index, index + 1);
			}
		}
	}

	private void upParam(@NotNull Integer id) {
		synchronized (paramsCount) {
			int index = paramIds.indexOf(id);
			if (index > 0) {
				swap(index, index - 1);
			}
		}
	}

	private void swap(int index1, int index2) {
		final View editParamView1 = getParamView(paramIds.get(index1));
		final View editParamView2 = getParamView(paramIds.get(index2));

		if (editParamView1 != null && editParamView2 != null) {
			final EditText paramNameEditText1 = (EditText) editParamView1.findViewById(R.id.function_param_edit_text);
			final EditText paramNameEditText2 = (EditText) editParamView2.findViewById(R.id.function_param_edit_text);
			swap(paramNameEditText1, paramNameEditText2);
		}
	}

	private void swap(@NotNull TextView first,
					  @NotNull TextView second) {
		final CharSequence tmp = first.getText();
		first.setText(second.getText());
		second.setText(tmp);
	}

	@Nullable
	private View getParamView(@NotNull Integer id) {
		final String tag = getParamTag(id);
		return this.findViewWithTag(tag);
	}

	@NotNull
	 private String getParamTag(@NotNull Integer index) {
		return PARAM_TAG_PREFIX + index;
	}

	public void removeParam(@NotNull Integer id) {
		synchronized (paramsCount) {
			if (paramIds.contains(id)) {
				final View editParamView = getParamView(id);
				if (editParamView != null) {
					this.removeView(editParamView);
					paramIds.remove(id);
				}
			}
		}
	}

	@NotNull
	public List<String> getParameterNames() {
		synchronized (paramsCount) {
			final List<String> result = new ArrayList<String>(paramsCount.intValue());

			for (Integer id : paramIds) {
				final View paramView = getParamView(id);
				if ( paramView != null ) {
					final EditText paramNameEditText = (EditText) paramView.findViewById(R.id.function_param_edit_text);
					result.add(paramNameEditText.getText().toString());
				}
			}

			return result;
		}
	}

}
