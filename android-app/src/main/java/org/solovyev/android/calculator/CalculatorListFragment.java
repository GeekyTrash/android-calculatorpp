package org.solovyev.android.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockListFragment;
import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 03.10.12
 * Time: 14:18
 */
public abstract class CalculatorListFragment extends SherlockListFragment {

	@NotNull
	private final CalculatorFragmentHelper fragmentHelper;

	protected CalculatorListFragment(int layoutResId, int titleResId) {
		fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(layoutResId, titleResId);
	}

	protected CalculatorListFragment(@NotNull CalculatorFragmentType fragmentType) {
		fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(fragmentType.getDefaultLayoutId(), fragmentType.getDefaultTitleResId());
	}

	protected CalculatorListFragment(@NotNull CalculatorFragmentHelper fragmentHelper) {
		this.fragmentHelper = fragmentHelper;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fragmentHelper.onCreate(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return fragmentHelper.onCreateView(this, inflater, container);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		fragmentHelper.onViewCreated(this, view);
	}

	@Override
	public void onResume() {
		super.onResume();

		this.fragmentHelper.onResume(this);
	}

	@Override
	public void onPause() {
		this.fragmentHelper.onPause(this);

		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		fragmentHelper.onDestroy(this);
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}

