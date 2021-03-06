package org.solovyev.android.calculator.plot;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.fragments.FragmentUtils;
import org.solovyev.common.msg.MessageType;

/**
 * User: serso
 * Date: 1/19/13
 * Time: 5:14 PM
 */
public class CalculatorPlotRangeActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cpp_dialog);

        FragmentUtils.createFragment(this, CalculatorPlotRangeFragment.class, R.id.dialog_layout, "plot-range");
    }

    public static class CalculatorPlotRangeFragment extends CalculatorFragment {

        public CalculatorPlotRangeFragment() {
            super(CalculatorFragmentType.plotter_range);
        }

        @Override
        public void onViewCreated(@NotNull View root, Bundle savedInstanceState) {
            super.onViewCreated(root, savedInstanceState);

            final CalculatorPlotter plotter = Locator.getInstance().getPlotter();

            final EditText xMinEditText = (EditText) root.findViewById(R.id.cpp_plot_range_x_min_editext);
            final EditText xMaxEditText = (EditText) root.findViewById(R.id.cpp_plot_range_x_max_editext);

            final PlotData plotData = plotter.getPlotData();
            final PlotBoundaries boundaries = plotData.getBoundaries();

            xMinEditText.setText(String.valueOf(boundaries.getXMin()));
            xMaxEditText.setText(String.valueOf(boundaries.getXMax()));

            root.findViewById(R.id.cpp_apply_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        final Float xMin = Float.valueOf(xMinEditText.getText().toString());
                        final Float xMax = Float.valueOf(xMaxEditText.getText().toString());

                        if ( xMin.equals(xMax) ) {
                            throw new IllegalArgumentException();
                        }

                        plotter.setPlotBoundaries(PlotBoundaries.newInstance(xMin, xMax));

                        CalculatorPlotRangeFragment.this.getActivity().finish();

                    } catch (IllegalArgumentException e) {
                        if (e instanceof NumberFormatException) {
                            Locator.getInstance().getNotifier().showMessage(R.string.cpp_invalid_number, MessageType.error);
                        } else {
                            Locator.getInstance().getNotifier().showMessage(R.string.cpp_plot_boundaries_should_differ, MessageType.error);
                        }
                    }
                }
            });
        }
    }
}

