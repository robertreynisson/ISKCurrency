package is.robertreynisson.iskcurrency.presenter_layer.Currency;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import is.robertreynisson.iskcurrency.ISKCurrency;
import is.robertreynisson.iskcurrency.R;
import is.robertreynisson.iskcurrency.domain_layer.CurrencyViewModel;
import is.robertreynisson.iskcurrency.presenter_layer.MainActivity;
import is.robertreynisson.iskcurrency.presenter_layer.MainToolbar;
import is.robertreynisson.iskcurrency.presenter_layer.models.Currency;
import is.robertreynisson.iskcurrency.utils.RxUIBinderUtil;

/**
 * Created by robert on 10.2.2016.
 */
public class CurrencyView extends FrameLayout {

    private static final String TAG = CurrencyView.class.getSimpleName();

    private CurrencyRecyclerAdapter currencyRecyclerAdpater;

    //A util that contains this views subscriptions
    //and maintains them throughout the lifecycle.
    private RxUIBinderUtil rxUIBinderUtil = new RxUIBinderUtil(this);

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.currency_view_recycler)
    public RecyclerView recyclerView;

    private CurrencyViewModel viewModel;

    public CurrencyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        progressBar.setVisibility(VISIBLE);
        swipeRefreshLayout.setVisibility(GONE);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            CurrencyRecyclerAdapter.compositeSubscription.clear();
            currencyRecyclerAdpater = null;
            MainActivity.reloadFragment();
        });
    }

    //This takes care of subscribing UI to the
    //viewModels observable objects via
    //the BinderUtil.
    public void setViewModel(CurrencyViewModel viewModel) {
        if (!MainActivity.isOnline()) {
            MainActivity.setOffLine();
        }
        this.viewModel = viewModel;
        rxUIBinderUtil.clear();
        if (this.viewModel != null) {
            rxUIBinderUtil.bindProperty(this.viewModel.getCurrencies(), this::updateUI);
            rxUIBinderUtil.bindProperty(this.viewModel.getTime(), MainToolbar::setTime);
        }
    }

    //This will be the method called by the observable
    //and is passed into the RxUIBinderUtil via this::updateUI
    //syntax (using Java 1.8 and RetroLambda Method Reference syntax)
    private void updateUI(List<Currency> currencyList) {
        progressBar.setVisibility(GONE);
        swipeRefreshLayout.setRefreshing(false);
        currencyRecyclerAdpater = new CurrencyRecyclerAdapter(currencyList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ISKCurrency.getInstance().getApplicationContext());
        this.recyclerView.invalidate();
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(this.currencyRecyclerAdpater);
        this.swipeRefreshLayout.setVisibility(VISIBLE);
    }
}
